package view;

import entities.Pergunta;
import entities.Resposta;
import entities.Usuario;
import repository.CrudAvaliacao;
import repository.CrudResposta;
import repository.CrudUsuario;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/** Telas de consulta, inclusao e arquivamento de respostas. */
public class MenuResposta {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'as' HH:mm");

    private final CrudUsuario crudUsuario;
    private final CrudResposta crudResposta;
    private final CrudAvaliacao crudAvaliacao;

    public MenuResposta(CrudUsuario crudUsuario, CrudResposta crudResposta) {
        this(crudUsuario, crudResposta, null);
    }

    public MenuResposta(CrudUsuario crudUsuario, CrudResposta crudResposta,
            CrudAvaliacao crudAvaliacao) {
        this.crudUsuario = crudUsuario;
        this.crudResposta = crudResposta;
        this.crudAvaliacao = crudAvaliacao;
    }

    public ArrayList<Resposta> listarPorPergunta(int idPergunta) {
        return crudResposta.listarPorPergunta(idPergunta);
    }

    public void responder(Pergunta pergunta, Usuario usuario) {
        Console.cabecalho("Início > Buscar perguntas > Perguntas mais recentes > Pergunta > Responder");
        imprimirPergunta(pergunta);
        String texto = Console.lerTexto("Resposta: ", 1);
        if (texto == null) {
            return;
        }
        if (!Console.confirmar("Confirma a inclusão da resposta?")) {
            Console.mensagem("Inclusão cancelada.");
            Console.pausar();
            return;
        }
        String erro = crudResposta.criar(usuario.getId(), pergunta.getId(), texto);
        if (erro == null) {
            Console.sucesso("Resposta incluída.");
        } else {
            Console.erro(erro);
        }
        Console.pausar();
    }

    public void mostrar(Pergunta pergunta, Resposta resposta, Usuario usuario) {
        mostrar("Início > Buscar perguntas > Perguntas mais recentes > Pergunta > Resposta",
                pergunta, resposta, usuario, true);
    }

    public void mostrar(String trilha, Pergunta pergunta, Resposta resposta, Usuario usuario,
            boolean podeAvaliar) {
        char opcao;
        do {
            Console.cabecalho(trilha);
            if (pergunta != null) {
                imprimirPergunta(pergunta);
            }
            Console.mensagem("RESPOSTA");
            Console.mensagem("--------");
            Console.mensagem(resposta.getResposta());
            Usuario autor = crudUsuario == null ? null : crudUsuario.buscarPorId(resposta.getIdUsuario());
            String nomeAutor = resposta.getIdUsuario() == usuario.getId()
                    ? "você" : autor == null ? "Usuário desconhecido" : autor.getNome();
            Console.mensagem("Respondido por " + nomeAutor + " em "
                    + formatarData(resposta.getCriacao()));
                Console.mensagem("Nota: " + resposta.getNota() + "  ("
                    + resposta.getAvaliacoes() + " avaliações)");
            Console.mensagem("");

            boolean isDonoResposta = (resposta.getIdUsuario() == usuario.getId());
            boolean isDonoPergunta = (pergunta != null && pergunta.getIdUsuario() == usuario.getId());

            if (isDonoResposta) {
                Console.mensagem("(A) Arquivar resposta");
            } else if (podeAvaliar) {
                Console.mensagem("(A) Concordar com a resposta");
                Console.mensagem("(B) Discordar da resposta");
                if (isDonoPergunta) {
                    Console.mensagem("(C) Arquivar resposta");
                }
            } else if (isDonoPergunta) {
                Console.mensagem("(A) Arquivar resposta");
            }

            Console.mensagem("(R) Retornar ao menu anterior");
            Console.mensagem("");
            opcao = Console.lerOpcao();

            if (isDonoResposta) {
                if (opcao == 'A') {
                    if (arquivar(resposta, usuario)) {
                        return;
                    }
                } else if (opcao != 'R') {
                    Console.opcaoInvalida();
                }
            } else if (podeAvaliar) {
                if (opcao == 'A') {
                    avaliar(resposta, usuario, true);
                } else if (opcao == 'B') {
                    avaliar(resposta, usuario, false);
                } else if (isDonoPergunta && opcao == 'C') {
                    if (arquivar(resposta, usuario)) {
                        return;
                    }
                } else if (opcao != 'R') {
                    Console.opcaoInvalida();
                }
            } else if (isDonoPergunta) {
                if (opcao == 'A') {
                    if (arquivar(resposta, usuario)) {
                        return;
                    }
                } else if (opcao != 'R') {
                    Console.opcaoInvalida();
                }
            } else if (opcao != 'R') {
                Console.opcaoInvalida();
            }
        } while (opcao != 'R');
    }

    private void avaliar(Resposta resposta, Usuario usuario, boolean positiva) {
        if (crudAvaliacao == null) {
            Console.erro("Módulo de avaliação não disponível.");
            Console.pausar();
            return;
        }
        String acao = positiva ? "concordar com" : "discordar de";
        if (!Console.confirmar("Confirma " + acao + " esta resposta?")) {
            Console.mensagem("Avaliação cancelada.");
            Console.pausar();
            return;
        }
        String erro = crudAvaliacao.avaliar(usuario.getId(), resposta.getId(), positiva);
        if (erro == null) {
            Console.sucesso("Avaliação registrada.");
            Resposta atualizada = crudResposta.ler(resposta.getId());
            if (atualizada != null) {
                resposta.setNota(atualizada.getNota());
                resposta.setAvaliacoes(atualizada.getAvaliacoes());
            }
        } else {
            Console.erro(erro);
        }
        Console.pausar();
    }

    private boolean arquivar(Resposta resposta, Usuario usuario) {
        if (!Console.confirmar("Confirma o arquivamento da resposta?")) {
            Console.mensagem("Arquivamento cancelado.");
            Console.pausar();
            return false;
        }
        String erro = crudResposta.arquivar(resposta.getId(), usuario.getId());
        if (erro == null) {
            Console.sucesso("Resposta arquivada.");
            Console.pausar();
            return true;
        } else {
            Console.erro(erro);
            Console.pausar();
            return false;
        }
    }

    private void imprimirPergunta(Pergunta pergunta) {
        Usuario autor = crudUsuario == null ? null : crudUsuario.buscarPorId(pergunta.getIdUsuario());
        String nomeAutor = autor == null ? "Usuário desconhecido" : autor.getNome();
        Console.mensagem(pergunta.getPergunta());
        Console.mensagem("Criada por " + nomeAutor + " em "
                + formatarData(pergunta.getCriacao()));
        Console.mensagem("");
    }

    private String formatarData(long milissegundos) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milissegundos),
                ZoneId.systemDefault()).format(FORMATO);
    }
}