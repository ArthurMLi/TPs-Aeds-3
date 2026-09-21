package view;

import entities.Usuario;
import repository.CrudUsuario;

/**
 * Tela de acesso ao sistema: login, cadastro de novo usuario e recuperacao de
 * senha pela pergunta secreta. E o primeiro menu que o usuario ve.
 */
public class MenuAcesso {

    private final CrudUsuario crudUsuario;
    private final MenuPrincipal menuPrincipal;

    public MenuAcesso(CrudUsuario crudUsuario, MenuPrincipal menuPrincipal) {
        this.crudUsuario = crudUsuario;
        this.menuPrincipal = menuPrincipal;
    }

    public void mostrar() {
        char opcao;
        do {
            Console.cabecalho(null);
            Console.mensagem("(A) Login");
            Console.mensagem("(B) Novo usuário (primeiro acesso)");
            Console.mensagem("(S) Sair");
            Console.mensagem("");
            opcao = Console.lerOpcao();

            switch (opcao) {
                case 'A':
                    login();
                    break;
                case 'B':
                    novoUsuario();
                    break;
                case 'S':
                    Console.mensagem("\nAté logo!");
                    break;
                default:
                    Console.opcaoInvalida();
            }
        } while (opcao != 'S');
    }

    // ------------------------------------------------------------------
    // Login
    // ------------------------------------------------------------------

    /**
     * Valida e-mail e senha de uma vez. A mensagem de erro nao revela qual dos
     * dois campos esta errado, para nao entregar a um curioso se determinado
     * e-mail existe no sistema.
     */
    private void login() {
        Console.cabecalho("Login");
        String email = Console.lerTexto("E-mail (ENTER cancela): ", 1);
        if (email == null) {
            return;
        }
        String senha = Console.lerTexto("Senha (ENTER cancela): ", 1);
        if (senha == null) {
            return;
        }

        Usuario usuario = crudUsuario.autenticar(email, senha);
        if (usuario != null) {
            Console.sucesso("Bem-vindo(a), " + usuario.getNome() + "!");
            menuPrincipal.mostrar(usuario);
            return;
        }

        Console.erro("E-mail ou senha incorretos.");
        Console.mensagem("(A) Tentar novamente");
        Console.mensagem("(B) Recuperar senha");
        Console.mensagem("(R) Retornar ao menu anterior");
        Console.mensagem("");
        char opcao = Console.lerOpcao();
        if (opcao == 'A') {
            login();
        } else if (opcao == 'B') {
            recuperarSenha(email);
        }
    }

    // ------------------------------------------------------------------
    // Recuperacao de senha
    // ------------------------------------------------------------------

    /**
     * Recuperacao pela pergunta secreta. A resposta armazenada e um hash da
     * versao normalizada (sem acentos, em minusculas), entao "São Paulo",
     * "sao paulo" e "SAO PAULO" sao aceitas igualmente.
     */
    private void recuperarSenha(String emailSugerido) {
        Console.cabecalho("Recuperação de senha");

        String email = emailSugerido;
        if (email == null || email.isEmpty()) {
            email = Console.lerTexto("E-mail (ENTER cancela): ", 1);
            if (email == null) {
                return;
            }
        } else {
            Console.mensagem("E-mail: " + email);
        }

        Usuario usuario = crudUsuario.buscarPorEmail(email);
        if (usuario == null) {
            Console.erro("Usuário não encontrado.");
            Console.pausar();
            return;
        }

        Console.mensagem("\nPergunta secreta: " + usuario.getPerguntaSecreta());
        String resposta = Console.lerTexto("Resposta (ENTER cancela): ", 1);
        if (resposta == null) {
            return;
        }
        if (!crudUsuario.respostaSecretaCorreta(usuario, resposta)) {
            Console.erro("Resposta incorreta.");
            Console.pausar();
            return;
        }

        String nova = Console.lerTexto("Nova senha (mín. 4 caracteres, ENTER cancela): ", 4);
        if (nova == null) {
            return;
        }
        String confirmacao = Console.lerTexto("Confirme a nova senha: ", 1);
        if (confirmacao == null || !nova.equals(confirmacao)) {
            Console.erro("As senhas não conferem.");
            Console.pausar();
            return;
        }

        // senhaAtual = null porque a identidade ja foi provada pela resposta secreta
        String erro = crudUsuario.alterarSenha(usuario, null, nova);
        if (erro != null) {
            Console.erro(erro);
        } else {
            Console.sucesso("Senha alterada. Faça o login com a nova senha.");
        }
        Console.pausar();
    }

    // ------------------------------------------------------------------
    // Novo usuario
    // ------------------------------------------------------------------

    /**
     * Cadastro de novo usuario. O e-mail vem primeiro porque e a chave de
     * acesso: se ja existir, nao faz sentido pedir o resto dos dados.
     */
    private void novoUsuario() {
        Console.cabecalho("Novo usuário");

        String email;
        while (true) {
            email = Console.lerTexto("E-mail (ENTER cancela): ", 1);
            if (email == null) {
                return;
            }
            if (!CrudUsuario.emailValido(email)) {
                Console.erro("E-mail inválido. Informe um e-mail no formato nome@dominio.com.");
                continue;
            }
            if (crudUsuario.emailJaCadastrado(email)) {
                Console.erro("Este e-mail já está cadastrado. Informe outro.");
                continue;
            }
            break;
        }

        String nome = Console.lerTexto("Nome completo (mín. 4 caracteres, ENTER cancela): ", 4);
        if (nome == null) {
            return;
        }
        String senha = Console.lerTexto("Senha (mín. 4 caracteres, ENTER cancela): ", 4);
        if (senha == null) {
            return;
        }
        Console.mensagem("\nA pergunta secreta será usada para recuperar a senha.");
        String pergunta = Console.lerTexto("Pergunta secreta (mín. 4 caracteres, ENTER cancela): ", 4);
        if (pergunta == null) {
            return;
        }
        String resposta = Console.lerTexto("Resposta secreta (ENTER cancela): ", 1);
        if (resposta == null) {
            return;
        }

        if (!Console.confirmar("\nConfirma a criação do usuário?")) {
            Console.mensagem("Cadastro cancelado.");
            Console.pausar();
            return;
        }

        String erro = crudUsuario.criar(nome, email, senha, pergunta, resposta);
        if (erro != null) {
            Console.erro(erro);
        } else {
            Console.sucesso("Usuário cadastrado. Faça o login para acessar o sistema.");
        }
        Console.pausar();
    }
}
