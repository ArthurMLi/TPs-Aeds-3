package view;

import entities.Usuario;
import repository.CrudPergunta;
import repository.CrudUsuario;

/**
 * Menu de mais alto nivel, exibido depois do login.
 *
 * Neste trabalho pratico so a area pessoal esta implementada; a busca por
 * perguntas de outros usuarios entra no proximo TP.
 */
public class MenuPrincipal {

    private final MenuMinhaArea menuMinhaArea;

    public MenuPrincipal(CrudUsuario crudUsuario, CrudPergunta crudPergunta) {
        this.menuMinhaArea = new MenuMinhaArea(crudUsuario, crudPergunta);
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
                    Console.erro("A busca por perguntas sera implementada no proximo trabalho pratico.");
                    Console.pausar();
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
