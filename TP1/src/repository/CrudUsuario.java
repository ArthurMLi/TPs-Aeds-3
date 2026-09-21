package repository;

import entities.Usuario;
import files.ArquivoPergunta;
import files.ArquivoUsuario;
import util.Crypto;

import java.util.regex.Pattern;

/**
 * Camada de controle da entidade Usuario.
 *
 * Nao le nem escreve na tela: recebe dados ja lidos pela visao, aplica as
 * regras de negocio e conversa com o ArquivoUsuario. Os metodos que podem
 * falhar por regra devolvem uma String com a mensagem de erro, ou null quando
 * a operacao deu certo.
 */
public class CrudUsuario {

    private static final Pattern EMAIL_VALIDO =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final ArquivoUsuario arqUsuarios;
    private ArquivoPergunta arqPerguntas;

    public CrudUsuario() throws Exception {
        this.arqUsuarios = new ArquivoUsuario();
    }

    public CrudUsuario(ArquivoUsuario arqUsuarios) {
        this.arqUsuarios = arqUsuarios;
    }

    /** Permite a exclusao em cascata das perguntas quando o usuario e excluido. */
    public void setArquivoPergunta(ArquivoPergunta arqPerguntas) {
        this.arqPerguntas = arqPerguntas;
    }

    public ArquivoUsuario getArquivoUsuario() {
        return arqUsuarios;
    }

    // ------------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------------

    /** Busca pelo indice indireto (tabela hash extensivel) email -> idUsuario. */
    public Usuario buscarPorEmail(String email) {
        try {
            return arqUsuarios.readByEmail(email);
        } catch (Exception e) {
            return null;
        }
    }

