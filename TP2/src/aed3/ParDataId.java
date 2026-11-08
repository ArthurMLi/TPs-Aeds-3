package aed3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParDataId implements InterfaceArvoreBMais<ParDataId>{
    private int id;
    private long data;
    private final static short tamanho = 12; 

    public ParDataId() {
        this(0, -1);
    }

    public ParDataId(long data, int id) {
        this.data = data;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    public short size(){
        return tamanho;
    }; // tamanho FIXO do registro

  @Override
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeLong(this.data);
        dos.writeInt(this.id);
        return baos.toByteArray();
    } // representação do elemento em um vetor de bytes

  
    @Override
    public void deserialize(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.data = dis.readLong();
        this.id = dis.readInt();
    }
    @Override
    public ParDataId clone() {
        return new ParDataId(this.data, this.id);
    }

  @Override
  public int compareTo(ParDataId a) {
      int comparacaoData = Long.compare(this.data, a.data);
      return comparacaoData != 0 ? comparacaoData : Integer.compare(this.id, a.id);
  }

  @Override
  public String toString() {
    return this.data + ";" + String.format("%-3d", this.id);
  }
}
