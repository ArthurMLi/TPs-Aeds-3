package view;

import entities.Avaliacao;
import entities.Pergunta;
import entities.Resposta;
import entities.Usuario;
import repository.CrudAvaliacao;
import repository.CrudPergunta;
import repository.CrudResposta;
import repository.CrudUsuario;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Visualizacao e cancelamento das avaliacoes realizadas pelo usuario logado.
 */
public class MenuMinhasAvaliacoes {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'as' HH:mm");

    private final CrudUsuario crudUsuario;
    private final CrudPergunta crudPergunta;
    private final CrudResposta crudResposta;
    private final CrudAvaliacao crudAvaliacao;

    public MenuMinhasAvaliacoes(CrudUsuario crudUsuario, CrudPergunta crudPergunta,
            CrudResposta crudResposta, CrudAvaliacao crudAvaliacao) {
        this.crudUsuario = crudUsuario;
        this.crudPergunta = crudPergunta;
        this.crudResposta = crudResposta;
        this.crudAvaliacao = crudAvaliacao;
    }

    public void mostrar(Usuario usuario) {
        if (crudAvaliacao == null) {
            Console.erro("Módulo de avaliações não disponível.");
            Console.pausar();
            return;
        }

        String entrada;
        do {
            Console.cabecalho("Início > Minha área > Minhas avaliações");
            ArrayList<Avaliacao> avaliacoes = crudAvaliacao.listarPorUsuario(usuario.getId());

            if (avaliacoes.isEmpty()) {
                Console.mensagem("Você ainda não realizou nenhuma avaliação.");
            } else {
                for (int i = 0; i < avaliacoes.size(); i++) {
                    Avaliacao av = avaliacoes.get(i);
                    Resposta r = crudResposta != null ? crudResposta.ler(av.getIdResposta()) : null;
                        String textoResposta = r != null ? r.getResposta() : "Resposta #" + av.getIdResposta();
                        Pergunta p = r != null && crudPergunta != null
                            ? crudPergunta.ler(r.getIdPergunta()) : null;
                        String textoPergunta = p != null ? p.getPergunta() : "Pergunta não encontrada";
                        String tipo = av.isAvaliacao() ? "Positiva (+1)" : "Negativa (-1)";

                    Console.mensagem("(" + (i + 1) + ") " + textoResposta);
                        Console.mensagem("    Pergunta: " + textoPergunta);
                        Console.mensagem("    Sua avaliação: " + tipo);
                    Console.mensagem("");
                }
            }

            Console.mensagem("");
            Console.mensagem("(R) Retornar ao menu anterior");
            entrada = Console.lerLinha("Opcao: ");

            if (!entrada.equalsIgnoreCase("R") && !entrada.isEmpty()) {
                try {
                    int numero = Integer.parseInt(entrada);
                    if (numero >= 1 && numero <= avaliacoes.size()) {
                        mostrarAvaliacao(avaliacoes.get(numero - 1), usuario);
                    } else {
                        Console.opcaoInvalida();
                    }
                } catch (NumberFormatException e) {
                    Console.opcaoInvalida();
                }
            }
        } while (!entrada.equalsIgnoreCase("R"));
    }

    private void mostrarAvaliacao(Avaliacao avaliacao, Usuario usuario) {
        Resposta resposta = crudResposta != null ? crudResposta.ler(avaliacao.getIdResposta()) : null;
        if (resposta == null) {
            Console.erro("Resposta vinculada não encontrada.");
            Console.pausar();
            return;
        }
        Pergunta pergunta = crudPergunta != null ? crudPergunta.ler(resposta.getIdPergunta()) : null;

        char opcao;
        do {
            Console.cabecalho("Início > Minha área > Minhas avaliações > Avaliação");
            if (pergunta != null) {
                Usuario autorPergunta = crudUsuario != null ? crudUsuario.buscarPorId(pergunta.getIdUsuario()) : null;
                String nomeAutorPergunta = autorPergunta == null ? "Usuário desconhecido" : autorPergunta.getNome();
                Console.mensagem(pergunta.getPergunta());
                Console.mensagem("Criada por " + nomeAutorPergunta + " em " + formatarData(pergunta.getCriacao()));
                Console.mensagem("");
            }

            Console.mensagem("RESPOSTA AVALIADA");
            Console.mensagem("-----------------");
            Console.mensagem(resposta.getResposta());
            Usuario autorResposta = crudUsuario != null ? crudUsuario.buscarPorId(resposta.getIdUsuario()) : null;
            String nomeAutorResposta = autorResposta == null ? "Usuário desconhecido" : autorResposta.getNome();
            Console.mensagem("Respondido por " + nomeAutorResposta + " em " + formatarData(resposta.getCriacao()));
            Console.mensagem("Nota: " + resposta.getNota() + "  (" + resposta.getAvaliacoes() + " avaliações)");
            Console.mensagem("");

            String tipo = avaliacao.isAvaliacao() ? "Positiva (+1)" : "Negativa (-1)";
            Console.mensagem("SUA AVALIAÇÃO: " + tipo);
            Console.mensagem("");

            Console.mensagem("(A) Cancelar avaliação");
            Console.mensagem("(R) Retornar ao menu anterior");
            Console.mensagem("");
            opcao = Console.lerOpcao();

            if (opcao == 'A') {
                if (cancelarAvaliacao(avaliacao, usuario)) {
                    return;
                }
            } else if (opcao != 'R') {
                Console.opcaoInvalida();
            }
        } while (opcao != 'R');
    }

    private boolean cancelarAvaliacao(Avaliacao avaliacao, Usuario usuario) {
        if (!Console.confirmar("Confirma o cancelamento desta avaliação?")) {
            Console.mensagem("Cancelamento abortado.");
            Console.pausar();
            return false;
        }
        String erro = crudAvaliacao.cancelar(avaliacao.getId(), usuario.getId());
        if (erro == null) {
            Console.sucesso("Avaliação cancelada.");
            Console.pausar();
            return true;
        } else {
            Console.erro(erro);
            Console.pausar();
            return false;
        }
    }

    private String formatarData(long milissegundos) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(milissegundos),
                ZoneId.systemDefault()).format(FORMATO);
    }
}
