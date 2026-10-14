package entities;

import aed3.InterfaceRegistro;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Resposta implements InterfaceRegistro {
    private int idResposta;
    private int idPergunta;
    private int idUsuario;
    private long criacao;
    private String resposta;
    private short nota;
    private short avaliacoes;
    private boolean ativa;

    public Resposta() {
        this(-1, -1, -1, System.currentTimeMillis(), "", (short) 0, (short) 0, true);
    }

    public Resposta(int idPergunta, int idUsuario, String resposta) {
        this(-1, idPergunta, idUsuario, System.currentTimeMillis(), resposta, (short) 0, (short) 0, true);
    }

    public Resposta(int idResposta, int idPergunta, int idUsuario, long criacao, String resposta, short nota, short avaliacoes, boolean ativa) {
        this.idResposta = idResposta;
        this.idPergunta = idPergunta;
        this.idUsuario = idUsuario;
        this.criacao = criacao;
        this.resposta = resposta;
        this.nota = nota;
        this.avaliacoes = avaliacoes;
        this.ativa = ativa;
    }

    @Override
    public int getId() {
        return this.idResposta;
    }

    @Override
    public void setId(int id) {
        this.idResposta = id;
    }

    public int getIdResposta() {
        return idResposta;
    }

    public void setIdResposta(int idResposta) {
        this.idResposta = idResposta;
    }

    public int getIdPergunta() {
        return idPergunta;
    }

    public void setIdPergunta(int idPergunta) {
        this.idPergunta = idPergunta;
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

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public short getNota() {
        return nota;
    }

    public void setNota(short nota) {
        this.nota = nota;
    }

    public short getAvaliacoes() {
        return avaliacoes;
    }

    public void setAvaliacoes(short avaliacoes) {
        this.avaliacoes = avaliacoes;
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
        dos.writeInt(this.idResposta);
        dos.writeInt(this.idPergunta);
        dos.writeInt(this.idUsuario);
        dos.writeLong(this.criacao);
        dos.writeUTF(this.resposta);
        dos.writeShort(this.nota);
        dos.writeShort(this.avaliacoes);
        dos.writeBoolean(this.ativa);
        return baos.toByteArray();
    }

    @Override
    public void deserialize(byte[] data) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        this.idResposta = dis.readInt();
        this.idPergunta = dis.readInt();
        this.idUsuario = dis.readInt();
        this.criacao = dis.readLong();
        this.resposta = dis.readUTF();
        this.nota = dis.readShort();
        this.avaliacoes = dis.readShort();
        this.ativa = dis.readBoolean();
    }

    @Override
    public String toString() {
        return "Resposta #" + this.idResposta + " (Usuario: " + this.idUsuario + "): " + this.resposta + " [" + (this.ativa ? "Ativa" : "Arquivada") + "]";
    }
}