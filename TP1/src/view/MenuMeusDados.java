package view;

import entities.Usuario;
import repository.CrudUsuario;

/**
 * Alteracao dos dados do proprio usuario.
 *
 * O ID nunca aparece nem pode ser alterado: ele e interno ao sistema e e o que
 * mantem os indices secundarios validos quando o e-mail muda.
 */
public class MenuMeusDados {

    private final CrudUsuario crudUsuario;

    public MenuMeusDados(CrudUsuario crudUsuario) {
        this.crudUsuario = crudUsuario;
    }

    public void mostrar(Usuario usuario) {
        char opcao;
        do {
            Console.cabecalho("Início > Minha área > Meus dados");
            Console.mensagem("Nome ...........: " + usuario.getNome());
            Console.mensagem("E-mail .........: " + usuario.getEmail());
            Console.mensagem("Pergunta secreta: " + usuario.getPerguntaSecreta());
            Console.mensagem("");
            Console.mensagem("(A) Alterar nome");
            Console.mensagem("(B) Alterar email");
            Console.mensagem("(C) Alterar senha");
            Console.mensagem("(D) Alterar pergunta e resposta de recuperação da senha");
            Console.mensagem("(R) Retornar ao menu anterior");
            Console.mensagem("");
            opcao = Console.lerOpcao();

            switch (opcao) {
                case 'A':
                    alterarNome(usuario);
                    break;
                case 'B':
                    alterarEmail(usuario);
                    break;
                case 'C':
                    alterarSenha(usuario);
                    break;
                case 'D':
                    alterarPerguntaSecreta(usuario);
                    break;
                case 'R':
                    break;
                default:
                    Console.opcaoInvalida();
            }
        } while (opcao != 'R');
    }

    private void alterarNome(Usuario usuario) {
        Console.cabecalho("Início > Minha área > Meus dados > Alterar nome");
        Console.mensagem("Nome atual: " + usuario.getNome());
        String novo = Console.lerTexto("Novo nome (ENTER cancela): ", 4);
        if (novo == null) {
            return;
        }
        relatar(crudUsuario.alterarNome(usuario, novo), "Nome alterado.");
    }

    /**
     * Alteracao do e-mail.
     *
     * O e-mail e a chave do indice indireto (tabela hash extensivel), e chave
     * de indice nao se altera no lugar: o ArquivoUsuario remove o par
     * (emailAntigo -> id) e insere (emailNovo -> id). O ID do usuario continua
     * o mesmo, por isso a arvore B+ do relacionamento 1:N nao e tocada e as
     * perguntas continuam vinculadas normalmente.
     */
    private void alterarEmail(Usuario usuario) {
        Console.cabecalho("Início > Minha área > Meus dados > Alterar email");
        Console.mensagem("E-mail atual: " + usuario.getEmail());
        String novo = Console.lerTexto("Novo e-mail (ENTER cancela): ", 1);
        if (novo == null) {
            return;
        }
        relatar(crudUsuario.alterarEmail(usuario, novo),
                "E-mail alterado. Use o novo e-mail no próximo acesso.");
    }

    private void alterarSenha(Usuario usuario) {
        Console.cabecalho("Início > Minha área > Meus dados > Alterar senha");
        String atual = Console.lerTexto("Senha atual (ENTER cancela): ", 1);
        if (atual == null) {
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
        relatar(crudUsuario.alterarSenha(usuario, atual, nova), "Senha alterada.");
    }

    private void alterarPerguntaSecreta(Usuario usuario) {
        Console.cabecalho("Início > Minha área > Meus dados > Recuperação de senha");
        Console.mensagem("Pergunta atual: " + usuario.getPerguntaSecreta());
        String pergunta = Console.lerTexto("Nova pergunta secreta (mín. 4 caracteres, ENTER cancela): ", 4);
        if (pergunta == null) {
            return;
        }
        String resposta = Console.lerTexto("Nova resposta secreta (ENTER cancela): ", 1);
        if (resposta == null) {
            return;
        }
        relatar(crudUsuario.alterarPerguntaSecreta(usuario, pergunta, resposta),
                "Pergunta e resposta de recuperação alteradas.");
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
