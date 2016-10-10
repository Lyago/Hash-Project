# ARQ_Hash

Trabalho para a matéria Organização de Estrutura de Arquivos do Bacharelado em Ciência da Computação no CEFET/RJ, ministrada pelo professor Renato Mauro.

##O Trabalho

* O trabalho consiste na criação de um índice, gerado a partir de uma função hash, para o a arquivo cep.dat, cedido pelo professor Renato. 
* Após a criação do índice devemos efetuar buscas de ceps nele e exibirmos os dados completos daquele cep, oriundos do arquivo original.
* O programa também exibe informações sobre o hash, tal como: 
    * Número de colisões
    * Média de passos para achar um cep
    * Probabilidades

##Código

* O Programa é divido em 4 arquivos, sendo 2 deles para auxílio na leitura e escrita dos arquivos.

  * O arquivo Endereco.java, cedido pelo professor Renato, é utilizado na leitura do arquivo binário cep.dat.

  * O arquivo Elemento.java é utilizado na leitura e na escrita do arquivo índice.
  
  
* O arquivo Hash.java contém os métodos da Classe Hash para a criação do índice baseada em uma função hash.
  * O método abaixo cria um arquivo apartir da Classe Elemento, que tem como parametros: cep, endereco e proximo. O endereco é a posição do cep no arquivo cep.dat e o proximo e um ponteiro para o proximo Elemento, caso haja colisão.
  
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
	
* No arquivo Principal.java há o método main e um menu para facilitar o uso.
	```java
		public static void main(String[] args) throws Exception{
		Scanner s1 = new Scanner(System.in);
		System.out.println("Digite 1 para criar o indice Hash de um arquivo");
		System.out.println("Digite 2 para procurar um cep");
		System.out.println("Digite 3 para para ver as estaticas simuladas para o hash");
		int i = s1.nextInt();
		int conf = 0;
		switch (i)
			{

	```
	O usuário recebe as mensagens na tela com as opções disponíves e escolhe a desejada, a variável conf é utulizada em um loop na opção 2 para o usário poder consultar diversos ceps.
	```java
		case 1:
				System.out.println("Entre com o arquivo cep");
				Scanner s2 = new Scanner(System.in);
				String arq = s2.nextLine();
				System.out.println("Entre com o nome do arquivo que sera criado");
				Scanner s3 = new Scanner(System.in);
				String hash = s3.nextLine();
				RandomAccessFile r = new RandomAccessFile(arq,"r");
				RandomAccessFile f = new RandomAccessFile(hash,"rw");
				System.out.println("A função de Hash usada é cep % n, entre com o n desejado");
				Scanner s4 = new Scanner(System.in);
				long n = s4.nextLong();
				System.out.println("Criando...");
				Hash.criaHash(f, n);
				Hash.criaIndice(r, f, n);
				System.out.println("Criado com sucesso!");
				break;				
	```
	Caso o usário opte pela opção 1, ele irá criar o arquivo índice baseado no arquivo cep original. Tudo é feito chamando os métodos da classe Hash.
	```java
		case 2:
				System.out.println("Entre com o arquivo cep");
				Scanner s5 = new Scanner(System.in);
				String arq1 = s5.nextLine();
				System.out.println("Entre com o indice hash");
				Scanner s6 = new Scanner(System.in);
				String hash1 = s6.nextLine();
				System.out.println("A função de Hash usada é cep % n, entre com o n desejado");
				Scanner s9 = new Scanner(System.in);
				long n1 = s9.nextLong();
				RandomAccessFile r1 = new RandomAccessFile(arq1,"r");
				RandomAccessFile f1 = new RandomAccessFile(hash1,"rw");
				System.out.println("Entre com o cep desejado");
				Scanner s7 = new Scanner(System.in);
				long cep = s7.nextLong();
				Hash.buscaHash(f1, r1, cep,n1);
				System.out.println("Se você desejar procurar outro cep nesse mesmo aqrquivo digite 1, se não, digite 2");
				Scanner s8 = new Scanner(System.in);
				conf = s8.nextInt();
				while(conf == 1){
					System.out.println("Entre com o cep desejado");
					s7 = new Scanner(System.in);
					cep = s7.nextLong();
					Hash.buscaHash(f1, r1, cep,n1);
					System.out.println("Se você desejar procurar outro cep nesse mesmo aqrquivo digite 1, se não, digite 2");
					s8 = new Scanner(System.in);
					conf = s8.nextInt();
				}
				break;	
	```
	Já a opção 2 é para a consulta de ceps fornecidos pelo usuário. A consulta é feita atrvés de um método da Classe Hash. Nessa opção há um While para o usuário consultar diversos ceps sem encerrar a aplicação.
	```java
		case 3:
				System.out.println("Entre com o arquivo cep");
				Scanner s10 = new Scanner(System.in);
				String arq2 = s10.nextLine();
				RandomAccessFile r2 = new RandomAccessFile(arq2,"r");
				System.out.println("A função de Hash usada é cep % n, entre com o n desejado");
				Scanner s11 = new Scanner(System.in);
				long n2 = s11.nextLong();
				Hash.estatisticasHash(r2, n2);
		}		
	}
}
	```
	Por fim na opção 3 o usuário vê as estatísticas da função de hash utilizada, que é feita através de um método da Classe Hash.
