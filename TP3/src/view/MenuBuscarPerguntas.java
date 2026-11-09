package view;

import entities.Usuario;
import repository.CrudPergunta;
import repository.CrudUsuario;

public class MenuBuscarPerguntas {

    private final CrudUsuario crudUsuario;
    private final MenuPerguntasMaisRecentes menuPerguntasMaisRecentes;
    private final MenuPerguntasPorPalavrasChaves menuPerguntasPorPalavrasChaves;

    public MenuBuscarPerguntas(CrudUsuario crudUsuario, CrudPergunta crudPergunta) {
        this.crudUsuario = crudUsuario;
        this.menuPerguntasMaisRecentes =
                new MenuPerguntasMaisRecentes(crudUsuario, crudPergunta);
        this.menuPerguntasPorPalavrasChaves =
                new MenuPerguntasPorPalavrasChaves(crudUsuario, crudPergunta);
    }

    public void mostrar(Usuario usuario) {
        char opcao;
        do {
            Console.cabecalho("Inicio > Buscar perguntas");
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
                    menuPerguntasPorPalavrasChaves.mostrar(usuario);
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
