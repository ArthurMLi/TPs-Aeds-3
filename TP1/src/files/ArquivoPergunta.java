package files;

import aed3.Arquivo;
import aed3.ArvoreBMais;
import aed3.ParIdId;
import entities.Pergunta;
import java.util.ArrayList;

public class ArquivoPergunta extends Arquivo<Pergunta> {

    ArvoreBMais<ParIdId> relUsuarioPergunta;

    public ArquivoPergunta() throws Exception {
        super("perguntas", Pergunta.class.getConstructor());
        relUsuarioPergunta = new ArvoreBMais<>(
            ParIdId.class.getConstructor(), 
            5,
            "./dados/perguntas/relUsuarioPergunta.db"
        );
    }

    @Override
    public int create(Pergunta pergunta) throws Exception {
        int id = super.create(pergunta);
        relUsuarioPergunta.create(new ParIdId(pergunta.getIdUsuario(), id));
        return id;
    }

    public Pergunta[] readAllByUsuario(int idUsuario) throws Exception {
        ArrayList<ParIdId> lista = relUsuarioPergunta.read(new ParIdId(idUsuario, -1));
        if (lista == null) {
            return new Pergunta[0];
        }

        ArrayList<Pergunta> aux = new ArrayList<>();
        for (int i = 0; i < lista.size(); i++) {
            ParIdId par = lista.get(i);
            Pergunta pergunta = super.read(par.getId2());
            if (pergunta != null) {
                aux.add(pergunta);
            }
        }

        Pergunta[] resp = new Pergunta[aux.size()];
        for (int i = 0; i < aux.size(); i++) {
            resp[i] = aux.get(i);
        }

        return resp;
    }

    @Override
    public boolean update(Pergunta pergunta) throws Exception {
        pergunta.setAlteracao(System.currentTimeMillis());
        return super.update(pergunta);
    }

    public boolean arquivar(int id) throws Exception {
        Pergunta pergunta = super.read(id);
        if (pergunta != null) {
            pergunta.setAtiva(false);
            return this.update(pergunta);
        }
        return false;
    }

    @Override
    public boolean delete(int id) throws Exception {
        Pergunta pergunta = super.read(id);
        if (pergunta != null) {
            relUsuarioPergunta.delete(new ParIdId(pergunta.getIdUsuario(), id));
            return super.delete(id);
        }
        return false;
    }

    public void deleteAllByUsuario(int idUsuario) throws Exception {
        ArrayList<ParIdId> lista = relUsuarioPergunta.read(new ParIdId(idUsuario, -1));
        if (lista != null) {
            for (int i = 0; i < lista.size(); i++) {
                ParIdId par = lista.get(i);
                super.delete(par.getId2());
                relUsuarioPergunta.delete(par);
            }
        }
    }
}
