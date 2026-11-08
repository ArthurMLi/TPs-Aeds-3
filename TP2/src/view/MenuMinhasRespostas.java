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

/**
 * Gestao das respostas escritas pelo usuario logado.
 *
 * Permite visualizar a lista de respostas criadas pelo usuario, acessar seus
 * detalhes e arquivar a resposta selecionada.
 */
public class MenuMinhasRespostas {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'as' HH:mm");

    private final CrudUsuario crudUsuario;
    private final CrudPergunta crudPergunta;
    private final CrudResposta crudResposta;
    private final MenuResposta menuResposta;

    public MenuMinhasRespostas(CrudUsuario crudUsuario, CrudPergunta crudPergunta,
            CrudResposta crudResposta) {
        this.crudUsuario = crudUsuario;
        this.crudPergunta = crudPergunta;
        this.crudResposta = crudResposta;
        this.menuResposta = new MenuResposta(crudUsuario, crudResposta);
    }

    public void mostrar(Usuario usuario) {
        if (crudResposta == null) {
            Console.erro("Módulo de respostas não disponível.");
            Console.pausar();
            return;
        }

        String entrada;
        do {
            Console.cabecalho("Início > Minha área > Minhas respostas");
            ArrayList<Resposta> respostas = crudResposta.listarPorUsuario(usuario.getId());

            if (respostas.isEmpty()) {
                Console.mensagem("Você ainda não cadastrou nenhuma resposta.");
            } else {
                for (int i = 0; i < respostas.size(); i++) {
                    Resposta r = respostas.get(i);
                    Pergunta p = crudPergunta != null ? crudPergunta.ler(r.getIdPergunta()) : null;
                    String textoPergunta = p != null ? p.getPergunta() : "Pergunta #" + r.getIdPergunta();

                    Console.mensagem("(" + (i + 1) + ") " + r.getResposta());
                    Console.mensagem("    Em resposta a: \"" + textoPergunta + "\"");
                        Console.mensagem("    Respondido em " + formatarData(r.getCriacao())
                            + " | Nota: " + r.getNota() + " (" + r.getAvaliacoes() + " avaliações)");
                    Console.mensagem("");
                }
            }

            Console.mensagem("");
            Console.mensagem("(R) Retornar ao menu anterior");
            entrada = Console.lerLinha("Opcao: ");

            if (!entrada.equalsIgnoreCase("R") && !entrada.isEmpty()) {
                try {
                    int numero = Integer.parseInt(entrada);
                    if (numero >= 1 && numero <= respostas.size()) {
                        Resposta selecionada = respostas.get(numero - 1);
                        Pergunta p = crudPergunta != null ? crudPergunta.ler(selecionada.getIdPergunta()) : null;
                        menuResposta.mostrar("Início > Minha área > Minhas respostas > Resposta",
                                p, selecionada, usuario, false);
                    } else {
                        Console.opcaoInvalida();
                    }
                } catch (NumberFormatException e) {
                    Console.opcaoInvalida();
                }
            }
        } while (!entrada.equalsIgnoreCase("R"));
    }

    private String formatarData(long milissegundos) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(milissegundos),
                ZoneId.systemDefault()).format(FORMATO);
    }
}
