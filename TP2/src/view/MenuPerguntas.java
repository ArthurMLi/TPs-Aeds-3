package view;

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
 * Gestao das perguntas do usuario logado.
 *
 * Exibe a lista de perguntas do usuario e, ao selecionar uma, apresenta os dados
 * completos da pergunta e as respostas associadas, permitindo alterar/arquivar a
 * pergunta ou moderar/arquivar respostas inadequadas.
 */
public class MenuPerguntas {

    private static final DateTimeFormatter FORMATO_PERGUNTA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FORMATO_RESPOSTA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'as' HH:mm");

    private final CrudUsuario crudUsuario;
    private final CrudPergunta crudPergunta;
    private final CrudResposta crudResposta;
    private final MenuResposta menuResposta;

    public MenuPerguntas(CrudUsuario crudUsuario, CrudPergunta crudPergunta,
            CrudResposta crudResposta) {
        this(crudUsuario, crudPergunta, crudResposta, null);
    }

    public MenuPerguntas(CrudUsuario crudUsuario, CrudPergunta crudPergunta,
            CrudResposta crudResposta, CrudAvaliacao crudAvaliacao) {
        this.crudUsuario = crudUsuario;
        this.crudPergunta = crudPergunta;
        this.crudResposta = crudResposta;
        this.menuResposta = new MenuResposta(crudUsuario, crudResposta, crudAvaliacao);
    }

    public MenuPerguntas(CrudPergunta crudPergunta) {
        this(null, crudPergunta, null);
    }

    public void mostrar(Usuario usuario) {
        String entrada;
        do {
            Console.cabecalho("Início > Minha área > Minhas perguntas");
            Pergunta[] perguntas = crudPergunta.listar(usuario.getId());

            if (perguntas.length == 0) {
                Console.mensagem("Você ainda não cadastrou nenhuma pergunta.");
            } else {
                for (int i = 0; i < perguntas.length; i++) {
                    Pergunta p = perguntas[i];
                    String estado = p.isAtiva() ? "" : "[ARQUIVADA] ";
                    Console.mensagem("(" + (i + 1) + ") " + estado + formatarData(p.getCriacao(), FORMATO_PERGUNTA));
                    Console.mensagem("    " + p.getPergunta());
                    Console.mensagem("    Palavras-chave: " + p.getPalavrasChave());
                    Console.mensagem("");
                }
            }

            Console.mensagem("");
            Console.mensagem("(A) Incluir nova pergunta");
            Console.mensagem("(R) Retornar ao menu anterior");
            entrada = Console.lerLinha("Opcao: ");

            if (entrada.equalsIgnoreCase("A") || entrada.equalsIgnoreCase("I") || entrada.equalsIgnoreCase("B")) {
                incluir(usuario);
            } else if (!entrada.equalsIgnoreCase("R") && !entrada.isEmpty()) {
                try {
                    int numero = Integer.parseInt(entrada);
                    if (numero >= 1 && numero <= perguntas.length) {
                        mostrarPergunta(perguntas[numero - 1], usuario);
                    } else {
                        Console.opcaoInvalida();
                    }
                } catch (NumberFormatException e) {
                    Console.opcaoInvalida();
                }
            }
        } while (!entrada.equalsIgnoreCase("R"));
    }

