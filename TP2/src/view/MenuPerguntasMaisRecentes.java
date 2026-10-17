package view;

import entities.Pergunta;
import entities.Resposta;
import entities.Usuario;
import repository.CrudPergunta;
import repository.CrudResposta;
import repository.CrudUsuario;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MenuPerguntasMaisRecentes {

    private static final int TAMANHO_PAGINA = 5;
    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    private final CrudPergunta crudPergunta;
    private final CrudUsuario crudUsuario;
    private final MenuResposta menuResposta;

    public MenuPerguntasMaisRecentes(CrudUsuario crudUsuario, CrudPergunta crudPergunta,
            CrudResposta crudResposta) {
        this.crudUsuario = crudUsuario;
        this.crudPergunta = crudPergunta;
        this.menuResposta = new MenuResposta(crudUsuario, crudResposta);
    }

    public void mostrar(Usuario usuario) {
        Pergunta[] perguntas = crudPergunta.listarMaisRecentes();
        int pagina = 0;
        String entrada;

        do {
            int inicio = pagina * TAMANHO_PAGINA;
            int fim = Math.min(inicio + TAMANHO_PAGINA, perguntas.length);

            Console.cabecalho("Inicio > Buscar perguntas > Perguntas mais recentes");
            if (perguntas.length == 0) {
                Console.mensagem("Nenhuma pergunta ativa encontrada.");
            } else {
                for (int i = inicio; i < fim; i++) {
                    imprimirResumo(i - inicio + 1, perguntas[i]);
                }
            }

            Console.mensagem("");
            if (pagina > 0) {
                Console.mensagem("(A) Pagina anterior");
            }
            if (fim < perguntas.length) {
                Console.mensagem("(B) Proxima pagina");
            }
            Console.mensagem("(R) Retornar");
            entrada = Console.lerLinha("Opcao: ");

            if (entrada.equalsIgnoreCase("A") && pagina > 0) {
                pagina--;
            } else if (entrada.equalsIgnoreCase("B") && fim < perguntas.length) {
                pagina++;
            } else if (!entrada.equalsIgnoreCase("R") && !entrada.isEmpty()) {
                try {
                    int numero = Integer.parseInt(entrada);
                    if (numero >= 1 && numero <= fim - inicio) {
                        mostrarPergunta(perguntas[inicio + numero - 1], usuario);
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

    private void mostrarPergunta(Pergunta pergunta, Usuario usuario) {
        Usuario autor = crudUsuario.buscarPorId(pergunta.getIdUsuario());
        String nomeAutor = autor == null ? "Usuario desconhecido" : autor.getNome();

        Console.cabecalho("Inicio > Buscar perguntas > Perguntas mais recentes > Pergunta");
        Console.mensagem(pergunta.getPergunta());
        Console.mensagem("Criada por " + nomeAutor + " em " + formatarData(pergunta.getCriacao()));
        Console.mensagem("");
        Console.mensagem("RESPOSTAS");
        Console.mensagem("---------");
        ArrayList<Resposta> respostas = menuResposta.listarPorPergunta(pergunta.getId());
        for (int i = 0; i < respostas.size(); i++) {
            imprimirResumoResposta(i + 1, respostas.get(i));
        }
        if (respostas.isEmpty()) {
            Console.mensagem("Esta pergunta ainda nao possui respostas.");
        }
        Console.mensagem("");
        Console.mensagem("(A) Responder a pergunta");
        Console.mensagem("(R) Retornar");
        String entrada;
        do {
            entrada = Console.lerLinha("Opcao: ");
            if (entrada.equalsIgnoreCase("A")) {
                menuResposta.responder(pergunta, usuario);
            } else if (!entrada.equalsIgnoreCase("R")) {
                try {
                    int numero = Integer.parseInt(entrada);
                    if (numero >= 1 && numero <= respostas.size()) {
                        menuResposta.mostrar(pergunta, respostas.get(numero - 1), usuario);
                    } else {
                        Console.opcaoInvalida();
                    }
                } catch (NumberFormatException e) {
                    Console.opcaoInvalida();
                }
            }
        } while (!entrada.equalsIgnoreCase("R"));
    }

    private void imprimirResumoResposta(int numero, Resposta resposta) {
        Usuario autor = crudUsuario.buscarPorId(resposta.getIdUsuario());
        String nomeAutor = autor == null ? "Usuario desconhecido" : autor.getNome();
        Console.mensagem("(" + numero + ") " + resposta.getResposta());
        Console.mensagem("Respondido por " + nomeAutor + " em "
                + formatarData(resposta.getCriacao()));
        Console.mensagem("Nota: " + resposta.getNota() + "  ("
                + resposta.getAvaliacoes() + " avaliacoes)");
        Console.mensagem("");
    }

    private String formatarData(long milissegundos) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(milissegundos),
                ZoneId.systemDefault()).format(FORMATO);
    }
}
