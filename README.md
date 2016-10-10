# Hash_Project

Trabalho para a matéria Organização de Estrutura de Arquivos do Bacharelado em Ciência da Computação no CEFET/RJ, ministrada pelo professor Renato Mauro.

##O Trabalho

* O trabalho consiste na criação de um Hash Table(Tabela de Dispersão), gerado a partir de uma função hash, para o a arquivo cep.dat, cedido pelo professor Renato. 
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

}``` 

  * O método abaixo cria um arquivo apartir da Classe ElementoHash, que tem como parametros: cep, endereco e proximo. O endereco é a posição do cep no arquivo cep.dat e o proximo e um ponteiro para o proximo Elemento, caso haja colisão.
  
	```java
	  public static void criaHash(RandomAccessFile f, long n) throws Exception{
			Elemento h = new Elemento();
			h.setCep(-1);
			h.setEndereco(-1);
			h.setProximo(-1);
			for(int i = 0; i < n; i++){
				h.escreveCep(f);				
			}
		}
	```
 * Outro método presente na Classe Hash cria o índice, adicionando os ceps ao novo arquivo.
    ```java
    	public static void criaIndice(RandomAccessFile f, RandomAccessFile r, long n) throws Exception{
		long i = 0;
		long p = 0;
		Elemento h = new Elemento();
		Endereco e = new Endereco();
		while(f.getFilePointer() < f.length()){
			e.leEndereco(f);
			p = Long.parseLong(e.getCep()) % n;
			r.seek(p*24);
			h.leCep(r);
    ```
   
   	 Com isso percorremos o arquivo cep.dat lendo os ceps, atraves da Classe Endereco. Depois de ler o cep é aplicado a função hash,  a qual é dada pelo resto da divisão do cep por 900001 (valor fornecido pelo professor Renato). Movemos a cabeça de leitura para a posição corresposdente ao resultado da função, no arquivo índice e lemos o que há nessa posição.
 	```java
		if(h.getCep() == -1){
				h.setCep(Long.parseLong(e.getCep()));
				h.setEndereco(i);
				h.setProximo(-1);
				r.seek(p*24);
				h.escreveCep(r);				
			}
	```
	Dentro do While há um if, que se nessa posição do arquivo índice o cep for igual a -1 nós setamos os atributos de um objeto Elemento. Cep, como o cep oriundo do arquivo cep.dat. A posição damos o número do registro daquele cep no aquivo original, pois há uma leitura sequencial do arquivo. Enquanto lemos guardamos a posição em uma variável, que incrementa uma unidade ao final de cada passagem pelo While. Retornamos a cabeça de leitura a posição inical do registro, pois ao lermos um registro a cabeça de leitura estará no registro seguinte, e escrevemos no arquivo com o auxílio da Classe Elemento.
	```java
		else{
				long prox = h.getProximo();
				h.setProximo(r.length());
				r.seek(p*24);
				h.escreveCep(r);
				r.seek(r.length());
				h.setCep(Long.parseLong(e.getCep()));
				h.setEndereco(i);
				h.setProximo(prox);
				h.escreveCep(r);				
			}i++;		
		}
	```
	Se a condição do if não for satisfeita é porque há um registro escrito nessa posição, portanto temos uma colisão. Com isso começamos o tratamento dessa colisão criando uma variável prox, que armazena o campo proximo escrito nesse regisrto do arquivo. Alteramos apenas o atributo proximo do registro lido como o final do arquivo e o reescrevemos. Depois disso movemos a cabeça de leitura para o final do arquivo e escrevemos o registro do aqruivo original. Após isso como já dito a cima há o incremento da variável de controle de registros e o loop continua até chegar ao fim do arquivo cep.dat.
 
 * Com o arquivo de índice criado podemos fazer buscas neles e encontrar o registro completo no arquivo original para ver os seus dados. Pois a busca a partir desse índice baseado em uma função hash é muito mais rápido, como veremos daqui a pouco, a média de passos para achar um arquivo é de 1,76.
 	```java
		public static void buscaHash(RandomAccessFile r, RandomAccessFile f, long cep, long n) throws Exception{
			Elemento h = new Elemento();
			long p = cep % n;
			r.seek(p*24);
			h.leCep(r);
			while(h.getCep() != cep && h.getProximo() != -1){
				r.seek(h.getProximo());
				h.leCep(r);
			}
			if(h.getCep() == cep){
				f.seek(h.getEndereco()*300);
				Endereco e = new Endereco();
				e.leEndereco(f);
				System.out.println(e.getLogradouro());
				System.out.println(e.getBairro());
				System.out.println(e.getCidade());
				System.out.println(e.getEstado());
				System.out.println(e.getSigla());
				System.out.println(e.getCep());			
			}else{
				System.out.println("Cep não encontrado!");
			}
		}
	```
	Esse método recebe o cep desejado como parametro, aplica a função de hash nele e vai para a posição no arquivo índice. Com isso há a verificação se o cep lido é o procurado, se não for e houver colisões, avançamos pelo encadeamento até encontra-lo. Se o cep for encontrado nós lemos a coluna posicao e posicionamos a cabeça de leitura na posição recebida no arquivo original. Lemos a linha correspondente ao cep e exibimos na tela as informações sobre ele. Se o cep não for encontrado exibimos uma mensagem ao usuário.
	
 * O último método presente na Classe Hash é usado para fornecer estatísticas sobre o arquivo índice gerado baseado na função hash.
 	```java
		public static void estatisticasHash(RandomAccessFile r, long n) throws Exception{
			System.out.println("-----------------------------------------------------");
			Endereco e = new Endereco();
			ArrayList<Integer> hash = new ArrayList<>();
			for(int i = 0; i < n; i++){
				hash.add(0);
			}

			int cep = 0;
			int fHash = 0 ;

			while(r.getFilePointer() < r.length()){
				e.leEndereco(r);
				cep = Integer.parseInt(e.getCep());
				fHash = (int) (cep % n);
				Integer novo = hash.get(fHash) + 1;
				hash.set(fHash, novo);			
			}
	```
	Nós criamos um ArrayList para emular o arquivo índice e o preenchemos com zeros. Após isso lemos o arquivo cep.dat e aplicamos a função hash sobre o cep e incrementamos 1 na posição correpondente.
	```java
		int colisoes = 0;
		
		for(int i = 0; i < hash.size();i++){
			int proc  = hash.get(i);
			if(proc>1){
				int aux = proc -1;
				colisoes+=aux;
			}
		}
		
		System.out.println("-----------------------------------------------------");
		System.out.println("O número total de colisões é de: " + colisoes);
		System.out.println("-----------------------------------------------------");
		
		int total = 0;
		int max = Collections.max(hash);
		
		for(int i = 0; i <= max; i++){
			int freq = Collections.frequency(hash, i);
			total += (i * freq);
			System.out.println("Existem "+ freq + " campos com "+ i +" elementos!");
		}
	```
 	Com o ArrayList pronto começamos a reunir informações sobre ele. A primeira é o número de colisões que ocorrem utilizando essa função. Outro dado é quantas vezes cada numero de colisão ocorre, nele utlizamos métodos de Collections para determinar o maior número que aparece (Collection.max()) e para vermos a quantidade de vezes que um número se repete (Collections.frequency()).
	```java
		double buscas = 0;
		
		double somatorio = 0;
		int fator = 0;
		DecimalFormat df = new DecimalFormat("#0.0000000000");
		System.out.println("-----------------------------------------------------");
		
		for(int i = 1; i <= max; i++){
			for(int j = i; j <= max; j++){
				int casos = Collections.frequency(hash, j);
				buscas+=casos;
			}
			double prob = (buscas/total);
			fator+=i;
			somatorio += Collections.frequency(hash, i) * fator;
			System.out.println("A probabilidade de achar o cep com "+ i + " passos e de : "+  df.format(prob));
			buscas = 0;
		}
		System.out.println("-----------------------------------------------------");
		double media = somatorio/total;		
		System.out.println("A média de passos para se achar um cep é de :" + df.format(media) );
	}	
}	
	```
	Nesse trecho calculamos as probabilidade de achar o cep, a partir do número de passos e calculamos a média de passos que uma busca leva.
