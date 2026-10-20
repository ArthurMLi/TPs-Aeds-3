package view;

import entities.Usuario;
import repository.CrudAvaliacao;
import repository.CrudPergunta;
import repository.CrudResposta;
import repository.CrudUsuario;

/**
 * Menu de mais alto nivel, exibido depois do login.
 *
 * A busca por perguntas e feita pela area publica do sistema.
 */
public class MenuPrincipal {

    private final MenuMinhaArea menuMinhaArea;
    private final MenuBuscarPerguntas menuBuscarPerguntas;

    public MenuPrincipal(CrudUsuario crudUsuario, CrudPergunta crudPergunta,
            CrudResposta crudResposta) {
        this(crudUsuario, crudPergunta, crudResposta, null);
    }

    public MenuPrincipal(CrudUsuario crudUsuario, CrudPergunta crudPergunta,
            CrudResposta crudResposta, CrudAvaliacao crudAvaliacao) {
        this.menuMinhaArea = new MenuMinhaArea(crudUsuario, crudPergunta, crudResposta, crudAvaliacao);
        this.menuBuscarPerguntas = new MenuBuscarPerguntas(crudUsuario, crudPergunta, crudResposta, crudAvaliacao);
    }

    /** O usuario logado e passado adiante como o "dono" de tudo que for exibido. */
    public void mostrar(Usuario usuario) {
        char opcao;
        do {
            Console.cabecalho("Início");
            Console.mensagem("(A) Minha área");
            Console.mensagem("(B) Buscar perguntas");
            Console.mensagem("(S) Sair");
            Console.mensagem("");
            opcao = Console.lerOpcao();

            switch (opcao) {
                case 'A':
                    menuMinhaArea.mostrar(usuario);
                    break;
                case 'B':
                    menuBuscarPerguntas.mostrar(usuario);
                    break;
                case 'S':
                    Console.mensagem("\nSessao encerrada.");
                    break;
                default:
                    Console.opcaoInvalida();
            }
        } while (opcao != 'S');
    }
}
