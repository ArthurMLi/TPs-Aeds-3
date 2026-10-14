package view;

import entities.Usuario;
import repository.CrudPergunta;
import repository.CrudUsuario;

/**
 * Menu de mais alto nivel, exibido depois do login.
 *
 * A busca por perguntas e feita pela area publica do sistema.
 */
public class MenuPrincipal {

    private final MenuMinhaArea menuMinhaArea;
    private final MenuBuscarPerguntas menuBuscarPerguntas;

    public MenuPrincipal(CrudUsuario crudUsuario, CrudPergunta crudPergunta) {
        this.menuMinhaArea = new MenuMinhaArea(crudUsuario, crudPergunta);
        this.menuBuscarPerguntas = new MenuBuscarPerguntas(crudUsuario, crudPergunta);
    }

    /** O usuario logado e passado adiante como o "dono" de tudo que for exibido. */
    public void mostrar(Usuario usuario) {
        char opcao;
        do {
            Console.cabecalho("Inicio");
            Console.mensagem("(A) Minha area");
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
