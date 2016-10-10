# Hash_Project

Trabalho para a matéria Organização de Estrutura de Arquivos do Bacharelado em Ciência da Computação no CEFET/RJ, ministrada pelo professor Renato Mauro.

##O Trabalho

* O trabalho consiste na criação de um Hash Table (Tabela de Dispersão), gerado a partir de uma função hash, para o a arquivo cep.dat, cedido pelo professor Renato. 
* Além de criar a tabela, o programa também deverá efetuar buscas dos ceps contindos no arquivo cep.dat, exibindo os dados completos do cep correnpondente.
* O programa também exibe informações estatísticas sobre o hash, tal como: 
    * Número de colisões
    * Colisões por posição da tabela
    * Posições vazias da tabela
    * Load Factor
    * Média de passos para achar um cep
    * Probabilidades

* Com essas informações, além de se ter uma melhor compreenção dos mecânismos envolvendo a implementação e uso da tabela, também será realizado uma análise da eficácia e conveniência de uso do hash table.
* Java foi a linguagem de programação escolhida para realizar esse trabalho.

##Código

* O Programa é divido em 4 classes, sendo 2 delas para auxílio na leitura e escrita dos arquivos.

  * A classe Endereco é utilizada na leitura do arquivo cep.dat.

  * A classe Elemento é utilizada na leitura e na escrita do arquivo hash.
  
  
* As demais classes são as classes Main, que possui o método main, e TabelaHash que possui os métodos para a criação da hash table baseada em uma função hash.

  * Na classe Main observamos o seguinte código:
   	```java
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
	```

  	Neste código, são instânciados os objetos do tipo RandomAccessFile que usaremos no acesso dos arquivos em disco. Quatro métodos 	estáticos da classe TabelaHash são então chamados. 

  * O primeiro método, criaTabela(RandomAccessFile f, long n), pode ser observado abaixo 
  
	```java
	 public static void criaTabela(RandomAccessFile f, long tamanho) throws IOException{
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
	```
	Um arquivo é criado apartir da classe ElementoHash, que tem como atributos: cep, endereco e proximo. O endereco é a posição 		do cep no arquivo cep.dat e o proximo e um ponteiro para o proximo Elemento, caso haja colisão. Ao final de sua execução,
	teremos uma tabela vazia em disco.
	
 * O próximo método a ser chamado pelo main, criaHash(RandomAccessFile f, RandomAccessFile r, long n),  é responsável por realizar a leitura dos ceps em disco, e o processo de dispersão dos elementos pela tabela, nesse caso os ceps. 
    ```java
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
	```
	O algoritimo implementado percorre o arquivo cep.dat e aplica a função de hash: cep % n, onde n é o tamanho estipulado para a tabela. A função hash retorna a posição em que o cep em questão será armazenado no arquivo hash.dat. Com esse dado, o algoritimo escreve, com auxílio da classe ElementoHash, o cep, o endereco e o proximo; 3 atributos do tipo *long*, o que significa um tamanho de registro de *24 bytes*. Pode-se observar que há um if dentro do loop do while para tratar as *colisões*, isto é, quando o algoritimo insere um cep em uma posição já ocupada por outro cep. A *colisão* é tratada de maneira que, toda vez que ela ocorre, o cep é armazenado no final do arquivo hash.dat e uma referência do endereço é armazenada no atributo *proximo* do ElementoHash na posição da colisão. Dessa maneira temos uma estrutura encadeada que começa no elemento da tabela e através das referências de *próximo*, segue pelo final do arquivo. Repare também que há um atributo da classe sendo incrementado toda vez que ocorre uma colisão, ele será útil para análisar o hash posteriormente.
 
  * Em seguida, temos a chamada do método estatisticasDeHash(RandomAccessFile h, RandomAccessFile c, long tamanho) é usado para fornecer estatísticas sobre o arquivo hash gerado baseado.
 	```java
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
					while(hash.getProximo() != -1 )
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
	```
	Ao final deste método, serão imprimidas no console as estatísticas que fornecerão os dados para a análise do hash. Para melhor entendimento do código, analisaremos por partes.
	
	```java
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
		
	```
	Neste primeiro trecho, o algoritimo percorre o arquivo cep.dat e faz uma consulta no hash.dat pra cada um dos ceps. O método consulta(long cep, RandomAccessFile h, RandomAccessFile c, long tamanho) também faz parte da classe TabelaHash e pode ser observado abaixo.

	```java
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
	```
	Este método nos retorna o endereço correspondente ao cep procurado, utilizando a Classe Endereco. Nele, é aplicado ao cep procurado a função de hash e assim é possível sabermos o endereço em que esse cep está armazenado no hash.dat. Novamente, um if é usado no caso de *colisões*, isto é, se o cep procurado está em uma posição no hash onde houve colisões. Um atributo da classe, *passosDeBusca*, é incrementado para contar quantos *passos* foram feitos até se achar o cep desejado. A variável local *colisoesDeBusca* é utilizada, em cada busca, para contar quantas colisões há até o termino da busca. Por fim, outro atributo da classe é utilizado para armazenar a maior ocorrência de *colisoesDeBusca*.
	
  *Com as buscas realizadas e seus dados importântes armazanados, iniciamos uma simulação do hash em um ArrayList no trecho de código abaixo.
  	
	```java
			ArrayList<Double> listaColisoes = new ArrayList<Double>();
			long qtdIndiceVazio = 0;
			for(int i=0;i<tamanho; i++){
				double colisoesEmUmIndice = 0;
				ElementoHash hash = new ElementoHash(); 
				h.seek(i*24);
				hash.le(h);
				if(hash.getCep() != -1){
					while(hash.getProximo() != -1 )
						h.seek(hash.getProximo());
						hash.le(h);
						colisoesEmUmIndice++;
					}
				}else{
					qtdIndiceVazio++;
				}
				listaColisoes.add(colisoesEmUmIndice);
			}

	```
	O ArrayList é criado e é preenchido de acordo com o conteúdo de hash.dat. No processo, podemos contar quantas das posições do hash estão vazias e armazenar o dado na numa variável local, *qtdIndiceVazio*. Ao termino desse trecho, temos uma lista onde os elementos são o número de colisões em cada posição do hash.
	
  * Por fim, imprimimos os dados pertinentes para realizarmos a análise do hash
	
	```java
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
	```
