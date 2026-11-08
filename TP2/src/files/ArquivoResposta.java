package files;

import aed3.Arquivo;
import aed3.ArvoreBMais;
import aed3.ParIdId;
import entities.Resposta;
import java.util.ArrayList;

/**
 * Arquivo de respostas: a entidade intermediaria do relacionamento N:N entre
 * usuarios e perguntas (N usuarios respondem N perguntas).
 *
 * O relacionamento e mantido como dois 1:N, cada um em uma arvore B+:
 *  - (idPergunta, idResposta): as respostas de uma pergunta;
 *  - (idUsuario, idResposta): as respostas escritas por um usuario.
 */
public class ArquivoResposta extends Arquivo<Resposta> {

    ArvoreBMais<ParIdId> relPerguntaResposta;
    ArvoreBMais<ParIdId> relUsuarioResposta;

    /**
     * Referencias opcionais aos arquivos de usuarios e de perguntas, usadas
     * para garantir a integridade referencial na inclusao (nao se cria
     * resposta de um usuario ou para uma pergunta que nao existam).
     */
    private ArquivoUsuario arqUsuarios;
    private ArquivoPergunta arqPerguntas;

    /**
     * Referencia ao arquivo de avaliacoes, usada na exclusao fisica: apagar uma
     * resposta apaga em cascata as avaliacoes que ela recebeu.
     */
    private ArquivoAvaliacao arqAvaliacoes;

    public ArquivoResposta() throws Exception {
        super("respostas", Resposta.class.getConstructor());
        relPerguntaResposta = new ArvoreBMais<>(
                ParIdId.class.getConstructor(),
                5,
                "./dados/respostas/relPerguntaResposta.db");
        relUsuarioResposta = new ArvoreBMais<>(
                ParIdId.class.getConstructor(),
                5,
                "./dados/respostas/relUsuarioResposta.db");
    }

    public void setArquivoUsuario(ArquivoUsuario arqUsuarios) {
        this.arqUsuarios = arqUsuarios;
    }

    public void setArquivoPergunta(ArquivoPergunta arqPerguntas) {
        this.arqPerguntas = arqPerguntas;
    }

    public void setArquivoAvaliacao(ArquivoAvaliacao arqAvaliacoes) {
        this.arqAvaliacoes = arqAvaliacoes;
    }

    /**
     * Inclui a resposta no arquivo de dados e registra os pares
     * (idPergunta, idResposta) e (idUsuario, idResposta) nas duas arvores B+.
     */
    @Override
    public int create(Resposta resposta) throws Exception { //##########################################
        if (resposta == null) {
            throw new IllegalArgumentException("Resposta nao pode ser nula.");
        }

        if (arqUsuarios != null && arqUsuarios.read(resposta.getIdUsuario()) == null) {
            throw new Exception("Usuario " + resposta.getIdUsuario() + " nao existe.");
        }
        if (arqPerguntas != null && arqPerguntas.read(resposta.getIdPergunta()) == null) {
            throw new Exception("Pergunta " + resposta.getIdPergunta() + " nao existe.");
        }
        int id = super.create(resposta);
        relPerguntaResposta.create(new ParIdId(resposta.getIdPergunta(), id));
        relUsuarioResposta.create(new ParIdId(resposta.getIdUsuario(), id));
        return id;
    }

    /** Respostas ativas de uma pergunta, lidas pela arvore (idPergunta, idResposta). */
    public ArrayList<Resposta> readAllByPergunta(int idPergunta) throws Exception {
        return lerAtivas(relPerguntaResposta.read(new ParIdId(idPergunta, -1)));
    }

    /** Respostas ativas de um usuario, lidas pela arvore (idUsuario, idResposta). */
    public ArrayList<Resposta> readAllByUsuario(int idUsuario) throws Exception {
        return lerAtivas(relUsuarioResposta.read(new ParIdId(idUsuario, -1)));
    }

    private ArrayList<Resposta> lerAtivas(ArrayList<ParIdId> lista) throws Exception {
        ArrayList<Resposta> respostas = new ArrayList<>();
        if (lista != null) {
            for (ParIdId par : lista) {
                Resposta resposta = super.read(par.getId2());
                if (resposta != null && resposta.isAtiva()) {
                    respostas.add(resposta);
                }
            }
        }
        return respostas;
    }

    /**
     * Altera a resposta (na pratica, so a nota, o numero de avaliacoes e o
     * estado ativa mudam). idPergunta e idUsuario sao imutaveis: se mudassem,
     * os pares gravados nas arvores B+ ficariam apontando para a entidade
     * errada. A data de criacao tambem e preservada.
     */
    @Override
    public boolean update(Resposta resposta) throws Exception {
        Resposta atual = super.read(resposta.getId());
        if (atual == null) {
            return false;
        }
        if (atual.getIdPergunta() != resposta.getIdPergunta()
                || atual.getIdUsuario() != resposta.getIdUsuario()) {
            throw new Exception("Os relacionamentos de uma resposta nao podem ser alterados.");
        }
        resposta.setCriacao(atual.getCriacao());
        return super.update(resposta);
    }

    /**
     * Arquivar nao e excluir: a resposta continua no arquivo e nas arvores,
     * com ativa = false, e as avaliacoes que ela recebeu continuam validas.
     */
    public boolean arquivar(int id) throws Exception {
        Resposta resposta = super.read(id);
        if (resposta != null) {
            resposta.setAtiva(false);
            return this.update(resposta);
        }
        return false;
    }

    /**
     * Exclusao fisica: apaga primeiro as avaliacoes da resposta, depois os
     * pares das duas arvores e, por fim, o registro.
     */
    @Override
    public boolean delete(int id) throws Exception {
        Resposta resposta = super.read(id);
        if (resposta == null) {
            return false;
        }
        if (arqAvaliacoes != null) {
            arqAvaliacoes.deleteAllByResposta(id);
        }
        relPerguntaResposta.delete(new ParIdId(resposta.getIdPergunta(), id));
        relUsuarioResposta.delete(new ParIdId(resposta.getIdUsuario(), id));
        return super.delete(id);
    }

    /** Exclui todas as respostas (ativas ou arquivadas) de uma pergunta. */
    public void deleteAllByPergunta(int idPergunta) throws Exception {
        ArrayList<ParIdId> lista = relPerguntaResposta.read(new ParIdId(idPergunta, -1));
        if (lista != null) {
            for (ParIdId par : lista) {
                this.delete(par.getId2());
            }
        }
    }

    /** Exclui todas as respostas (ativas ou arquivadas) escritas por um usuario. */
    public void deleteAllByUsuario(int idUsuario) throws Exception {
        ArrayList<ParIdId> lista = relUsuarioResposta.read(new ParIdId(idUsuario, -1));
        if (lista != null) {
            for (ParIdId par : lista) {
                this.delete(par.getId2());
            }
        }
    }
}
