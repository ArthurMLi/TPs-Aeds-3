package view;

import entities.Pergunta;
import entities.Usuario;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import repository.CrudPergunta;
import repository.CrudUsuario;

public class MenuPerguntasPorPalavrasChaves {

    private static final int TAMANHO_PAGINA = 5;
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy 'as' HH:mm");

    private final CrudPergunta crudPergunta;
    private final CrudUsuario crudUsuario;

    public MenuPerguntasPorPalavrasChaves(CrudUsuario crudUsuario, CrudPergunta crudPergunta) {
        this.crudUsuario = crudUsuario;
        this.crudPergunta = crudPergunta;
    }

    public void mostrar(Usuario usuario) {
        String consulta = Console.lerLinha("Digite as palavras-chave: ");
        Pergunta[] perguntas = crudPergunta.listarPorPalavrasChave(consulta);
        Pergunta[] perguntasOrdenadas = perguntas;
        int pagina = 0;
        String entrada;

        do {
            int inicio = pagina * TAMANHO_PAGINA;
            int fim = Math.min(inicio + TAMANHO_PAGINA, perguntasOrdenadas.length);

            Console.cabecalho("Inicio > Buscar perguntas > Perguntas por palavras chaves");
            if (perguntasOrdenadas.length == 0) {
                Console.mensagem("Nenhuma pergunta ativa encontrada.");
            } else {
                for (int i = inicio; i < fim; i++) {
                    imprimirResumo(i - inicio + 1, perguntasOrdenadas[i]);
                }
            }

            Console.mensagem("");
            if (pagina > 0) {
                Console.mensagem("(A) Pagina anterior");
            }
            if (fim < perguntasOrdenadas.length) {
                Console.mensagem("(B) Proxima pagina");
            }
            Console.mensagem("(R) Retornar");
            entrada = Console.lerLinha("Opcao: ");

            if (entrada.equalsIgnoreCase("A") && pagina > 0) {
                pagina--;
            } else if (entrada.equalsIgnoreCase("B") && fim < perguntasOrdenadas.length) {
                pagina++;
            } else if (!entrada.equalsIgnoreCase("R") && !entrada.isEmpty()) {
                try {
                    int numero = Integer.parseInt(entrada);
                    if (numero >= 1 && numero <= fim - inicio) {
                        mostrarPergunta(perguntasOrdenadas[inicio + numero - 1]);
                    } else {
                        Console.opcaoInvalida();
                    }
                } catch (NumberFormatException e) {
                    Console.opcaoInvalida();
                }
            }
        } while (!entrada.equalsIgnoreCase("R"));
    }

    private void imprimirResumo(int numero, Pergunta pergunta) {
        Usuario autor = crudUsuario.buscarPorId(pergunta.getIdUsuario());
        String nomeAutor = autor == null ? "Usuario desconhecido" : autor.getNome();

        Console.mensagem("(" + numero + ") " + pergunta.getPergunta());
        Console.mensagem("    Criada por " + nomeAutor + " em " + formatarData(pergunta.getCriacao()));
        Console.mensagem("");
    }

    private void mostrarPergunta(Pergunta pergunta) {
        Usuario autor = crudUsuario.buscarPorId(pergunta.getIdUsuario());
        String nomeAutor = autor == null ? "Usuario desconhecido" : autor.getNome();

        Console.cabecalho("Inicio > Buscar perguntas > Perguntas por palavras chaves > Pergunta");
        Console.mensagem(pergunta.getPergunta());
        Console.mensagem("Criada por " + nomeAutor + " em " + formatarData(pergunta.getCriacao()));
        Console.mensagem("");
        Console.mensagem("RESPOSTAS");
        Console.mensagem("---------");
        Console.mensagem("As respostas serao implementadas no proximo trabalho pratico.");
        Console.mensagem("");
        Console.mensagem("(R) Retornar");
        while (!Console.lerLinha("Opcao: ").equalsIgnoreCase("R")) {
            Console.opcaoInvalida();
        }
    }

    private String formatarData(long milissegundos) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(milissegundos),
                ZoneId.systemDefault()).format(FORMATO);
    }
}
