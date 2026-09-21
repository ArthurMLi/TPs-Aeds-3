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

    @Override
    public int create(Usuario usuario) throws Exception {
        int id = super.create(usuario);
        ParEmailId par = new ParEmailId(usuario.getEmail(), id);
        indiceEmail.create(par);
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

    @Override
    public boolean update(Usuario novo) throws Exception {
        Usuario antigo = super.read(novo.getId());
        if (antigo != null) {
            if (!antigo.getEmail().equalsIgnoreCase(novo.getEmail())) {
                indiceEmail.delete(ParEmailId.hash(antigo.getEmail()));
                indiceEmail.create(new ParEmailId(novo.getEmail(), novo.getId()));
            }
            return super.update(novo);
        }
        return false;
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
