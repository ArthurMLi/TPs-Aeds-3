package files;

import aed3.Arquivo;
import aed3.ArvoreBMais;
import aed3.ParIdId;
import entities.Resposta;
import java.util.ArrayList;

public class ArquivoResposta extends Arquivo<Resposta> {

    ArvoreBMais<ParIdId> relPerguntaResposta;
    ArvoreBMais<ParIdId> relUsuarioResposta;

    /**
     * Referencia opcional ao arquivo de usuarios, usada apenas para garantir a
     * integridade referencial na inclusao (nao se cria pergunta apontando para
     * um idUsuario que nao existe).
     */
    private ArquivoUsuario arqUsuarios;
    private ArquivoPergunta arqPerguntas;

    public ArquivoResposta() throws Exception {
        super("respostas", Resposta.class.getConstructor());
        relPerguntaResposta = new ArvoreBMais<>(
                ParIdId.class.getConstructor(),
                5,
                "./dados/respostas/relPerguntaResposta.db");
        relUsuarioResposta = new ArvoreBMais<>(
                ParIdId.class.getConstructor(),
                5,
                "./dados/respostas/relUsuarioPPergunta.db");
    }

    public void setArquivoUsuario(ArquivoUsuario arqUsuarios) {
        this.arqUsuarios = arqUsuarios;
    }

    public void setArquivoPergunta(ArquivoPergunta arqPerguntas) {
        this.arqPerguntas = arqPerguntas;
    }

    /**
     * Inclui a pergunta no arquivo de dados e registra o par
     * (idUsuario, idPergunta) na arvore B+, que e o que permite responder
     * "quais sao as perguntas deste usuario" sem varrer o arquivo inteiro.
     */
    @Override
    public int create(Resposta resposta) throws Exception {
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

    // public Pergunta[] readAllByUsuario(int idUsuario) throws Exception {
    // ArrayList<ParIdId> lista = relUsuarioPergunta.read(new ParIdId(idUsuario,
    // -1));
    // if (lista == null) {
    // return new Pergunta[0];
    // }
    //
    // ArrayList<Pergunta> aux = new ArrayList<>();
    // for (int i = 0; i < lista.size(); i++) {
    // ParIdId par = lista.get(i);
    // Pergunta pergunta = super.read(par.getId2());
    // if (pergunta != null) {
    // aux.add(pergunta);
    // }
    // }
    //
    // Pergunta[] resp = new Pergunta[aux.size()];
    // for (int i = 0; i < aux.size(); i++) {
    // resp[i] = aux.get(i);
    // }
    //
    // return resp;
    // }

    public ArrayList<Resposta> readAllByPergunta(int idPergunta) throws Exception {
        ArrayList<ParIdId> lista = relPerguntaResposta.read(new ParIdId(idPergunta, -1));
        ArrayList<Resposta> respostas = new ArrayList<>();

        if (lista != null) {
            for (int i = lista.size() - 1; i >= 0; i--) {
                ParIdId indice = lista.get(i);
                Resposta resposta = super.read(indice.getId2());
                if (resposta != null && resposta.isAtiva()) {
                    respostas.add(resposta);
                }
            }
        }
        return respostas;
    }
    public ArrayList<Resposta> readAllByUsuario(int idUsuario) throws Exception {
        ArrayList<ParIdId> lista = relUsuarioResposta.read(new ParIdId(idUsuario, -1));
        ArrayList<Resposta> respostas = new ArrayList<>();

        if (lista != null) {
            for (int i = lista.size() - 1; i >= 0; i--) {
                ParIdId indice = lista.get(i);
                Resposta resposta = super.read(indice.getId2());
                if (resposta != null && resposta.isAtiva()) {
                    respostas.add(resposta);
                }
            }
        }
        return respostas;
    }

    /**
     * Altera a pergunta e carimba a data/hora de alteracao.
     * O idUsuario e imutavel: se ele pudesse mudar, o par gravado na arvore B+
     * ficaria apontando para o usuario errado e a listagem quebraria.
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

    public boolean arquivar(int id) throws Exception {
        Resposta resposta = super.read(id);
        if (resposta != null) {
            resposta.setAtiva(false);
            return this.update(resposta);
        }
        return false;
    }

    @Override
    public boolean delete(int id) throws Exception {
        Resposta resposta = super.read(id);
        if (resposta == null) {
            return false;
        }
        relPerguntaResposta.delete(new ParIdId(resposta.getIdPergunta(), id));
        relUsuarioResposta.delete(new ParIdId(resposta.getIdUsuario(), id));
        return super.delete(id);
    }
}