package view;

import entities.Usuario;
import repository.CrudAvaliacao;
import repository.CrudPergunta;
import repository.CrudResposta;
import repository.CrudUsuario;

/**
 * Area pessoal do usuario logado.
 *
 * Permite gerenciar dados pessoais, perguntas, respostas e avaliacoes realizadas.
 */
public class MenuMinhaArea {

    private final MenuMeusDados menuMeusDados;
    private final MenuPerguntas menuPerguntas;
    private final MenuMinhasRespostas menuMinhasRespostas;
    private final MenuMinhasAvaliacoes menuMinhasAvaliacoes;

    public MenuMinhaArea(CrudUsuario crudUsuario, CrudPergunta crudPergunta,
            CrudResposta crudResposta, CrudAvaliacao crudAvaliacao) {
        this.menuMeusDados = new MenuMeusDados(crudUsuario);
        this.menuPerguntas = new MenuPerguntas(crudUsuario, crudPergunta, crudResposta,
            crudAvaliacao);
        this.menuMinhasRespostas = new MenuMinhasRespostas(crudUsuario, crudPergunta, crudResposta);
        this.menuMinhasAvaliacoes = new MenuMinhasAvaliacoes(crudUsuario, crudPergunta, crudResposta, crudAvaliacao);
    }

    public MenuMinhaArea(CrudUsuario crudUsuario, CrudPergunta crudPergunta) {
        this(crudUsuario, crudPergunta, null, null);
    }

    public void mostrar(Usuario usuario) {
        char opcao;
        do {
            Console.cabecalho("Início > Minha área");
            Console.mensagem("(A) Meus dados");
            Console.mensagem("(B) Minhas perguntas");
            Console.mensagem("(C) Minhas respostas");
            Console.mensagem("(D) Minhas avaliações");
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
                    menuMinhasRespostas.mostrar(usuario);
                    break;
                case 'D':
                    menuMinhasAvaliacoes.mostrar(usuario);
                    break;
                case 'R':
                    break;
                default:
                    Console.opcaoInvalida();
            }
        } while (opcao != 'R');
    }
}
