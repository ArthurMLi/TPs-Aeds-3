import files.ArquivoPergunta;
import files.ArquivoResposta;
import files.ArquivoUsuario;
import repository.CrudPergunta;
import repository.CrudResposta;
import repository.CrudUsuario;
import view.Console;
import view.MenuAcesso;
import view.MenuPrincipal;

/**
 * Ponto de entrada do sistema Ajuda Aí 1.0.
 *
 * Abre os dois arquivos de dados uma unica vez e os compartilha com as camadas
 * de controle e de visao. Os dois arquivos precisam se conhecer:
 *
 *  - o arquivo de perguntas consulta o de usuarios para garantir a integridade
 *    referencial (nao existe pergunta de um idUsuario inexistente);
 *  - o arquivo de usuarios precisa do de perguntas para a exclusao em cascata
 *    (excluir um usuario exclui todas as perguntas dele).
 */
public class Principal {

    public static void main(String[] args) {
        ArquivoUsuario arqUsuarios = null;
        ArquivoPergunta arqPerguntas = null;
        ArquivoResposta arqRespostas = null;

        try {
            arqUsuarios = new ArquivoUsuario();
            arqPerguntas = new ArquivoPergunta();
            arqRespostas = new ArquivoResposta();

            CrudUsuario crudUsuario = new CrudUsuario(arqUsuarios);
            CrudPergunta crudPergunta = new CrudPergunta(arqPerguntas);
            CrudResposta crudResposta = new CrudResposta(arqRespostas);

            // Liga os dois lados do relacionamento 1:N
            crudPergunta.setArquivoUsuario(arqUsuarios);
            crudUsuario.setArquivoPergunta(arqPerguntas);
            crudResposta.setArquivoUsuario(arqUsuarios);
            crudResposta.setArquivoPergunta(arqPerguntas);

            MenuPrincipal menuPrincipal = new MenuPrincipal(crudUsuario, crudPergunta,
                    crudResposta);
            new MenuAcesso(crudUsuario, menuPrincipal).mostrar();

        } catch (Exception e) {
            Console.erro("Erro fatal: " + e.getMessage());
            e.printStackTrace();
        } finally {
            fechar(arqUsuarios, arqPerguntas, arqRespostas);
        }
    }

    /** Fecha os arquivos para garantir que os buffers cheguem ao disco. */
    private static void fechar(ArquivoUsuario arqUsuarios, ArquivoPergunta arqPerguntas,
            ArquivoResposta arqRespostas) {
        try {
            if (arqPerguntas != null) {
                arqPerguntas.close();
            }
        } catch (Exception e) {
            Console.erro("Erro ao fechar o arquivo de perguntas: " + e.getMessage());
        }
        try {
            if (arqUsuarios != null) {
                arqUsuarios.close();
            }
        } catch (Exception e) {
            Console.erro("Erro ao fechar o arquivo de usuarios: " + e.getMessage());
        }
        try {
            if (arqRespostas != null) {
                arqRespostas.close();
            }
        } catch (Exception e) {
            Console.erro("Erro ao fechar o arquivo de respostas: " + e.getMessage());
        }
    }
}
