package view;

import entities.Usuario;
import repository.CrudAvaliacao;
import repository.CrudPergunta;
import repository.CrudResposta;
import repository.CrudUsuario;

public class MenuBuscarPerguntas {

    private final CrudUsuario crudUsuario;
    private final MenuPerguntasMaisRecentes menuPerguntasMaisRecentes;

    public MenuBuscarPerguntas(CrudUsuario crudUsuario, CrudPergunta crudPergunta,
            CrudResposta crudResposta) {
        this(crudUsuario, crudPergunta, crudResposta, null);
    }

    public MenuBuscarPerguntas(CrudUsuario crudUsuario, CrudPergunta crudPergunta,
            CrudResposta crudResposta, CrudAvaliacao crudAvaliacao) {
        this.crudUsuario = crudUsuario;
        this.menuPerguntasMaisRecentes =
                new MenuPerguntasMaisRecentes(crudUsuario, crudPergunta, crudResposta, crudAvaliacao);
    }

    public void mostrar(Usuario usuario) {
        char opcao;
        do {
            Console.cabecalho("Início > Buscar perguntas");
            Console.mensagem("(A) Perguntas mais recentes");
            Console.mensagem("(B) Busca por palavras-chave");
            Console.mensagem("");
            Console.mensagem("(R) Retornar");
            Console.mensagem("");
            opcao = Console.lerOpcao();

            switch (opcao) {
                case 'A':
                    menuPerguntasMaisRecentes.mostrar(usuario);
                    break;
                case 'B':
                    Console.erro("A busca por palavras-chave será implementada no próximo trabalho prático.");
                    Console.pausar();
                    break;
                case 'R':
                    break;
                default:
                    Console.opcaoInvalida();
            }
        } while (opcao != 'R');
    }

    public Usuario buscarUsuario(int id) {
        return crudUsuario.buscarPorId(id);
    }
}
