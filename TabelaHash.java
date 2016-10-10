package p1;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class TabelaHash {
	private static long colisoesTotal = 0;
	private static long maxDeColisoesEmUmIndice = 0;
	private static double passosDeBusca = 0.0;
	private static int colisoesDeBusca = 0;
	
	
	public static void  criaTabela(RandomAccessFile f, long tamanho) throws IOException{
		System.out.println("Criando Tabela...");
		ElementoHash h = new ElementoHash();
		h.setCep(-1);
		h.setEndereco(-1);
		h.setProximo(-1);
			
		for(int i=0; i<tamanho; i++){
			h.escreve(f);
		}
		System.out.println("Tabela criada com sucesso!");
	}
	
	public static void criaHash(RandomAccessFile c, RandomAccessFile h, long tamanho) throws IOException{
		System.out.println("Criando Hash...");
		long i =0;
		
		Endereco e = new Endereco(); 
		ElementoHash hash = new ElementoHash();
		
		while(c.getFilePointer() < c.length()){
			e.leEndereco(c);
			long p = Long.parseLong(e.getCep())% tamanho;
			h.seek(p*24);
			hash.le(h);
			if(hash.getCep() == -1){
				hash.setCep(Long.parseLong(e.getCep()));
				hash.setEndereco(i);
				hash.setProximo(-1);
				h.seek(p*24);
				hash.escreve(h);
			}else{
				long prox = hash.getProximo();
				hash.setProximo(h.length());
				h.seek(p*24);
				hash.escreve(h);
				h.seek(h.length());
				hash.setCep(Long.parseLong(e.getCep()));
				hash.setEndereco(i);
				hash.setProximo(prox);
				hash.escreve(h);
				colisoesTotal++;
			}
			i++;
		}
		System.out.println("Hash criado com sucesso!");
	}
	
	public static Endereco consulta(long cep, RandomAccessFile h, RandomAccessFile c, long tamanho)throws IOException{
		ElementoHash hash = new ElementoHash(); 
		Endereco e = new Endereco();
		passosDeBusca++;
		colisoesDeBusca = 0;
		
		long p = cep % tamanho;
		h.seek(p*24);
		hash.le(h);
		if(hash.getProximo() != -1){
			while(hash.getCep() != cep){
				h.seek(hash.getProximo());
				hash.le(h);
				passosDeBusca++;
				colisoesDeBusca++;
			}
			if(colisoesDeBusca > maxDeColisoesEmUmIndice){
				maxDeColisoesEmUmIndice = colisoesDeBusca;
			}
		}
		c.seek(hash.getEndereco()*300);
		e.leEndereco(c);
		return e; 
	
	}
	
	public static void estatisticasDeHash(RandomAccessFile h, RandomAccessFile c, long tamanho) throws IOException{
		System.out.println("Gerando estatísticas do Hash...");
		
		Endereco e = new Endereco();
		c.seek(0);
		
		while(c.getFilePointer()<c.length()){
			long aux = c.getFilePointer(); 
			e.leEndereco(c);
			TabelaHash.consulta(Long.parseLong(e.getCep()), h, c, tamanho);
			c.seek(aux+300);
		}
		
		DecimalFormat df = new DecimalFormat("#0.000");
		DecimalFormat df2 = new DecimalFormat("#0");
		DecimalFormat df3 = new DecimalFormat("#0.000000");
		
		
		ArrayList<Double> listaColisoes = new ArrayList<Double>();
		long qtdIndiceVazio = 0;
		for(int i=0;i<tamanho; i++){
			double colisoesEmUmIndice = 0;
			ElementoHash hash = new ElementoHash(); 
			h.seek(i*24);
			hash.le(h);
			if(hash.getCep() != -1){
				while(hash.getProximo() != -1 ){
					h.seek(hash.getProximo());
					hash.le(h);
					colisoesEmUmIndice++;
				}
			}else{
				qtdIndiceVazio++;
			}
			listaColisoes.add(colisoesEmUmIndice);
		}
		
		double total = tamanho;
		
		System.out.println("Número de posições vazia no hash: " + qtdIndiceVazio);
		System.out.println("Load Factor: "+ df.format((c.length())/(total*300)));
		System.out.println("Número máximo de colisões em um mesmo índice: " + maxDeColisoesEmUmIndice);
		
		double media = passosDeBusca*300/c.length();
		System.out.println("A média de passos de busca é: " + df.format(media)); 
		for(double i=0.0; i<=maxDeColisoesEmUmIndice; i++){
			if(i==0.0){
				System.out.println("Existem " + (Collections.frequency(listaColisoes, i)- qtdIndiceVazio) +" campos com " + df2.format(i) + " colisões - Probabilidade em busca: "+ df3.format((Collections.frequency(listaColisoes, i)-qtdIndiceVazio)/(total - qtdIndiceVazio)));
			}else{
				System.out.println("Existem " + Collections.frequency(listaColisoes, i) +" campos com " + df2.format(i) + " colisões - Probabilidade em busca: " + df3.format(Collections.frequency(listaColisoes, i)/(total - qtdIndiceVazio)));
			}
		}
		System.out.println("Número de Colisões: " + colisoesTotal);	
	}
}
