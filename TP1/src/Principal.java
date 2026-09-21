import files.ArquivoPergunta;
import files.ArquivoUsuario;
import repository.CrudPergunta;
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

        try {
            arqUsuarios = new ArquivoUsuario();
            arqPerguntas = new ArquivoPergunta();

            CrudUsuario crudUsuario = new CrudUsuario(arqUsuarios);
            CrudPergunta crudPergunta = new CrudPergunta(arqPerguntas);

            // Liga os dois lados do relacionamento 1:N
            crudPergunta.setArquivoUsuario(arqUsuarios);
            crudUsuario.setArquivoPergunta(arqPerguntas);

            MenuPrincipal menuPrincipal = new MenuPrincipal(crudUsuario, crudPergunta);
            new MenuAcesso(crudUsuario, menuPrincipal).mostrar();

        } catch (Exception e) {
            Console.erro("Erro fatal: " + e.getMessage());
            e.printStackTrace();
        } finally {
            fechar(arqUsuarios, arqPerguntas);
        }
    }

    /** Fecha os arquivos para garantir que os buffers cheguem ao disco. */
    private static void fechar(ArquivoUsuario arqUsuarios, ArquivoPergunta arqPerguntas) {
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
            Console.erro("Erro ao fechar o arquivo de usuários: " + e.getMessage());
        }
    }
}
