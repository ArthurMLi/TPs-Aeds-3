package view;

import java.util.Scanner;

/**
 * Utilitarios de entrada e saida da interface textual.
 *
 * Mantem um unico Scanner para toda a aplicacao: abrir varios Scanners sobre
 * System.in faz um consumir o buffer do outro e as leituras comecam a se
 * perder.
 */
public class Console {

    private static final Scanner ENTRADA = new Scanner(System.in);

    public static final String TITULO = "AJUDA AÍ 1.0";

    private Console() {
    }

    /** Cabecalho padrao das telas, com a trilha de navegacao. */
    public static void cabecalho(String trilha) {
        System.out.println();
        System.out.println(TITULO);
        System.out.println("------------");
        if (trilha != null && !trilha.isEmpty()) {
            System.out.println("> " + trilha);
        }
        System.out.println();
    }

    public static String lerLinha(String rotulo) {
        System.out.print(rotulo);
        String linha = ENTRADA.nextLine();
        return linha == null ? "" : linha.trim();
    }

    /** Le uma opcao de menu e devolve a primeira letra em maiuscula. */
    public static char lerOpcao() {
        String linha = lerLinha("Opção: ");
        if (linha.isEmpty()) {
            return ' ';
        }
        return Character.toUpperCase(linha.charAt(0));
    }

    /** Le um texto obrigatorio; Enter em branco cancela e devolve null. */
    public static String lerTexto(String rotulo, int tamanhoMinimo) {
        while (true) {
            String texto = lerLinha(rotulo);
            if (texto.isEmpty()) {
                return null;
            }
            if (texto.length() >= tamanhoMinimo) {
                return texto;
            }
            erro("O valor deve ter no mínimo " + tamanhoMinimo + " caracteres.");
        }
    }

    public static boolean confirmar(String pergunta) {
        String resposta = lerLinha(pergunta + " (S/N) ");
        return !resposta.isEmpty()
                && (resposta.charAt(0) == 'S' || resposta.charAt(0) == 's');
    }

    public static void mensagem(String texto) {
        System.out.println(texto);
    }

    public static void erro(String texto) {
        System.out.println("[!] " + texto);
    }

    public static void sucesso(String texto) {
        System.out.println("[ok] " + texto);
    }

    public static void opcaoInvalida() {
        erro("Opção inválida.");
    }

    public static void pausar() {
        System.out.print("\nPressione ENTER para continuar...");
        ENTRADA.nextLine();
    }
}
