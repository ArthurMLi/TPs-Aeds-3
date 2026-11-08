import files.ArquivoAvaliacao;
import files.ArquivoPergunta;
import files.ArquivoResposta;
import files.ArquivoUsuario;
import repository.CrudAvaliacao;
import repository.CrudPergunta;
import repository.CrudResposta;
import repository.CrudUsuario;
import view.Console;
import view.MenuAcesso;
import view.MenuPrincipal;

/**
 * Ponto de entrada do sistema Ajuda Aí 1.0.
 *
 * Abre os quatro arquivos de dados uma unica vez e os compartilha com as
 * camadas de controle e de visao. Os arquivos precisam se conhecer:
 *
 *  - perguntas, respostas e avaliacoes consultam os arquivos de que dependem
 *    para garantir a integridade referencial na inclusao (nao existe resposta
 *    de uma pergunta inexistente, nem avaliacao de uma resposta inexistente);
 *  - na exclusao fisica o caminho e o inverso, em cascata: usuario -> perguntas
 *    -> respostas -> avaliacoes, para que nenhum indice fique apontando para
 *    um ID que nao existe mais.
 */
public class Principal {

    public static void main(String[] args) {
        ArquivoUsuario arqUsuarios = null;
        ArquivoPergunta arqPerguntas = null;
        ArquivoResposta arqRespostas = null;
        ArquivoAvaliacao arqAvaliacoes = null;

        try {
            arqUsuarios = new ArquivoUsuario();
            arqPerguntas = new ArquivoPergunta();
            arqRespostas = new ArquivoResposta();
            arqAvaliacoes = new ArquivoAvaliacao();

            CrudUsuario crudUsuario = new CrudUsuario(arqUsuarios);
            CrudPergunta crudPergunta = new CrudPergunta(arqPerguntas);
            CrudResposta crudResposta = new CrudResposta(arqRespostas);
            CrudAvaliacao crudAvaliacao = new CrudAvaliacao(arqAvaliacoes);

            // Relacionamento 1:N usuario -> perguntas (TP1)
            crudPergunta.setArquivoUsuario(arqUsuarios);
            crudUsuario.setArquivoPergunta(arqPerguntas);

            // Relacionamento N:N usuario <-> pergunta, por meio das respostas
            crudResposta.setArquivoUsuario(arqUsuarios);
            crudResposta.setArquivoPergunta(arqPerguntas);
            arqPerguntas.setArquivoResposta(arqRespostas);

            // Relacionamento N:N usuario <-> resposta, por meio das avaliacoes
            crudAvaliacao.setArquivoUsuario(arqUsuarios);
            crudAvaliacao.setArquivoResposta(arqRespostas);
            arqRespostas.setArquivoAvaliacao(arqAvaliacoes);

            // A exclusao de um usuario apaga tambem as respostas e avaliacoes dele
            crudUsuario.setArquivoResposta(arqRespostas);
            crudUsuario.setArquivoAvaliacao(arqAvaliacoes);

            MenuPrincipal menuPrincipal = new MenuPrincipal(crudUsuario, crudPergunta,
                    crudResposta, crudAvaliacao);
            new MenuAcesso(crudUsuario, menuPrincipal).mostrar();

        } catch (Exception e) {
            Console.erro("Erro fatal: " + e.getMessage());
            e.printStackTrace();
        } finally {
            fechar(arqUsuarios, arqPerguntas, arqRespostas, arqAvaliacoes);
        }
    }

    /** Fecha os arquivos para garantir que os buffers cheguem ao disco. */
    private static void fechar(ArquivoUsuario arqUsuarios, ArquivoPergunta arqPerguntas,
            ArquivoResposta arqRespostas, ArquivoAvaliacao arqAvaliacoes) {
        try {
            if (arqAvaliacoes != null) {
                arqAvaliacoes.close();
            }
        } catch (Exception e) {
            Console.erro("Erro ao fechar o arquivo de avaliacoes: " + e.getMessage());
        }
        try {
            if (arqRespostas != null) {
                arqRespostas.close();
            }
        } catch (Exception e) {
            Console.erro("Erro ao fechar o arquivo de respostas: " + e.getMessage());
        }
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
    }
}
