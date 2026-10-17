package repository;

import entities.Resposta;
import files.ArquivoPergunta;
import files.ArquivoResposta;
import files.ArquivoUsuario;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Camada de controle da entidade Pergunta.
 *
 * Como o CrudUsuario, nao faz entrada nem saida de dados: devolve null quando a
 * operacao deu certo e uma mensagem de erro quando alguma regra barrou.
 */
public class CrudResposta {

    private final ArquivoResposta arqRespostas;

    public CrudResposta() throws Exception {
        this.arqRespostas = new ArquivoResposta();
    }

    public CrudResposta(ArquivoResposta arqResposta) {
        this.arqRespostas = arqResposta;
    }

    public ArquivoResposta getArquivoResposta() {
        return arqRespostas;
    }

    /**
     * Liga o arquivo de usuarios para que a inclusao valide a integridade
     * referencial: so se cria pergunta para um idUsuario que exista de fato.
     */
    public void setArquivoUsuario(ArquivoUsuario arqUsuarios) {
        arqRespostas.setArquivoUsuario(arqUsuarios);
    }

    public void setArquivoPergunta(ArquivoPergunta arqPerguntas) {
        arqRespostas.setArquivoPergunta(arqPerguntas);
    }

    // ------------------------------------------------------------------
    // Inclusao
    // ------------------------------------------------------------------

    /**
     * Inclui uma pergunta. O usuario informa apenas o texto e as palavras-chave;
     * o idUsuario vem de quem esta logado, as datas de criacao e alteracao vem
     * do relogio, a nota comeca em zero e a pergunta nasce ativa.
     */
    public String criar(int idUsuario, int idPergunta, String texto) {
        if (idUsuario <= 0) {
            return "Usuario invalido.";
        }
        if (idPergunta <= 0) {
            return "Pergunta invalida.";
        }
        if (texto == null || texto.trim().isEmpty()) {
            return "O texto da pergunta nao pode ser vazio.";
        }
        try {
            arqRespostas.create(new Resposta(idPergunta, idUsuario, texto.trim()));
            return null;
        } catch (Exception e) {
            return "Nao foi possivel incluir a pergunta: " + e.getMessage();
        }
    }

    // ------------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------------

    public Resposta ler(int id) {
        try {
            return arqRespostas.read(id);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Devolve todas as perguntas do usuario, da mais antiga para a mais recente.
     * O caminho percorrido e: idUsuario -> arvore B+ -> lista de idPergunta ->
     * indice direto ID -> endereco -> registro no arquivo de dados.
     */

    private ArrayList<Resposta> filtrarAtivas(ArrayList<Resposta> lista) {
        ArrayList<Resposta> ativas = new ArrayList<Resposta>();
        for (Resposta r : lista) {
            if (r.isAtiva()) {
                ativas.add(r);
            }
        }
        return ativas;
    }

    public ArrayList<Resposta> listarPorResposta(int idPergunta) {
        try {
            ArrayList<Resposta> respostas = arqRespostas.readAllByPergunta(idPergunta);
            respostas = filtrarAtivas(respostas);
                respostas.sort(Comparator.comparingInt(Resposta::getNota).reversed()
                    .thenComparingLong(Resposta::getCriacao));

            return respostas;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public ArrayList<Resposta> listarPorUsuario(int idUsuario) {
        try {
            ArrayList<Resposta> respostas = arqRespostas.readAllByUsuario(idUsuario);
            respostas = filtrarAtivas(respostas);
            respostas.sort(Comparator.comparingLong(Resposta::getCriacao));

            return respostas;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // ------------------------------------------------------------------
    // Arquivamento e exclusao
    // ------------------------------------------------------------------

    /**
     * Arquiva a pergunta (ativa = false). Nao e exclusao: o registro continua
     * valido no arquivo e nos indices, porque outras entidades criadas por
     * outros usuarios (respostas, votos) dependem dela. O arquivamento e
     * definitivo: nao existe desarquivar.
     */
    public String arquivar(int id, int idUsuario) {
        try {
            Resposta resposta = arqRespostas.read(id);
            if (resposta == null) {
                return "Pergunta nao encontrada.";
            }
            if (resposta.getIdUsuario() != idUsuario) {
                return "Esta pergunta pertence a outro usuario.";
            }
            if (!resposta.isAtiva()) {
                return "Esta pergunta ja esta arquivada.";
            }
            return arqRespostas.arquivar(id) ? null : "Nao foi possivel arquivar a pergunta.";
        } catch (Exception e) {
            return "Nao foi possivel arquivar a pergunta: " + e.getMessage();
        }
    }

    public void fechar() throws Exception {
        arqRespostas.close();
    }
}