    private void mostrarPergunta(Pergunta pergunta, Usuario usuario) {
        String entrada;
        do {
            Pergunta atual = crudPergunta.ler(pergunta.getId());
            if (atual != null) {
                pergunta = atual;
            }

            Console.cabecalho("Início > Minha área > Minhas perguntas > Pergunta");
            Console.mensagem(formatarData(pergunta.getCriacao(), FORMATO_PERGUNTA));
            Console.mensagem(pergunta.getPergunta());
            Console.mensagem("Palavras chave: " + pergunta.getPalavrasChave());
            if (!pergunta.isAtiva()) {
                Console.mensagem("[Esta pergunta está ARQUIVADA]");
            }
            Console.mensagem("");
            Console.mensagem("RESPOSTAS");
            Console.mensagem("---------");

            ArrayList<Resposta> respostas = menuResposta.listarPorPergunta(pergunta.getId());
            if (respostas.isEmpty()) {
                Console.mensagem("Esta pergunta ainda não possui respostas.");
            } else {
                for (int i = 0; i < respostas.size(); i++) {
                    imprimirResumoResposta(i + 1, respostas.get(i));
                }
            }

            Console.mensagem("");
            Console.mensagem("(A) Alterar dados da pergunta");
            Console.mensagem("(B) Arquivar pergunta");
            Console.mensagem("(R) Retornar");
            entrada = Console.lerLinha("Opcao: ");

            if (entrada.equalsIgnoreCase("A")) {
                alterar(pergunta, usuario);
            } else if (entrada.equalsIgnoreCase("B")) {
                if (arquivar(pergunta, usuario)) {
                    return;
                }
            } else if (!entrada.equalsIgnoreCase("R") && !entrada.isEmpty()) {
                try {
                    int numero = Integer.parseInt(entrada);
                    if (numero >= 1 && numero <= respostas.size()) {
                        menuResposta.mostrar("Início > Minha área > Minhas perguntas > Pergunta > Resposta",
                            pergunta, respostas.get(numero - 1), usuario, true);
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
        Usuario autor = crudUsuario == null ? null : crudUsuario.buscarPorId(resposta.getIdUsuario());
        String nomeAutor = autor == null ? "Usuário desconhecido" : autor.getNome();
        Console.mensagem("(" + numero + ") " + resposta.getResposta());
                Console.mensagem("Respondido por " + nomeAutor + " em "
                + formatarData(resposta.getCriacao(), FORMATO_RESPOSTA));
        Console.mensagem("Nota: " + resposta.getNota() + "  ("
                + resposta.getAvaliacoes() + " avaliações)");
        Console.mensagem("");
    }

    private void incluir(Usuario usuario) {
        Console.cabecalho("Início > Minha área > Minhas perguntas > Incluir");
        String texto = Console.lerTexto("Pergunta (ENTER cancela): ", 1);
        if (texto == null) {
            return;
        }
        Console.mensagem("Separe as palavras-chave por ponto e vírgula. Ex.: pão;mofado;saúde");
        String palavras = Console.lerTexto("Palavras-chave (ENTER cancela): ", 1);
        if (palavras == null) {
            return;
        }
        if (!Console.confirmar("\nConfirma a inclusão da pergunta?")) {
            Console.mensagem("Inclusão cancelada.");
            Console.pausar();
            return;
        }
        relatar(crudPergunta.incluir(usuario.getId(), texto, palavras), "Pergunta incluida.");
    }

    private void alterar(Pergunta pergunta, Usuario usuario) {
        Console.cabecalho("Início > Minha área > Minhas perguntas > Alterar");
        if (!pergunta.isAtiva()) {
            Console.erro("Uma pergunta arquivada não pode ser alterada.");
            Console.pausar();
            return;
        }

            Console.mensagem("\nDeixe em branco para manter o valor atual.");
        Console.mensagem("\nPergunta atual: " + pergunta.getPergunta());
        String texto = Console.lerLinha("Nova pergunta: ");
        Console.mensagem("\nPalavras-chave atuais: " + pergunta.getPalavrasChave());
        String palavras = Console.lerLinha("Novas palavras-chave: ");

        if (texto.isEmpty() && palavras.isEmpty()) {
            Console.mensagem("Nada foi alterado.");
            Console.pausar();
            return;
        }
        if (!Console.confirmar("\nConfirma a alteração?")) {
            Console.mensagem("Alteração cancelada.");
            Console.pausar();
            return;
        }
        relatar(crudPergunta.alterar(pergunta.getId(), usuario.getId(), texto, palavras),
                "Pergunta alterada.");
    }

    private boolean arquivar(Pergunta pergunta, Usuario usuario) {
        Console.cabecalho("Início > Minha área > Minhas perguntas > Arquivar");
        if (!pergunta.isAtiva()) {
            Console.erro("Esta pergunta já está arquivada.");
            Console.pausar();
            return false;
        }

        Console.mensagem("\nPergunta: " + pergunta.getPergunta());
        Console.mensagem("O arquivamento é definitivo: não será possível desarquivar.");
        if (!Console.confirmar("Confirma o arquivamento?")) {
            Console.mensagem("Arquivamento cancelado.");
            Console.pausar();
            return false;
        }
        String erro = crudPergunta.arquivar(pergunta.getId(), usuario.getId());
        if (erro == null) {
            Console.sucesso("Pergunta arquivada.");
            Console.pausar();
            return true;
        } else {
            Console.erro(erro);
            Console.pausar();
            return false;
        }
    }

    private void relatar(String erro, String mensagemDeSucesso) {
        if (erro != null) {
            Console.erro(erro);
        } else {
            Console.sucesso(mensagemDeSucesso);
        }
        Console.pausar();
    }

    private String formatarData(long milissegundos, DateTimeFormatter formato) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(milissegundos),
                ZoneId.systemDefault()).format(formato);
    }
}
