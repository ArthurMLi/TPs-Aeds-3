package repository;

import aed3.Arquivo;
import entities.Usuario;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Scanner;
import java.util.regex.Pattern;

public class CrudUsuario {
    private static final Pattern EMAIL_VALIDO = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final Scanner console = new Scanner(System.in);
    private final Arquivo<Usuario> arquivoUsuarios;

    public CrudUsuario() throws Exception {
        arquivoUsuarios = new Arquivo<>("usuarios", Usuario.class.getConstructor());
    }

    public void criarUsuario() {
        System.out.println("\nNovo usuario");
        System.out.println("Preencha os dados do novo usuario:");
        String email = lerEmail();
        if (email == null) {
            return;
        }
        String nome = lerTexto("Nome (min. de 4 caracteres): ", 4);
        if (nome == null) {
            return;
        }
        String senha = lerTexto("Senha (min. de 4 caracteres): ", 4);
        if (senha == null) {
            return;
        }
        String pergunta = lerTexto("Pergunta secreta (min. de 4 caracteres): ", 4);
        if (pergunta == null) {
            return;
        }
        String resposta = lerTexto("Resposta secreta (min. de 4 caracteres): ", 4);
        if (resposta == null) {
            return;
        }

        System.out.print("Confirma a criacao do usuario? (S/N) ");
        if (!confirmar()) {
            System.out.println("Inclusao cancelada.");
            return;
        }
        try {
            Usuario usuario = new Usuario(nome, email, gerarHash(senha), pergunta, gerarHash(resposta));
            // criar o usuario no arquivo retornando um bool pra validar se foi criado
            // (fazer)
            System.out.println("Usuario incluido com sucesso. ID: " + usuario.getId());
        } catch (Exception e) {
            System.out.println("Erro do sistema. Nao foi possivel criar o usuario.");
        }

    }

    public void buscarUsuario() {
        System.out.println("\nBusca de usuario");
        System.out.println("Preencha o email do usuario a ser buscado:");
        String email = lerEmail();
        if (email == null) {
            return;
        }

        try {
            Usuario usuario = buscarPorEmail(email);
            if (usuario == null) {
                System.out.println("Usuario nao encontrado.");
            } else {
                mostrarUsuario(usuario);
            }
        } catch (Exception e) {
            System.out.println("Erro do sistema. Nao foi possivel buscar o usuario.");
        }
    }

    public void excluirUsuario() {
        System.out.println("\nExclusao de usuario");
        System.out.println("Preencha o email do usuario a ser excluido:");
        String email = lerEmail();
        if (email == null) {
            return;
        }

        try {
            Usuario usuario = buscarPorEmail(email);
            if (usuario == null) {
                System.out.println("Usuario nao encontrado.");
                return;
            }

            mostrarUsuario(usuario);
            System.out.print("Confirma a exclusao do usuario? (S/N) ");
            if (confirmar() // && deletar o usuário (fazer)
            ) {
                System.out.println("Usuario excluido com sucesso.");
            } else {
                System.out.println("Exclusao cancelada.");
            }
        } catch (Exception e) {
            System.out.println("Erro do sistema. Nao foi possivel excluir o usuario.");
        }
    }

    public void mostrarUsuario(Usuario usuario) {
        System.out.println("\nDetalhes do usuario:");
        System.out.println("ID........: " + usuario.getId());
        System.out.println("Nome......: " + usuario.getNome());
        System.out.println("Email.....: " + usuario.getEmail());
        System.out.println("Pergunta..: " + usuario.getPerguntaSecreta());
    }

    public Usuario buscarPorEmail(String email) {
        // Busca um usuário pelo email (fazer)
        // ArrayList<Usuario> usuarios = arquivoUsuarios.readAll();
        /*
         * for (Usuario usuario : usuarios) {
         * if (usuario.getEmail().equalsIgnoreCase(email)) {
         * return usuario;
         * }
         * }
         */
        return null;
    }

    public void fechar() throws Exception {
        arquivoUsuarios.close();
    }

    private String lerEmail() {
        while (true) {
            System.out.print("Email(Enter cancela): ");
            String email = console.nextLine().trim();
            if (email.isEmpty()) {
                return null;
            }
            if (EMAIL_VALIDO.matcher(email).matches()) {
                return email;
            }
            System.out.println("Email invalido. Informe um email valido.");
        }
    }

    private String lerTexto(String mensagem, int tamanhoMinimo) {
        while (true) {
            System.out.print(mensagem);
            String texto = console.nextLine().trim();
            if (texto.isEmpty()) {
                return null;
            }
            if (texto.length() >= tamanhoMinimo) {
                return texto;
            }
            System.out.println("O valor deve ter no minimo " + tamanhoMinimo + " caracteres.");
        }
    }

    private boolean confirmar() {
        String resposta = console.nextLine().trim();
        return !resposta.isEmpty() && (resposta.charAt(0) == 'S' || resposta.charAt(0) == 's');
    }


    // Alguem por favor cria uma classe de criptografia migra essa funcao pra la
    private String gerarHash(String texto) throws Exception {
        byte[] hash = MessageDigest.getInstance("SHA-256")
                .digest(texto.getBytes(StandardCharsets.UTF_8));
        StringBuilder resultado = new StringBuilder();
        for (byte valor : hash) {
            resultado.append(String.format("%02x", valor));
        }
        return resultado.toString();
    }
}
