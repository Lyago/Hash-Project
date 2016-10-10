package p1;

import java.io.RandomAccessFile;

public class Main {

	public static void main(String[] args) throws Exception 
	{
		long inicial = System.currentTimeMillis();
		RandomAccessFile ceps = new RandomAccessFile("cep.dat", "r");
		RandomAccessFile hash = new RandomAccessFile("hash.dat", "rw");

		TabelaHash.criaTabela(hash, 900001);
		TabelaHash.criaHash(ceps, hash, 900001);
		TabelaHash.estatisticasDeHash(hash, ceps, 900001);
		TabelaHash.consulta(22750008, hash, ceps, 900001);
		System.out.println("Tempo decorrido: " +(System.currentTimeMillis() - inicial) + " ms");
		ceps.close();
		hash.close();
	}

}
