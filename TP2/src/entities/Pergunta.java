package entities;

import aed3.InterfaceRegistro;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Pergunta implements InterfaceRegistro {
    private int id;
    private int idUsuario;
    private long criacao;
    private long alteracao;
    private short nota;
    private String pergunta;
    private String palavrasChave;
    private boolean ativa;

    public Pergunta() {
        this(-1, -1, System.currentTimeMillis(), System.currentTimeMillis(), (short) 0, "", "", true);
    }

    public Pergunta(int idUsuario, String pergunta, String palavrasChave) {
        this(-1, idUsuario, System.currentTimeMillis(), System.currentTimeMillis(), (short) 0, pergunta, palavrasChave, true);
    }

    public Pergunta(int id, int idUsuario, long criacao, long alteracao, short nota, String pergunta, String palavrasChave, boolean ativa) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.criacao = criacao;
        this.alteracao = alteracao;
        this.nota = nota;
        this.pergunta = pergunta;
        this.palavrasChave = palavrasChave;
        this.ativa = ativa;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public int getIdPergunta() {
        return this.id;
    }

    public void setIdPergunta(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public long getCriacao() {
        return criacao;
    }

    public void setCriacao(long criacao) {
        this.criacao = criacao;
    }

    public long getAlteracao() {
        return alteracao;
    }

    public void setAlteracao(long alteracao) {
        this.alteracao = alteracao;
    }

    public short getNota() {
        return nota;
    }

    public void setNota(short nota) {
        this.nota = nota;
    }

    public String getPergunta() {
        return pergunta;
    }

    public void setPergunta(String pergunta) {
        this.pergunta = pergunta;
    }

    public String getPalavrasChave() {
        return palavrasChave;
    }

    public void setPalavrasChave(String palavrasChave) {
        this.palavrasChave = palavrasChave;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    @Override
    public byte[] serialize() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeInt(this.idUsuario);
        dos.writeLong(this.criacao);
        dos.writeLong(this.alteracao);
        dos.writeShort(this.nota);
        dos.writeUTF(this.pergunta);
        dos.writeUTF(this.palavrasChave);
        dos.writeBoolean(this.ativa);
        return baos.toByteArray();
    }

    @Override
    public void deserialize(byte[] data) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.idUsuario = dis.readInt();
        this.criacao = dis.readLong();
        this.alteracao = dis.readLong();
        this.nota = dis.readShort();
        this.pergunta = dis.readUTF();
        this.palavrasChave = dis.readUTF();
        this.ativa = dis.readBoolean();
    }

    @Override
    public String toString() {
        return "Pergunta #" + this.id + " (Usuario: " + this.idUsuario + "): " + this.pergunta + " [" + (this.ativa ? "Ativa" : "Arquivada") + "]";
    }
}