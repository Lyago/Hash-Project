package p1;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ElementoHash {
	
	private long cep;
	private long endereco;
	private long proximo;

	public void le(DataInput din) throws IOException
	{
		this.cep = din.readLong();
		this.endereco = din.readLong();
		this.proximo = din.readLong();
	}

	public void escreve(DataOutput dout) throws IOException
	{
		dout.writeLong(this.cep);
		dout.writeLong(this.endereco);
		dout.writeLong(this.proximo);
	}
	
	public long getEndereco() {
		return endereco;
	}

	public void setEndereco(long endereco) {
		this.endereco = endereco;
	}

	public long getProximo() {
		return proximo;
	}

	public void setProximo(long proximo) {
		this.proximo = proximo;
	}

	public long getCep() {
		return cep;
	}
	
	public void setCep(long cep) {
		this.cep = cep;
	}

}
