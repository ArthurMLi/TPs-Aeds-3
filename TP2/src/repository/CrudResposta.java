package repository;

import entities.Pergunta;
import entities.Resposta;
import files.ArquivoPergunta;
import files.ArquivoResposta;
import files.ArquivoUsuario;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Camada de controle da entidade Resposta.
 *
 * Como o CrudUsuario, nao faz entrada nem saida de dados: devolve null quando a
 * operacao deu certo e uma mensagem de erro quando alguma regra barrou.
 */
public class CrudResposta {

    private final ArquivoResposta arqRespostas;
    private ArquivoPergunta arqPerguntas;

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
     * referencial: so se cria resposta para um idUsuario que exista de fato.
     */
    public void setArquivoUsuario(ArquivoUsuario arqUsuarios) {
        arqRespostas.setArquivoUsuario(arqUsuarios);
    }

    public void setArquivoPergunta(ArquivoPergunta arqPerguntas) {
        this.arqPerguntas = arqPerguntas;
        arqRespostas.setArquivoPergunta(arqPerguntas);
    }

    // ------------------------------------------------------------------
    // Inclusao
    // ------------------------------------------------------------------

    /**
     * Inclui uma resposta. O usuario informa apenas o texto;
     * o idUsuario vem de quem esta logado, a data de criacao vem
     * do relogio, a nota e avaliacoes comecam em zero e a resposta nasce ativa.
     */
    public String criar(int idUsuario, int idPergunta, String texto) {
        if (idUsuario <= 0) {
            return "Usuario invalido.";
        }
        if (idPergunta <= 0) {
            return "Pergunta invalida.";
        }
        if (texto == null || texto.trim().isEmpty()) {
            return "O texto da resposta nao pode ser vazio.";
        }
        try {
            if (arqPerguntas != null) {
                Pergunta pergunta = arqPerguntas.read(idPergunta);
                if (pergunta == null) {
                    return "Pergunta nao encontrada.";
                }
                if (!pergunta.isAtiva()) {
                    return "Esta pergunta esta arquivada e nao aceita novas respostas.";
                }
            }
            arqRespostas.create(new Resposta(idPergunta, idUsuario, texto.trim()));
            return null;
        } catch (Exception e) {
            return "Nao foi possivel incluir a resposta: " + e.getMessage();
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

    private ArrayList<Resposta> filtrarAtivas(ArrayList<Resposta> lista) {
        ArrayList<Resposta> ativas = new ArrayList<>();
        for (Resposta r : lista) {
            if (r.isAtiva()) {
                ativas.add(r);
            }
        }
        return ativas;
    }

    /**
     * Devolve as respostas ativas de uma pergunta, ordenadas por nota decrescente
     * e, em caso de empate, por data de criacao.
     */
    public ArrayList<Resposta> listarPorPergunta(int idPergunta) {
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

    /**
     * Devolve todas as respostas ativas escritas pelo usuario.
     */
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
     * Arquiva a resposta (ativa = false). O arquivamento e permitido se quem solicitou
     * for o autor da resposta OU o autor da pergunta a que a resposta pertence.
     */
    public String arquivar(int id, int idUsuario) {
        try {
            Resposta resposta = arqRespostas.read(id);
            if (resposta == null) {
                return "Resposta nao encontrada.";
            }

            boolean isAutorResposta = (resposta.getIdUsuario() == idUsuario);
            boolean isAutorPergunta = false;
            if (arqPerguntas != null) {
                Pergunta p = arqPerguntas.read(resposta.getIdPergunta());
                if (p != null && p.getIdUsuario() == idUsuario) {
                    isAutorPergunta = true;
                }
            }

            if (!isAutorResposta && !isAutorPergunta) {
                return "Voce nao tem permissao para arquivar esta resposta.";
            }

            if (!resposta.isAtiva()) {
                return "Esta resposta ja esta arquivada.";
            }
            return arqRespostas.arquivar(id) ? null : "Nao foi possivel arquivar a resposta.";
        } catch (Exception e) {
            return "Nao foi possivel arquivar a resposta: " + e.getMessage();
        }
    }

    public void fechar() throws Exception {
        arqRespostas.close();
    }
}
