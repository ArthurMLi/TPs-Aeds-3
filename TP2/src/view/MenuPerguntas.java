package view;

import entities.Pergunta;
import entities.Usuario;
import repository.CrudPergunta;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Gestao das perguntas do usuario logado: listar, incluir, alterar e arquivar.
 *
 * A listagem e numerada sequencialmente na tela e os IDs reais nunca sao
 * exibidos, porque sao de uso interno do sistema. Como a interface e textual e
 * o usuario nao pode "clicar" numa pergunta, a tela guarda um vetor que associa
 * o numero mostrado ao ID real, e e esse vetor que traduz a escolha do usuario
 * nas operacoes de alteracao e arquivamento.
 */
public class MenuPerguntas {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final CrudPergunta crudPergunta;

    public MenuPerguntas(CrudPergunta crudPergunta) {
        this.crudPergunta = crudPergunta;
    }

    public void mostrar(Usuario usuario) {
        char opcao;
        do {
            Console.cabecalho("Inicio > Minha area > Minhas perguntas");
            Console.mensagem("(A) Listar");
            Console.mensagem("(B) Incluir");
            Console.mensagem("(C) Alterar");
            Console.mensagem("(D) Arquivar");
            Console.mensagem("(R) Retornar ao menu anterior");
            Console.mensagem("");
            opcao = Console.lerOpcao();

            switch (opcao) {
                case 'A':
                    listar(usuario);
                    Console.pausar();
                    break;
                case 'B':
                    incluir(usuario);
                    break;
                case 'C':
                    alterar(usuario);
                    break;
                case 'D':
                    arquivar(usuario);
                    break;
                case 'R':
                    break;
                default:
                    Console.opcaoInvalida();
            }
        } while (opcao != 'R');
    }

    // ------------------------------------------------------------------
    // Listagem
    // ------------------------------------------------------------------

    /**
     * Busca as perguntas do usuario pela arvore B+ e as imprime numeradas.
     * Devolve o vetor de perguntas na mesma ordem em que foram exibidas, para
     * que o numero (i + 1) da tela corresponda a posicao i do vetor.
     */
    private Pergunta[] listar(Usuario usuario) {
        Pergunta[] perguntas = crudPergunta.listar(usuario.getId());

        Console.mensagem("\nMINHAS PERGUNTAS");
        Console.mensagem("");
        if (perguntas.length == 0) {
            Console.mensagem("Voce ainda nao cadastrou nenhuma pergunta.");
            return perguntas;
        }

        for (int i = 0; i < perguntas.length; i++) {
            Pergunta p = perguntas[i];
            String estado = p.isAtiva() ? "" : "ARQUIVADA ";
            Console.mensagem("(" + (i + 1) + ") " + estado + formatarData(p.getCriacao()));
            Console.mensagem(p.getPergunta());
            Console.mensagem("Palavras chave: " + p.getPalavrasChave());
            Console.mensagem("");
        }
        return perguntas;
    }

    private String formatarData(long milissegundos) {
        return LocalDateTime
                .ofInstant(Instant.ofEpochMilli(milissegundos), ZoneId.systemDefault())
                .format(FORMATO);
    }

    /**
     * Lista as perguntas e pede que o usuario escolha uma pelo numero da tela.
     * Devolve a pergunta escolhida, ou null se nao houver perguntas ou se o
     * usuario cancelar.
     */
    private Pergunta escolher(Usuario usuario, String acao) {
        Pergunta[] perguntas = listar(usuario);
        if (perguntas.length == 0) {
            Console.pausar();
            return null;
        }

        String entrada = Console.lerTexto("Numero da pergunta a " + acao + " (ENTER cancela): ", 1);
        if (entrada == null) {
            return null;
        }

        int numero;
        try {
            numero = Integer.parseInt(entrada);
        } catch (NumberFormatException e) {
            Console.erro("Informe um numero valido.");
            Console.pausar();
            return null;
        }
        if (numero < 1 || numero > perguntas.length) {
            Console.erro("Nao existe pergunta com esse numero.");
            Console.pausar();
            return null;
        }
        // Traducao do numero exibido para o ID real da pergunta
        return perguntas[numero - 1];
    }

    // ------------------------------------------------------------------
    // Inclusao
    // ------------------------------------------------------------------

    /**
     * O usuario informa somente o texto e as palavras-chave. O idUsuario vem de
     * quem esta logado, as datas vem do relogio e a nota comeca em zero.
     */
    private void incluir(Usuario usuario) {
        Console.cabecalho("Inicio > Minha area > Minhas perguntas > Incluir");
        String texto = Console.lerTexto("Pergunta (ENTER cancela): ", 1);
        if (texto == null) {
            return;
        }
        Console.mensagem("Separe as palavras-chave por ponto-e-virgula. Ex.: pao;mofado;saude");
        String palavras = Console.lerTexto("Palavras-chave (ENTER cancela): ", 1);
        if (palavras == null) {
            return;
        }
        if (!Console.confirmar("\nConfirma a inclusao da pergunta?")) {
            Console.mensagem("Inclusao cancelada.");
            Console.pausar();
            return;
        }
        relatar(crudPergunta.incluir(usuario.getId(), texto, palavras), "Pergunta incluida.");
    }

    // ------------------------------------------------------------------
    // Alteracao
    // ------------------------------------------------------------------

    /**
     * Permite alterar o texto e as palavras-chave. Campos deixados em branco
     * permanecem como estavam. A data de alteracao e ajustada automaticamente
     * pelo ArquivoPergunta.
     */
    private void alterar(Usuario usuario) {
        Console.cabecalho("Inicio > Minha area > Minhas perguntas > Alterar");
        Pergunta pergunta = escolher(usuario, "alterar");
        if (pergunta == null) {
            return;
        }
        if (!pergunta.isAtiva()) {
            Console.erro("Uma pergunta arquivada nao pode ser alterada.");
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
        if (!Console.confirmar("\nConfirma a alteracao?")) {
            Console.mensagem("Alteracao cancelada.");
            Console.pausar();
            return;
        }
        relatar(crudPergunta.alterar(pergunta.getId(), usuario.getId(), texto, palavras),
                "Pergunta alterada.");
    }

    // ------------------------------------------------------------------
    // Arquivamento
    // ------------------------------------------------------------------

    /**
     * Arquivar nao e excluir: o registro continua no arquivo e nos indices,
     * apenas com ativa = false, porque respostas e votos de outros usuarios
     * dependem dele. A operacao e definitiva.
     */
    private void arquivar(Usuario usuario) {
        Console.cabecalho("Inicio > Minha area > Minhas perguntas > Arquivar");
        Pergunta pergunta = escolher(usuario, "arquivar");
        if (pergunta == null) {
            return;
        }
        if (!pergunta.isAtiva()) {
            Console.erro("Esta pergunta ja esta arquivada.");
            Console.pausar();
            return;
        }

        Console.mensagem("\nPergunta: " + pergunta.getPergunta());
        Console.mensagem("O arquivamento e definitivo: nao sera possivel desarquivar.");
        if (!Console.confirmar("Confirma o arquivamento?")) {
            Console.mensagem("Arquivamento cancelado.");
            Console.pausar();
            return;
        }
        relatar(crudPergunta.arquivar(pergunta.getId(), usuario.getId()), "Pergunta arquivada.");
    }

    private void relatar(String erro, String mensagemDeSucesso) {
        if (erro != null) {
            Console.erro(erro);
        } else {
            Console.sucesso(mensagemDeSucesso);
        }
        Console.pausar();
    }
}
