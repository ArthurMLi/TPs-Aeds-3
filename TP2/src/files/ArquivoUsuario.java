package files;

import aed3.Arquivo;
import aed3.HashExtensivel;
import aed3.ParEmailId;
import entities.Pergunta;
import entities.Usuario;
import java.io.IOException;

public class ArquivoUsuario extends Arquivo<Usuario> {

    HashExtensivel<ParEmailId> indiceEmail;

    public ArquivoUsuario() throws Exception {
        super("usuarios", Usuario.class.getConstructor());
        indiceEmail = new HashExtensivel<>(
            ParEmailId.class.getConstructor(),
            4,
            "./dados/usuarios/indiceEmail.diretorio.db",
            "./dados/usuarios/indiceEmail.cestos.db"
        );
    }

    /**
     * Inclui um usuario no arquivo e no indice indireto de email.
     * A verificacao de email duplicado acontece ANTES da gravacao: se o par
     * fosse rejeitado pelo indice depois do super.create(), o registro ja
     * estaria gravado no arquivo de dados e ficaria orfao (invisivel na busca
     * por email, mas ocupando um ID e aparecendo no readAll).
     */
    @Override
    public int create(Usuario usuario) throws Exception {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario nao pode ser nulo.");
        }
        if (readByEmail(usuario.getEmail()) != null) {
            throw new Exception("Ja existe um usuario cadastrado com esse e-mail.");
        }
        // Monta o par antes de gravar: se o email for invalido ou longo demais,
        // o construtor lanca excecao aqui, sem sujar o arquivo de dados.
        ParEmailId par = new ParEmailId(usuario.getEmail(), -1);
        int id = super.create(usuario);
        indiceEmail.create(new ParEmailId(par.getEmail(), id));
        return id;
    }

    public Usuario readByEmail(String email) throws Exception {
        if (email == null) {
            return null;
        }
        int hash = ParEmailId.hash(email);
        ParEmailId par = indiceEmail.read(hash);
        if (par != null) {
            return super.read(par.getId());
        }
        return null;
    }

    /**
     * Altera um usuario. Quando o email muda, a chave do indice muda junto:
     * como chave de indice nao se altera no lugar, a entrada antiga e removida
     * e a nova e inserida. O arquivo de dados e gravado antes do indice para
     * que um erro na gravacao nao deixe o indice apontando para algo que nao
     * foi alterado.
     */
    @Override
    public boolean update(Usuario novo) throws Exception {
        Usuario antigo = super.read(novo.getId());
        if (antigo == null) {
            return false;
        }
        boolean emailMudou = !antigo.getEmail().equalsIgnoreCase(novo.getEmail());
        if (emailMudou) {
            Usuario dono = readByEmail(novo.getEmail());
            if (dono != null && dono.getId() != novo.getId()) {
                throw new Exception("Ja existe um usuario cadastrado com esse e-mail.");
            }
            // Valida o novo email antes de mexer em qualquer arquivo.
            new ParEmailId(novo.getEmail(), novo.getId());
        }
        if (!super.update(novo)) {
            return false;
        }
        if (emailMudou) {
            indiceEmail.delete(ParEmailId.hash(antigo.getEmail()));
            indiceEmail.create(new ParEmailId(novo.getEmail(), novo.getId()));
        }
        return true;
    }

    @Override
    public boolean delete(int id) throws Exception {
        Usuario usuario = super.read(id);
        if (usuario != null) {
            indiceEmail.delete(ParEmailId.hash(usuario.getEmail()));
            return super.delete(id);
        }
        return false;
    }

    public boolean delete(int id, ArquivoPergunta arqPerguntas) throws Exception {
        if (arqPerguntas != null) {
            Pergunta[] perguntas = arqPerguntas.readAllByUsuario(id);
            for (int i = 0; i < perguntas.length; i++) {
                arqPerguntas.delete(perguntas[i].getId());
            }
        }
        return this.delete(id);
    }

    @Override
    public void close() throws IOException {
        indiceEmail.close();
        super.close();
    }
}
