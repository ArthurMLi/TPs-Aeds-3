package view;

import entities.Pergunta;
import entities.Resposta;
import entities.Usuario;
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

    public MenuResposta(CrudUsuario crudUsuario, CrudResposta crudResposta) {
        this.crudUsuario = crudUsuario;
        this.crudResposta = crudResposta;
    }

    public ArrayList<Resposta> listarPorPergunta(int idPergunta) {
        return crudResposta.listarPorResposta(idPergunta);
    }

    public void responder(Pergunta pergunta, Usuario usuario) {
        Console.cabecalho("Inicio > Buscar perguntas > Perguntas mais recentes > Pergunta > Responder");
        imprimirPergunta(pergunta);
        String texto = Console.lerTexto("Resposta (ENTER cancela): ", 1);
        if (texto == null) {
            return;
        }
        if (!Console.confirmar("Confirma a inclusao da resposta?")) {
            Console.mensagem("Inclusao cancelada.");
            Console.pausar();
            return;
        }
        String erro = crudResposta.criar(usuario.getId(), pergunta.getId(), texto);
        if (erro == null) {
            Console.sucesso("Resposta incluida.");
        } else {
            Console.erro(erro);
        }
        Console.pausar();
    }

    public void mostrar(Pergunta pergunta, Resposta resposta, Usuario usuario) {
        char opcao;
        do {
            Console.cabecalho("Inicio > Buscar perguntas > Perguntas mais recentes > Pergunta > Resposta");
            imprimirPergunta(pergunta);
            Console.mensagem("RESPOSTA");
            Console.mensagem("--------");
            Console.mensagem(resposta.getResposta());
            Usuario autor = crudUsuario.buscarPorId(resposta.getIdUsuario());
            String nomeAutor = resposta.getIdUsuario() == usuario.getId()
                    ? "voce" : autor == null ? "Usuario desconhecido" : autor.getNome();
            Console.mensagem("Respondido por " + nomeAutor + " em "
                    + formatarData(resposta.getCriacao()));
            Console.mensagem("Nota: " + resposta.getNota() + "  ("
                    + resposta.getAvaliacoes() + " avaliacoes)");
            Console.mensagem("");

            if (resposta.getIdUsuario() == usuario.getId()) {
                Console.mensagem("(A) Arquivar resposta");
            }
            Console.mensagem("(R) Retornar ao menu anterior");
            opcao = Console.lerOpcao();

            if (opcao == 'A' && resposta.getIdUsuario() == usuario.getId()) {
                arquivar(resposta, usuario);
                return;
            }
            if (opcao != 'R' && !(opcao == 'A' && resposta.getIdUsuario() == usuario.getId())) {
                Console.opcaoInvalida();
            }
        } while (opcao != 'R');
    }

    private void arquivar(Resposta resposta, Usuario usuario) {
        if (!Console.confirmar("Confirma o arquivamento da resposta?")) {
            Console.mensagem("Arquivamento cancelado.");
            Console.pausar();
            return;
        }
        String erro = crudResposta.arquivar(resposta.getId(), usuario.getId());
        if (erro == null) {
            Console.sucesso("Resposta arquivada.");
        } else {
            Console.erro(erro);
        }
        Console.pausar();
    }

    private void imprimirPergunta(Pergunta pergunta) {
        Usuario autor = crudUsuario.buscarPorId(pergunta.getIdUsuario());
        String nomeAutor = autor == null ? "Usuario desconhecido" : autor.getNome();
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