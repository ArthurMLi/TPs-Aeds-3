package files;

import aed3.Arquivo;
import aed3.ArvoreBMais;
import aed3.ParIdId;
import entities.Avaliacao;
import entities.Resposta;
import java.util.ArrayList;

public class ArquivoAvaliacao extends Arquivo<Avaliacao> {

    ArvoreBMais<ParIdId> relUsuarioAvaliacao;
    ArvoreBMais<ParIdId> relRespostaAvaliacao;

    /**
     * Referencias opcionais aos arquivos de usuarios e respostas, usadas para
     * garantir a integridade referencial na inclusao.
     */
    private ArquivoUsuario arqUsuarios;
    private ArquivoResposta arqRespostas;

    public ArquivoAvaliacao() throws Exception {
        super("avaliacoes", Avaliacao.class.getConstructor());
        relUsuarioAvaliacao = new ArvoreBMais<>(
                ParIdId.class.getConstructor(),
                5,
                "./dados/avaliacoes/relUsuarioAvaliacao.db");
        relRespostaAvaliacao = new ArvoreBMais<>(
                ParIdId.class.getConstructor(),
                5,
                "./dados/avaliacoes/relRespostaAvaliacao.db");
    }

    public void setArquivoUsuario(ArquivoUsuario arqUsuarios) {
        this.arqUsuarios = arqUsuarios;
    }

    public void setArquivoResposta(ArquivoResposta arqRespostas) {
        this.arqRespostas = arqRespostas;
    }

    /**
     * Inclui a avaliacao no arquivo de dados e registra os pares
     * (idUsuario, idAvaliacao) e (idResposta, idAvaliacao) nas duas arvores B+,
     * mantendo o relacionamento N:N consultavel a partir de ambos os lados.
     */
    @Override
    public int create(Avaliacao avaliacao) throws Exception {
        if (avaliacao == null) {
            throw new IllegalArgumentException("Avaliacao nao pode ser nula.");
        }

        if (arqUsuarios != null && arqUsuarios.read(avaliacao.getIdUsuario()) == null) {
            throw new Exception("Usuario " + avaliacao.getIdUsuario() + " nao existe.");
        }
        if (arqRespostas != null && arqRespostas.read(avaliacao.getIdResposta()) == null) {
            throw new Exception("Resposta " + avaliacao.getIdResposta() + " nao existe.");
        }
        int id = super.create(avaliacao);
        relUsuarioAvaliacao.create(new ParIdId(avaliacao.getIdUsuario(), id));
        relRespostaAvaliacao.create(new ParIdId(avaliacao.getIdResposta(), id));
        return id;
    }

    /**
     * Busca a avaliacao que um usuario fez em uma determinada resposta.
     * Retorna a entidade Avaliacao se existir, ou null caso contrario.
     */
    public Avaliacao readByUsuarioEResposta(int idUsuario, int idResposta) throws Exception {
        ArrayList<ParIdId> lista = relUsuarioAvaliacao.read(new ParIdId(idUsuario, -1));
        if (lista != null) {
            for (ParIdId par : lista) {
                Avaliacao av = super.read(par.getId2());
                if (av != null && av.getIdResposta() == idResposta) {
                    return av;
                }
            }
        }
        return null;
    }

    public ArrayList<Avaliacao> readAllByUsuario(int idUsuario) throws Exception {
        ArrayList<ParIdId> lista = relUsuarioAvaliacao.read(new ParIdId(idUsuario, -1));
        ArrayList<Avaliacao> avaliacoes = new ArrayList<>();

        if (lista != null) {
            for (int i = lista.size() - 1; i >= 0; i--) {
                ParIdId indice = lista.get(i);
                Avaliacao avaliacao = super.read(indice.getId2());
                if (avaliacao != null) {
                    avaliacoes.add(avaliacao);
                }
            }
        }
        return avaliacoes;
    }

    public ArrayList<Avaliacao> readAllByResposta(int idResposta) throws Exception {
        ArrayList<ParIdId> lista = relRespostaAvaliacao.read(new ParIdId(idResposta, -1));
        ArrayList<Avaliacao> avaliacoes = new ArrayList<>();

        if (lista != null) {
            for (int i = lista.size() - 1; i >= 0; i--) {
                ParIdId indice = lista.get(i);
                Avaliacao avaliacao = super.read(indice.getId2());
                if (avaliacao != null) {
                    avaliacoes.add(avaliacao);
                }
            }
        }
        return avaliacoes;
    }

    @Override
    public boolean update(Avaliacao avaliacao) throws Exception {
        Avaliacao atual = super.read(avaliacao.getId());
        if (atual == null) {
            return false;
        }
        if (atual.getIdResposta() != avaliacao.getIdResposta()
                || atual.getIdUsuario() != avaliacao.getIdUsuario()) {
            throw new Exception("Os relacionamentos de uma avaliacao nao podem ser alterados.");
        }
        return super.update(avaliacao);
    }

    @Override
    public boolean delete(int id) throws Exception {
        Avaliacao avaliacao = super.read(id);
        if (avaliacao == null) {
            return false;
        }
        relUsuarioAvaliacao.delete(new ParIdId(avaliacao.getIdUsuario(), id));
        relRespostaAvaliacao.delete(new ParIdId(avaliacao.getIdResposta(), id));
        return super.delete(id);
    }

    public void deleteAllByResposta(int idResposta) throws Exception {
        ArrayList<ParIdId> lista = relRespostaAvaliacao.read(new ParIdId(idResposta, -1));
        if (lista != null) {
            for (ParIdId parResposta : lista) {
                int idAvaliacao = parResposta.getId2();
                Avaliacao avaliacao = super.read(idAvaliacao);
                if (avaliacao != null) {
                    relUsuarioAvaliacao.delete(new ParIdId(avaliacao.getIdUsuario(), idAvaliacao));
                    super.delete(idAvaliacao);
                }
                relRespostaAvaliacao.delete(parResposta);
            }
        }
    }

    /**
     * Exclui todas as avaliacoes feitas por um usuario. Como as respostas
     * avaliadas continuam existindo, a nota e o numero de avaliacoes de cada
     * uma sao revertidos antes de a avaliacao ser apagada.
     */
    public void deleteAllByUsuario(int idUsuario) throws Exception {
        ArrayList<ParIdId> lista = relUsuarioAvaliacao.read(new ParIdId(idUsuario, -1));
        if (lista != null) {
            for (ParIdId parUsuario : lista) {
                int idAvaliacao = parUsuario.getId2();
                Avaliacao avaliacao = super.read(idAvaliacao);
                if (avaliacao != null) {
                    if (arqRespostas != null) {
                        Resposta resposta = arqRespostas.read(avaliacao.getIdResposta());
                        if (resposta != null) {
                            resposta.setNota((short) (resposta.getNota()
                                    - (avaliacao.isAvaliacao() ? 1 : -1)));
                            if (resposta.getAvaliacoes() > 0) {
                                resposta.setAvaliacoes((short) (resposta.getAvaliacoes() - 1));
                            }
                            arqRespostas.update(resposta);
                        }
                    }
                    relRespostaAvaliacao.delete(new ParIdId(avaliacao.getIdResposta(), idAvaliacao));
                    super.delete(idAvaliacao);
                }
                relUsuarioAvaliacao.delete(parUsuario);
            }
        }
    }
}
