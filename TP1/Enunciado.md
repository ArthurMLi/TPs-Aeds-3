# TP1 — Relacionamento 1:N

| Informações | Detalhes |
|---|---|
| **Disciplina** | Algoritmos e Estruturas de Dados III |
| **Pontuação** | 4 pontos |
| **Entrega** | URL do repositório no GitHub e vídeo de demonstração |
| **Prazo** | 21 de setembro, às 23h59 |

> **Objetivo:** implementar um sistema simplificado de perguntas e respostas, explorando armazenamento de dados em arquivos, CRUDs e índices.

## Sumário

- [Contexto](#contexto)
- [O que vamos fazer](#o-que-vamos-fazer)
- [Acesso ao sistema](#acesso-ao-sistema)
- [Entidade Usuário](#a-entidade-usuário)
- [Novo usuário](#novo-usuário)
- [Usuário já cadastrado](#usuário-já-cadastrado)
- [Menu principal](#menu-principal)
- [Meus dados](#meus-dados)
- [Entidade Pergunta](#as-perguntas)
- [Gestão das perguntas](#gestão-das-perguntas)
- [Listagem](#listagem)
- [Inclusão](#inclusão)
- [Alteração](#alteração)
- [Arquivamento](#arquivamento)
- [Código que já está pronto](#código-que-já-está-pronto)
- [O que deve ser feito](#o-que-deve-ser-feito)
- [Forma de entrega](#forma-de-entrega)
- [Distribuição de pontos](#distribuição-de-pontos)

## Contexto
Uma das grandes vantagens da Web é a forma como ela nos conecta e, assim, nos permite chegar muito mais longe. Por exemplo, toda vez que temos dúvida sobre alguma coisa, basta procurar na Web que certamente nós encontraremos uma resposta.

Existem alguns projetos, porém, que são dedicados a tirar dúvidas muito específicas das pessoas. Nesses projetos, uma pessoa posta sua dúvida e as outras pessoas vão responder da forma que acharem correta.

Por exemplo, toda vez que eu vejo uma receita, me deparo com a seguinte instrução: "Adicione sal a gosto". Mas como evitar o erro de colocar sal demais ou sal a menos em um prato que você nunca preparou antes? Bem, basta perguntar para alguém mais experiente. Ela poderia responder que é para você "adicionar o sal aos poucos e ir provando até chegar no ponto que você gosta". Outra pessoa poderia ser mais específica em relação ao prato que estamos preparando: "meia colher de café". 

E pronto! Temos uma pergunta e duas respostas. Precisamos, agora, apenas de um sistema que permita às pessoas cadastrarem suas perguntas e a outras pessoas a responderem essas perguntas. 

Na verdade, há uma grande quantidade de projetos como essa na Web, como o StackOverflow, o Answers e o Brainly. Gaste uns minutinhos conhecendo um pouco mais desses projetos.


## O que vamos fazer
Neste semestre, nós vamos implementar um sistema que permite o cadastro de perguntas e respostas. O nosso projeto será uma versão simplificada do StackOverflow, mas será suficiente para nos permitir explorar as principais operações de armazenamento de dados em arquivos.

Neste primeiro trabalho prático, criaremos as perguntas, mas deixaremos a parte das respostas para o segundo trabalho prático. No entanto, como as perguntas serão criadas por usuários específicos, precisaremos também criar os próprios usuários.


## O acesso ao sistema
Para acessar o sistema, um usuário deverá estar cadastrado. O acesso será feito mediante email e senha. Caso o usuário não esteja cadastrado, deverá haver uma opção que permita o seu próprio cadastro. Assim, um primeira tela poderia ser algo assim:

```text
AJUDA AÍ 1.0
------------

(A) Login
(B) Novo usuário (primeiro acesso)

(S) Sair

Opção: _
```

## A entidade Usuário
Nossa entidade usuário precisará contar com pelo menos os seguintes atributos:

- `int idUsuario` — Este atributo é um valor que identifica cada usuário de forma única. Nesses casos, geralmente trabalhamos com valores numéricos, sequenciais e não significativos, pois esse valor não traz nenhuma informação específica do usuário; é apenas usado para distinguir um usuário do outro. A princípio, o usuário do sistema não deve se preocupar com esse valor. Ele será gerado automaticamente pelo sistema e só será usado nas relações internas entre os seus arquivos.
- `String nome` — Este atributo deve conter o nome completo do usuário. Esse atributo é que será apresentado em todas as vezes que houver uma apresentação do próprio usuário.
- `String email` — Neste atributo, armazenaremos o e-mail do usuário. Esse e-mail será usado tanto para acesso ao sistema, assegurando que não existam dois ou mais usuários com o mesmo e-mail.
- `String hashSenha` — A senha pessoal de cada usuário não pode ser armazenada. Assim, armazenaremos o código hash da senha neste atributo. Ele será usado para validar o acesso ao sistema.
- `String perguntaSecreta` — Este atributo deve ser usado quando for necessária a recuperação da senha. Essa pergunta será apresentada ao usuário para validar a sua resposta.
- `String hashRespostaSecreta` — Como no caso da senha, não armazenaremos a resposta em si, mas o código hash dela. Para evitarmos erros de digitação, antes de gerarmos o código hash, será importante remover os acentos e transformar as letras em minúsculas.
Reforçando a informação, nós não armazenamos a senha de um usuário, mas o código hash retornado por ela. Como temos uma limitação de recursos a serem usados em uma interface textual, os atributos PerguntaSecreta e RespostaSecreta deverão ser usados para recuperação da senha. Também é importante lembrar que toda entidade precisa de um identificador exclusivo (ID) que, como vimos nas aulas, será um número inteiro positivo sequencial. Notem que um usuário se identificará por meio do seu email e não do seu ID. A diferença é que o email pode ser alterado, o ID não.

Como as buscas por usuários serão feitas por email, você precisará de um índice indireto baseado nos emails. Crie esse índice usando uma Tabela Hash Extensível.


## Novo usuário
Os usuários só serão cadastrados no sistema por meio da rotina de primeiro acesso, pois cada usuário deverá se cadastrar pessoalmente. Talvez, no futuro, seja importante termos algum usuário moderador que possa bloquear usuários que estão prejudicando o funcionamento do sistema, como escrevendo perguntas ou respostas irrelevantes ou mesmo desrespeitando as regras da ética e do bom convívio social. Por enquanto, porém, vamos torcer para que todos sejam respeitosos.

Assim, a primeira ação do usuário é informar o email que usará no sistema. Esse email deve ser usado sempre, pois, se o usuário se cadastrar com um email (por exemplo, fulano@gmail.com) e, mais tarde, se cadastrar novamente com outro email (por exemplo, fulano@hotmail.com), o sistema não terá como saber que se trata da mesma pessoa.  

Você pode implementar uma tela como a abaixo para a leitura do email:

```text
NOVO USUÁRIO

Email: _
```
Após o usuário informar o seu email, você deve checar se esse email já existe. Se existir, você deve informar uma mensagem de erro e solicitar um novo email. Se o email não existir, então você pode solicitar os demais dados: nome e senha. Ao final, redirecione o usuário para o menu de acesso para que ele faça o primeiro acesso informando o email e a senha cadastrados.


## Usuário já cadastrado
Usuários já cadastrados poderão acessar a primeira opção do menu de acesso, que deverá validar o email e a senha do usuário. O email e a senha devem ser validados de uma só vez. Quando os dados forem inválidos, a mensagem deve retornar que o nome ou o email estão incorretos. Nesse caso, apresente as opções de tentar novamente ou de recuperar senha.

Cada usuário pode escrever perguntas sobre os mais diversos assuntos. Essas perguntas serão mantidas em um segundo arquivo que também precisará do seu CRUD. Nesse arquivo, é necessário manter o vínculo da pergunta ao usuário que a fez. Esse relacionamento é do tipo 1:N (1 usuário para N perguntas).


## Menu principal
Antes de implementarmos as operações com perguntas, precisamos começar a criar o nosso menu principal. Você deverá criar, para a sua aplicação, um laço de mais alto nível para conter esse menu. Ele oferecerá acesso a duas operações: a primeira é o gerenciamento das suas próprias perguntas, respostas e votos, localizadas na sua área pessoal (Minha área); a segunda é a busca por perguntas de outros usuários (onde você também poderá responder ou votar nelas). O seu menu principal, por enquanto, deverá ter uma tela como esta:

```text
AJUDA AÍ 1.0
------------

> Início

(A) Minha área
(B) Buscar perguntas

(S) Sair

Opção: _
```
Por enquanto, você só precisa implementar o acesso à primeira opção, isto é, o gerenciamento de perguntas na área pessoal do usuário, e a última, de saída do sistema. 

Ao acessar a primeira opção, o usuário deve ver uma tela como esta:

```text
AJUDA AÍ 1.0
------------

> Início > Minha área

(A) Meus dados
(B) Minhas perguntas
(C) Minhas respostas
(D) Meus votos

(R) Retornar ao menu anterior

Opção: _
```
Novamente, por enquanto, vamos cuidar apenas da primeira e da segunda opção desse menu (e da última, de retorno, é claro). As duas outras opções ficarão para as próximas etapas do projeto.


## Meus dados
Na área de dados do usuário, devem ser apresentadas as seguintes opções:

```text
AJUDA AÍ 1.0
------------

> Início > Minha área > Meus dados

(A) Alterar nome
(B) Alterar email
(C) Alterar senha
(D) Alterar pergunta e resposta de recuperação da senha

(R) Retornar ao menu anterior

Opção: _
```

## As perguntas
A Pergunta é a principal entidade do nosso sistema. Tudo girará em torno dela. Vamos começar, portanto, definindo a estrutura da nossa entidade Pergunta. Usaremos os seguintes atributos para ela:

int idPergunta - Este atributo identificará cada pergunta de forma exclusiva. Da mesma forma que no caso dos usuários, ele será gerado automaticamente e será na forma de um número inteiro sequencial. Os valores sequenciais, no entanto, não serão numerados por usuário, mas para todo o sistema. Assim, a pergunta de ID 1 pode pertencer a um usuário, a de ID 2 pode pertencer a outro usuário e a pergunta de ID 3 pode ser de um terceiro usuário.
int idUsuario - O ID do usuário será usado para associar esta pergunta ao seu criador. É por meio desse atributo que criaremos o nosso relacionamento 1:N, isto é, 1 usuário poderá criar N perguntas, mas 1 pergunta só pode pertencer a 1 único usuário. Para mantermos a integridade dos nossos dados, será sempre importante só permitir a inclusão de IDs de usuários se eles de fato existirem no arquivo de usuários. Também é importante assegurar que, se um usuário for excluído, todas as suas perguntas também sejam excluídas.
long criacao - Este atributo, do tipo long , conterá a data e a hora da criação da pergunta na forma de milissegundos.
long alteracao - Este atributo, do tipo long , conterá a data e a hora de modificação da pergunta na forma de milissegundos.
short nota - Esse valor indicará a nota da pergunta dada pelos demais usuários com base na sua relevância e na qualidade da sua construção. A nota é baseada na soma dos votos positivos e negativos. Assim, a nota atual pode também ser positiva ou negativa.
String pergunta - Este atributo será uma string para conter todo o texto da pergunta, mesmo que tenha vários parágrafos ou trechos de código. 
String palavrasChave - Este atributo conterá uma lista de termos separados por ponto-e-vírgula, que serão necessários às buscas pelas perguntas. 
booalen ativa - Este atributo indica se a pergunta está ativa. As perguntas não poderão ser excluídas, mas arquivadas. Uma pergunta arquivada não aparecerá nas listagens. Nesse caso, o valor deste atributo será false. 

## Gestão das perguntas
Todas as operações com perguntas deverão ser realizadas a partir do menu de perguntas, que pode ter essa aparência:

```text
AJUDA AÍ 1.0
------------

> Início > Minha área > Minhas perguntas

(A) Listar
(B) Incluir
(C) Alterar
(D) Arquivar

(R) Retornar ao menu anterior

Opção: _
```
Cada opção levará a uma operação diferente.


### Listagem
Na maioria dos sistemas que oferecem relatórios baseados em algum tipo de filtro, é comum encontrarmos arquivos auxiliares para a geração desses relatórios. Caso contrário, seria necessário percorrer todo o arquivo principal e analisar cada um dos registros para ver qual atende aos critérios do filtro.

No nosso caso, o filtro é o atributo idUsuário, isto é, só listaremos as perguntas que tiverem o valor do atributo idUsuário igual ao ID do usuário que estiver usando o sistema. Para sabermos quais são as perguntas de um determinado usuário, usaremos uma árvore B+ com o par [idUsuário, idPergunta].

Esse será o primeiro relacionamento que implementaremos no projeto. Será um relacionamento 1:N (um usuário pode ter N perguntas, mas uma pergunta pertence a um único usuário). Nesse tipo de relacionamento, usaremos a chave estrangeira idUsuário na entidade Pergunta. Isso nos permitirá saber qual é o usuário responsável por uma pergunta. Mas, para fazermos o caminho inverso, isto é, saber quais são as perguntas de um usuário, precisaremos dessa árvore B+.

Atenção: as perguntas apresentadas na tela devem ser numeradas sequencialmente. O ID da pergunta e o ID do usuário não devem ser apresentados, pois esses IDs são dados para uso interno pelo sistema. O estado da pergunta indicado pelo atributo ativa só será apresentado se ela estiver arquivada (inativa) e como sugerido por meio da pergunta 3 abaixo Sua tela deve ficar mais ou menos assim:

```text
MINHAS PERGUNTAS

(1) 
03/08/2026 18:08
É seguro comer pão mofado, se você cortar a parte mofada fora?
Palavras chave: pão;mofado;saúde 

(2) 
10/08/2026 09:30
Para quem está começando a programar agora, qual é a linguagem de programação recomendada?
Palavras chave: programação;linguagem

(3) ARQUIVADA 
11/08/2026 14:52
Por que a luz azul das telas atrapalha o nosso sono?
Palavras chave: luz azul;sono


Pressione qualquer tecla para continuar...
```

### Inclusão

Para incluir uma pergunta no arquivo, o usuário precisará informar apenas a pergunta e a lista de palavras chave. Os demais atributos serão já existirão ou serão definidos automaticamente, isto é, o ID do usuário será o ID da pessoa que está usando o sistema, a data/hora de criação (e atualização) será obtida do computador e a nota será, inicialmente, zero. Depois, basta incluir todos esses valores no arquivo usando o método create() CRUD.


### Alteração

Para alterarmos os dados de uma pergunta, precisamos do ID dessa pergunta. Em um sistema com interface gráfica, apresentaríamos uma lista das perguntas do usuário na tela e ele apenas clicaria na pergunta desejada. Aqui, como estamos usando uma interface textual, teremos que simular algo semelhante. 

A primeira ação é listar todas as perguntas da mesma forma que foi mostrada na tela acima para a listagem. Só que, em seguida, será importante saber qual dessas perguntas o usuário deseja alterar. Assim, você deve manter uma associação (um simples vetor é suficiente) entre o número sequencial apresentado na tela e o real ID da pergunta. O usuário informará o número sequencial apresentado na tela, mas nos passos abaixo você precisará usar o ID da pergunta.

Quando você for alterar uma pergunta, deverá permitir a alteração do atributo de palavras chave também. Lembre-se de que qualquer alteração deve automaticamente ajustar o campo atualização. 

Como você deve ter observado, não demos a opção de alteração do ID de pergunta, do ID de usuário ou do estado. IDs nunca são alterados. Assim, podemos alterar todos os outros dados, mas não o seu ID. Quanto ao ID do usuário, não demos a opção de sua alteração, porque cada usuário só pode gerenciar as suas próprias perguntas. Finalmente, o estado da pergunta permanece como ativo e não há por que alterá-lo manualmente.


### Arquivamento

Como existem muitas relações das perguntas com outras entidades (respostas, votos e comentários) que foram criadas por outros usuários, não faremos a exclusão de perguntas. Me parece meio agressivo apagar dados criados por outros usuários, mesmo que em resposta às minhas próprias perguntas. Assim, permitiremos apenas o arquivamento das perguntas. O processo parece com o de exclusão, mas, ao invés de apagar os dados, apenas muda o estado do atributo ativa. Uma pergunta arquivada só será visualizada pelo seu autor e não poderá receber mais respostas.

O arquivamento será definitivo, isto é, não será possível desarquivar uma pergunta.


## Código que já está pronto
Nesse projeto, vocês devem necessariamente usar o CRUD genéricoLinks to an external site. que desenvolvemos em sala como base. Nosso CRUD cria registros com a seguinte estrutura:

- **Lápide:** byte que indica se o registro é válido ou se é um registro excluído.
- **Indicador de tamanho do registro:** número inteiro (`short`) que indica o tamanho do vetor de bytes.
- **Vetor de bytes:** bytes que descrevem a entidade, obtidos por meio do método `toByteArray()` do próprio objeto.
Além disso, vocês precisarão usar as classes TabelaHashExtensívelLinks to an external site. e Árvore B+Links to an external site. que disponibizei para criar os índices. Não vale inventar uma nova estrutura de dados para os índices nesse projeto, ok?


## O que deve ser feito
Implementar o CRUD de Usuários.
Implementar o CRUD de Perguntas, assegurando que cada pergunta pertença a um usuário específico.
Implementar o relacionamento 1:N com o par (idUsuario; idPergunta) usando a Árvore B+.
Criar a visão e o controle de usuários.
Criar a visão e o controle de perguntas. Uma nova pergunta deverá ser automaticamente vinculado ao usuário ativo no sistema.
Observe que para tudo funcionar, vocês precisarão acessar os arquivos e as visões de usuários e de perguntas em todas as classes de controle.


## Forma de entrega
Código - Vocês devem postar o seu trabalho no GitHub e enviar apenas o URL do seu projeto. Criem um repositório específico para este projeto (ao invés de mandar o repositório pessoal de algum de vocês em que estejam todos os seus códigos). Acrescentem um arquivo readme.md ao projeto que será o relatório do trabalho de vocês (explicado abaixo).
Relatório - O relatório deve começar com a lista dos participantes do trabalho prático e, em seguida, ter uma descrição completa do que o sistema faz. Capturem algumas telas e citem os nomes das classes que foram criadas. Expliquem todas as operações especiais que foram implementadas. O objetivo é que vocês facilitem ao máximo a minha correção, de tal forma que eu possa entender com facilidade tudo aquilo que fizeram e dar uma nota justa. No relatório, vocês devem, necessariamente, responder ao seguinte checklist (copie as perguntas abaixo para o seu relatório e responda sim/não em frente a elas, justificando a resposta quando necessário):
Há um CRUD de usuários (que estende a classe Arquivo, acrescentando Tabelas Hash Extensíveis e Árvores B+ como índices diretos e indiretos conforme necessidade) que funciona corretamente?
Há um CRUD de perguntas (que estende a classe Arquivo, acrescentando Tabelas Hash Extensíveis e Árvores B+ como índices diretos e indiretos conforme necessidade) que funciona corretamente?
As perguntas estão vinculadas aos usuários usando o idUsuario como chave estrangeira?
Há uma árvore B+ que registre o relacionamento 1:N entre usuários e perguntas?
O trabalho compila corretamente?
O trabalho está completo e funcionando sem erros de execução?
O trabalho é original e não a cópia de um trabalho de outro grupo?
Vídeo de demonstração - Grave um vídeo de até 5 minutos (captura de tela com narração em áudio) mostrando as principais operações do seu sistema. Se o vídeo ficar grande demais para o GitHub, vocês podem publicá-lo no YouTube e compartilhar o link ou usar a própria ferramenta do Canvas para captura de vídeo. Nesse seu vídeo você deve demonstrar o funcionamento das seguintes operações:
Cadastro de um novo usuário
Login falhando e uso da funcionalidade de recuperação de senha
Login correto
Atualização do email do usuário (explique também o código fonte responsável por este processo)
Cadastro de uma pergunta
Listagem de perguntas
Atualização de uma pergunta
Arquivamento de uma pergunta (explique o código fonte responsável por este processo)
Lembre-se de que, para essa atividade, eu avaliarei tanto o esforço quanto o resultado. Portanto, escrevam o relatório e gravem o vídeo de forma que me ajude a observar o resultado.

Atenção: As respostas incorretas ao checklist prejudicaram consideravelmente a nota do grupo. Se vocês disserem que fizeram algo que não foi implementado, a nota final será reduzida em 50% por resposta incorreta (duas respostas incorretas significam a nota zero). Além disso, se vocês disserem que algo está funcionando corretamente, mas a operação não funcionar direito, a nota final será reduzida em 25% por resposta incorreta. Dessa forma, quando necessário, justifiquem as respostas ao checklist. A falta do relatório no repositório implicará em perda de 50% dos pontos obtidos na atividade. A falta do vídeo implicará, da mesma forma, em perda de 50% dos pontos. Se os dois faltarem, a nota será, automaticamente, zero.


## Distribuição de pontos
Essa atividade vale 4 pontos. A rubrica de avaliação estabelece os critérios que serão usados na correção.

Atenção: o TP é específico por grupo. TPs iguais receberão a nota zero (independentemente de quem realmente fez o trabalho).

Se tiverem dúvidas sobre o trabalho a fazer, me avisem. Não deixem de observar que o URL com o código no GitHub deve ser entregue até o dia especificado na atividade.