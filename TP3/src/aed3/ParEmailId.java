package aed3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParEmailId implements InterfaceHashExtensivel {

    private String email;
    private int id;
    public static final int TAMANHO_EMAIL = 100;
    private static final short TAMANHO = 104;

    /**
     * Construtor vazio, exigido pela HashExtensivel para montar os cestos.
     * Nao valida o email: o par vazio e apenas um espaco reservado no cesto.
     */
    public ParEmailId() {
        this.email = "";
        this.id = -1;
    }

    public ParEmailId(String email, int id) {
        if (email == null || !(email.contains("@") && email.contains("."))) {
            throw new IllegalArgumentException("Email invalido.");
        }
        if (email.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > TAMANHO_EMAIL) {
            throw new IllegalArgumentException(
                "Email muito longo (maximo de " + TAMANHO_EMAIL + " bytes).");
        }
        this.id = id;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return hash(this.email);
    }

    public static int hash(String email) {
        if (email == null)
            return 0;
        return Math.abs(email.trim().toLowerCase().hashCode() % Integer.MAX_VALUE);
    }

    @Override
    public short size() {
        return TAMANHO;
    }

    @Override
    public String toString() {
        return "(" + this.email + ";" + this.id + ")";
    }

    @Override
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        byte[] bytes = new byte[TAMANHO_EMAIL];
        byte[] emailBytes = this.email.getBytes("UTF-8");
        int len = Math.min(emailBytes.length, TAMANHO_EMAIL);
        for (int i = 0; i < len; i++) {
            bytes[i] = emailBytes[i];
        }
        for (int i = len; i < TAMANHO_EMAIL; i++) {
            bytes[i] = ' ';
        }
        dos.write(bytes);
        dos.writeInt(this.id);
        return baos.toByteArray();
    }

    @Override
    public void deserialize(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        byte[] bytes = new byte[TAMANHO_EMAIL];
        dis.readFully(bytes);
        this.email = new String(bytes, "UTF-8").trim().toLowerCase();
        this.id = dis.readInt();
    }
}