    /** Busca pelo indice direto (ID -> endereco) embutido na classe Arquivo. */
    public Usuario buscarPorId(int id) {
        try {
            return arqUsuarios.read(id);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean emailJaCadastrado(String email) {
        return buscarPorEmail(email) != null;
    }

    public static boolean emailValido(String email) {
        return email != null && EMAIL_VALIDO.matcher(email).matches();
    }

    // ------------------------------------------------------------------
    // Inclusao
    // ------------------------------------------------------------------

    /**
     * Cadastra um novo usuario. A senha e a resposta secreta nunca sao
     * armazenadas: guarda-se apenas o hash SHA-256 delas. A resposta secreta
     * passa antes por normalizacao (sem acentos, minusculas) para tolerar
     * diferencas de digitacao na hora de recuperar a senha.
     */
    public String criar(String nome, String email, String senha,
                        String perguntaSecreta, String respostaSecreta) {
        String erro = validarDados(nome, email, senha, perguntaSecreta, respostaSecreta);
        if (erro != null) {
            return erro;
        }
        try {
            if (emailJaCadastrado(email)) {
                return "Ja existe um usuario cadastrado com esse e-mail.";
            }
            Usuario usuario = new Usuario(
                    nome.trim(),
                    email.trim(),
                    Crypto.hashSenha(senha),
                    perguntaSecreta.trim(),
                    Crypto.hashResposta(respostaSecreta));
            arqUsuarios.create(usuario);
            return null;
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Nao foi possivel criar o usuario: " + e.getMessage();
        }
    }

    private String validarDados(String nome, String email, String senha,
                                String perguntaSecreta, String respostaSecreta) {
        if (nome == null || nome.trim().length() < 4) {
            return "O nome deve ter no minimo 4 caracteres.";
        }
        if (!emailValido(email)) {
            return "E-mail invalido.";
        }
        if (senha == null || senha.length() < 4) {
            return "A senha deve ter no minimo 4 caracteres.";
        }
        if (perguntaSecreta == null || perguntaSecreta.trim().length() < 4) {
            return "A pergunta secreta deve ter no minimo 4 caracteres.";
        }
        if (respostaSecreta == null || respostaSecreta.trim().isEmpty()) {
            return "A resposta secreta nao pode ser vazia.";
        }
        return null;
    }

    // ------------------------------------------------------------------
    // Autenticacao e recuperacao de senha
    // ------------------------------------------------------------------

    /**
     * Valida e-mail e senha de uma vez so. Devolve o usuario autenticado ou
     * null; quem chama nao sabe qual dos dois campos falhou, de proposito.
     */
    public Usuario autenticar(String email, String senha) {
        Usuario usuario = buscarPorEmail(email);
        if (usuario == null || senha == null) {
            return null;
        }
        if (usuario.getHashSenha().equals(Crypto.hashSenha(senha))) {
            return usuario;
        }
        return null;
    }

    public boolean respostaSecretaCorreta(Usuario usuario, String resposta) {
        if (usuario == null || resposta == null) {
            return false;
        }
        return usuario.getHashRespostaSecreta().equals(Crypto.hashResposta(resposta));
    }

    // ------------------------------------------------------------------
    // Alteracoes
    // ------------------------------------------------------------------

    public String alterarNome(Usuario usuario, String novoNome) {
        if (novoNome == null || novoNome.trim().length() < 4) {
            return "O nome deve ter no minimo 4 caracteres.";
        }
        String anterior = usuario.getNome();
        usuario.setNome(novoNome.trim());
        String erro = gravar(usuario);
        if (erro != null) {
            usuario.setNome(anterior);
        }
        return erro;
    }

    /**
     * Altera o e-mail. Como o e-mail e a chave do indice indireto, o
     * ArquivoUsuario remove a entrada antiga e insere a nova; o ID do usuario
     * permanece o mesmo, e por isso nenhum outro indice precisa ser tocado.
     */
    public String alterarEmail(Usuario usuario, String novoEmail) {
        if (!emailValido(novoEmail)) {
            return "E-mail invalido.";
        }
        Usuario dono = buscarPorEmail(novoEmail);
        if (dono != null && dono.getId() != usuario.getId()) {
            return "Ja existe um usuario cadastrado com esse e-mail.";
        }
        String anterior = usuario.getEmail();
        usuario.setEmail(novoEmail.trim());
        String erro = gravar(usuario);
        if (erro != null) {
            usuario.setEmail(anterior);
        }
        return erro;
    }

    public String alterarSenha(Usuario usuario, String senhaAtual, String novaSenha) {
        if (senhaAtual != null && !usuario.getHashSenha().equals(Crypto.hashSenha(senhaAtual))) {
            return "Senha atual incorreta.";
        }
        if (novaSenha == null || novaSenha.length() < 4) {
            return "A nova senha deve ter no minimo 4 caracteres.";
        }
        String anterior = usuario.getHashSenha();
        usuario.setHashSenha(Crypto.hashSenha(novaSenha));
        String erro = gravar(usuario);
        if (erro != null) {
            usuario.setHashSenha(anterior);
        }
        return erro;
    }

    public String alterarPerguntaSecreta(Usuario usuario, String pergunta, String resposta) {
        if (pergunta == null || pergunta.trim().length() < 4) {
            return "A pergunta secreta deve ter no minimo 4 caracteres.";
        }
        if (resposta == null || resposta.trim().isEmpty()) {
            return "A resposta secreta nao pode ser vazia.";
        }
        String pAnterior = usuario.getPerguntaSecreta();
        String rAnterior = usuario.getHashRespostaSecreta();
        usuario.setPerguntaSecreta(pergunta.trim());
        usuario.setHashRespostaSecreta(Crypto.hashResposta(resposta));
        String erro = gravar(usuario);
        if (erro != null) {
            usuario.setPerguntaSecreta(pAnterior);
            usuario.setHashRespostaSecreta(rAnterior);
        }
        return erro;
    }

    private String gravar(Usuario usuario) {
        try {
            return arqUsuarios.update(usuario) ? null : "Usuario nao encontrado.";
        } catch (Exception e) {
            return "Nao foi possivel gravar a alteracao: " + e.getMessage();
        }
    }

    // ------------------------------------------------------------------
    // Exclusao
    // ------------------------------------------------------------------

    /**
     * Exclui o usuario. Como o relacionamento e 1:N e as perguntas dependem do
     * usuario, a exclusao e em cascata: primeiro as perguntas (arquivo de dados
     * e arvore B+), depois o usuario (arquivo de dados e indice de email).
     * Apagar o usuario primeiro deixaria perguntas orfas apontando para um ID
     * que nao existe mais.
     */
    public String excluir(int idUsuario) {
        try {
            Usuario usuario = arqUsuarios.read(idUsuario);
            if (usuario == null) {
                return "Usuario nao encontrado.";
            }
            if (arqPerguntas != null) {
                return arqUsuarios.delete(idUsuario, arqPerguntas)
                        ? null : "Nao foi possivel excluir o usuario.";
            }
            return arqUsuarios.delete(idUsuario) ? null : "Nao foi possivel excluir o usuario.";
        } catch (Exception e) {
            return "Nao foi possivel excluir o usuario: " + e.getMessage();
        }
    }

    public void fechar() throws Exception {
        arqUsuarios.close();
    }
}
