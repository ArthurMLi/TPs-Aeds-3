package view;

import entities.Usuario;
import repository.CrudPergunta;
import repository.CrudUsuario;

/**
 * Area pessoal do usuario logado.
 *
 * Respostas e votos aparecem no menu, como pede o enunciado, mas pertencem aos
 * proximos trabalhos praticos.
 */
public class MenuMinhaArea {

    private final MenuMeusDados menuMeusDados;
    private final MenuPerguntas menuPerguntas;

    public MenuMinhaArea(CrudUsuario crudUsuario, CrudPergunta crudPergunta) {
        this.menuMeusDados = new MenuMeusDados(crudUsuario);
        this.menuPerguntas = new MenuPerguntas(crudPergunta);
    }

    /** Devolve false quando o usuario excluiu a propria conta e precisa deslogar. */
    public void mostrar(Usuario usuario) {
        char opcao;
        do {
            Console.cabecalho("Inicio > Minha area");
            Console.mensagem("(A) Meus dados");
            Console.mensagem("(B) Minhas perguntas");
            Console.mensagem("(C) Minhas respostas");
            Console.mensagem("(D) Meus votos");
            Console.mensagem("(R) Retornar ao menu anterior");
            Console.mensagem("");
            opcao = Console.lerOpcao();

            switch (opcao) {
                case 'A':
                    menuMeusDados.mostrar(usuario);
                    break;
                case 'B':
                    menuPerguntas.mostrar(usuario);
                    break;
                case 'C':
                    Console.erro("As respostas serao implementadas no proximo trabalho pratico.");
                    Console.pausar();
                    break;
                case 'D':
                    Console.erro("Os votos serao implementados em um trabalho pratico posterior.");
                    Console.pausar();
                    break;
                case 'R':
                    break;
                default:
                    Console.opcaoInvalida();
            }
        } while (opcao != 'R');
    }
}
