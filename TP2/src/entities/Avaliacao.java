package entities;

import aed3.InterfaceRegistro;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Avaliacao implements InterfaceRegistro {
    private int idAvaliacao;
    private int idUsuario;
    private int idResposta;
    private boolean avaliacao; // true para positiva (+1), false para negativa (-1)

    public Avaliacao() {
        this(-1, -1, -1, false);
    }

    public Avaliacao(int idUsuario, int idResposta, boolean avaliacao) {
        this(-1, idUsuario, idResposta, avaliacao);
    }

    public Avaliacao(int idAvaliacao, int idUsuario, int idResposta, boolean avaliacao) {
        this.idAvaliacao = idAvaliacao;
        this.idUsuario = idUsuario;
        this.idResposta = idResposta;
        this.avaliacao = avaliacao;
    }

    @Override
    public int getId() {
        return this.idAvaliacao;
    }

    @Override
    public void setId(int id) {
        this.idAvaliacao = id;
    }

    public int getIdAvaliacao() {
        return idAvaliacao;
    }

    public void setIdAvaliacao(int idAvaliacao) {
        this.idAvaliacao = idAvaliacao;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdResposta() {
        return idResposta;
    }

    public void setIdResposta(int idResposta) {
        this.idResposta = idResposta;
    }

    public boolean isAvaliacao() {
        return avaliacao;
    }

    public boolean getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(boolean avaliacao) {
        this.avaliacao = avaliacao;
    }

    @Override
    public byte[] serialize() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.idAvaliacao);
        dos.writeInt(this.idUsuario);
        dos.writeInt(this.idResposta);
        dos.writeBoolean(this.avaliacao);
        return baos.toByteArray();
    }

    @Override
    public void deserialize(byte[] data) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        this.idAvaliacao = dis.readInt();
        this.idUsuario = dis.readInt();
        this.idResposta = dis.readInt();
        this.avaliacao = dis.readBoolean();
    }

    @Override
    public String toString() {
        return "Avaliacao #" + this.idAvaliacao + " (Usuario: " + this.idUsuario
                + ", Resposta: " + this.idResposta + "): "
                + (this.avaliacao ? "Positiva (+1)" : "Negativa (-1)");
    }
}
