# TP2 — Relacionamento N:N
O usuário deve ser capaz de consultar as perguntas no sistema publicadas por qualquer usuário e de responder a essas perguntas. As respostas também ficarão em um arquivo específico, gerenciado pelo CRUD genérico. Cada resposta deverá ter vínculo tanto com a pergunta a que responde quanto ao usuário que a escreveu. Esse relacionamento pode ser entendido como um relacionamento N:N (N usuários respondem a N perguntas). Em relacionamentos N:N, geralmente temos uma outra entidade entre as outras duas, que contém os dois IDs dessas entidades relacionadas.


A BUSCA DE PERGUNTAS
Antes de respondermos às perguntas, porém, será importante localizar as perguntas. Faremos isso de duas formas: perguntas mais recentes e busca por palavras-chave. Neste TP, implementaremos apenas a listagem das perguntas mais recentes. Para isso, o usuário deverá acessar a opção B no menu principal:

AJUDA AÍ 1.0
------------

> Início

(A) Minha área
(B) Buscar perguntas

(S) Sair

Opção: _
Ao selecionar essa opção B, o sistema deve mostrar as duas opções de busca para o usuário conforme tela abaixo, lembrando que implementaremos apenas a primeira neste TP: a busca das perguntas mais recentes. A implementação dessa busca exigirá que vocês criem um novo índice para as perguntas: um índice baseado na data de criação da pergunta (em ordem decrescente).

AJUDA AÍ 1.0
------------

> Início > Buscar perguntas

(A) Perguntas mais recentes
(B) Busca por palavras-chave

(R) Retornar

Opção: _
Quando o usuário escolher a opção A, deve ser apresentada a ele uma tela com as 5 perguntas mais recentes. No entanto, ele deverá poder avançar para a o próximo conjunto de 5 perguntas, como mostra a tela abaixo.

AJUDA AÍ 1.0
------------

> Início > Buscar perguntas > Perguntas mais recentes

(1) Por que sentimos mais sono depois do almoço?
    Criada por Roberto Souza em 15/09/2026 às 17:52 

(2) Por que o café parece perder o efeito quando é consumido todos os dias?
    Criada por Ana Marina Reis em 15/09/2026 às 09:36

(3) É realmente necessário fechar os aplicativos do celular que estão em segundo plano? 
    Criada por Pedro Rocha em 12/09/2026 às 22:15

(4) Por que algumas pessoas conseguem trabalhar melhor ouvindo música?
    Criada por Sofia Paiva em 10/09/2026 às 07:41

(5) Carregar o celular durante a noite prejudica a bateria?
    Criada por Adriana Estefânia da Silva em 08/09/2026 às 19:34

(A) Página anterior
(B) Próxima página

(R) Retornar

Opção: _
A escolha de uma pergunta deve permitir a visualização dos seus dados completos. Assim, quando o usuário selecionar uma pergunta, deve ver uma tela como esta:

AJUDA AÍ 1.0
------------

> Início > Buscar perguntas > Perguntas mais recentes > Pergunta

Por que sentimos mais sono depois do almoço?
Criada por Roberto Souza em 15/09/2026 às 17:52 

RESPOSTAS
---------
(1) Porque o corpo gasta energia para fazer a digestão 
Respondido por Antônio Carlos Machado em 16/09/2026 às 13:20
Nota: 12  (20 avaliações)

(2) Porque o nosso relógico biológico naturalmente reduz o estado de alerta nesse horário
Respondido por Ana Cláudia Fagundes em 15/09/2026 às 09:21
Nota: 7  (13 avaliações)

(3) Porque a gente quer enrolar com o trabalho e finge ter sono
Respondido por Dick Vigarista em 18/09/2026 às 21:30
Nota: -2  (2 avaliações)

(A) Responder à pergunta

(R) Retornar

Opção: _
Ao clicar em um resposta, o usuário deve poder visualizar os dados dessa resposta e avaliá-la como positiva ou negativa. As respostas devem estar ordenadas por essa avaliação.


O RELACIONAMENTO N:N
Um usuário pode responder N perguntas e uma pergunta pode ser respondida por N usuários. Para isso usaremos um relacionamento N:N. Neste disciplina, os tipos de relacionamentos que nos interessam são:

1:N - Uma entidade de um tipo se relacionada com várias entidades de outro tipo. Esse é o caso mais comum, usado para categorias, grupos, etc. Exemplos: uma editora possui vários livros; uma categoria possui vários produtos; um trabalhador possui vários dependentes; etc. Esse tipo de relacionamento vocês já implementaram no TP1.
N:N - As entidades de um tipo se relacionam com várias entidades de outro tipo. Exemplos: um ou mais autores escrevem um ou mais livros; um ou mais atores fazem um ou mais filmes; uma ou mais playlists contém uma ou mais músicas; etc. Esse tipo de relacionamento, vocês devem implementar neste TP2.
A implementação de relacionamentos N:N é um pouco mais complicada que o relacionamento 1:N, porque a gente precisa poder consultar as relações a partir de qualquer uma das duas entidades. Pensando de uma forma simplista, seria como se a gente tivesse que implementar dois relacionamentos 1:N entre essas entidades. Assim, poderemos consultar quais são os usuários inscritos em um determinado curso e quais são os cursos de um determinado usuário.

No nosso projeto, criaremos uma entidade Resposta e, nela, dois atributos para o relacionamento: idUsuario e idPergunta. Nossa relação ficará parecendo com dois relacionamentos 1:N:

O primeiro será com os usuários: 1 usuário pode criar N respostas
O segundo será com as perguntas: 1 pergunta pode ter N respostas
Veja isso representado na figura abaixo. As linhas sólidas representam as relações reais que criaremos entre as entidades e a linha tracejada representa a relação implícita entre elas.

image.png

Também precisaremos de duas árvores B+ para facilitarmos as consultas desses relacionamentos. Na primeira árvore, registraremos quais são as respostas escritas pelo usuário (idUsuario, idResposta) e, na segunda, registraremos quais são as respostas de uma determinada pergunta (idPergunta, idResposta). Logo em seguida, faremos o mesmo para a avaliação das respostas.


A ENTIDADE RESPOSTA
O objetivo principal do nosso sistema é encontrar respostas para as perguntas dos usuários. Portanto, cuidaremos agora da entidade que representará essas respostas, que deverá conter os seguintes atributos:

int idResposta - Este atributo identificará cada pergunta de forma exclusiva. Novamente será um número inteiro sequencial não significativo, isto é, as respostas receberão, como ID, números sequenciais independentes dos usuários que as escreverem ou das perguntas que respondem.
int idPergunta - Este atributo será usado para o ID da pergunta a que esta resposta está associada. 
int idUsuario - O ID do usuário que escreveu esta resposta deverá ser armazenado neste atributo.
long criacao - Este atributo, do tipo long, conterá a data e a hora da criação da resposta na forma de milissegundos.
String resposta - Este atributo será uma string para conter todo o texto da resposta, mesmo que tenha vários parágrafos ou trechos de código.
short nota - Esse atributo determina a nota atual que a resposta obteve por meio das avaliações dos outros usuários.
short avaliacoes - Este atributo indica quantas avaliações de outros usuários a resposta recebeu.
boolean ativa - Este atributo indica se a resposta está ativa. As respostas, como as perguntas, não poderão ser excluídas, mas arquivadas. Uma resposta arquivada não aparecerá nas listagens. Nesse caso, o valor deste atributo será false. 

CRIAÇÃO DE RESPOSTAS
Ao clicar na opção "Responder à pergunta", o usuário deve ver uma tela como esta abaixo.

AJUDA AÍ 1.0
------------

> Início > Buscar perguntas > Perguntas mais recentes > Pergunta > Responder

Por que sentimos mais sono depois do almoço?
Criada por Roberto Souza em 15/09/2026 às 17:52 

Resposta: _
Aqui, como a resposta será apenas à pergunta selecionada, é interessante que ela seja reapresentada, como mostra a tela acima.

Como já havíamos visto, precisaremos ter duas árvores de relacionamento. A primeira receberá o par de chaves [idPergunta, idResposta] e, dessa forma, associará cada resposta a uma pergunta específica. Já a segunda árvore receberá o par de chaves [idUsuário, idResposta] e associará a resposta a um usuário específico. No entanto, essa segunda árvore de relacionamento não será usada aqui. Ela será usada na área pessoal do usuário quando ele quisesse listar todas as respostas que já escreveu independentemente das perguntas respondidas.

O usuário deve digitar apenas a resposta propriamente dita. Os demais atributos devem ser preenchidos automaticamente.


OPERAÇÕES COM AS RESPOSTAS
Ao visualizar uma resposta, o menu apresentado para o usuário deve considerar se a resposta é do próprio usuário ou não. Se for, ele terá apenas a opção de "Arquivar sua resposta". Se a resposta não for dele mesmo, ele poderá avaliar a resposta. Na tela abaixo, a gente vê o que deve ser exibido se a resposta for do próprio usuário.

AJUDA AÍ 1.0
------------

> Início > Buscar perguntas > Perguntas mais recentes > Pergunta > Resposta

Por que sentimos mais sono depois do almoço?
Criada por Roberto Souza em 15/09/2026 às 17:52 

RESPOSTA
--------

Por causa da compressão do diafragma. O volume de comida no estômago 
pressiona a base dos pulmões, reduzindo sutilmente a entrada de oxigêncio 
e causando lentidão no cérebro.
Respondido por você em 18/09/2026 às 07:55
Nota: 4  (8 avaliações)

(A) Arquivar resposta

(R) Retornar ao menu anterior

Opção: _
Como você vê, não vamos permitir a alteração da resposta, porque ela já terá recebido avaliações. Se, por acaso, a pessoa tiver escrito algo errado, precisará arquivar a resposta e criar uma nova.

Por outro lado, se a resposta não for do próprio usuário, ele deverá ver uma tela como esta abaixo, em que ele pode concordar com ou discordar de uma resposta, contribuindo para o cálculo da nota dessa resposta.

AJUDA AÍ 1.0
------------

> Início > Buscar perguntas > Perguntas mais recentes > Pergunta > Resposta

Por que sentimos mais sono depois do almoço?
Criada por Roberto Souza em 15/09/2026 às 17:52 

RESPOSTA
--------

Porque o corpo gasta energia para fazer a digestão. 
Respondido por Antônio Carlos Machado em 16/09/2026 às 13:20
Nota: 12  (15 avaliações)

(A) Concordar com a resposta
(B) Discordar da resposta

(R) Retornar ao menu anterior

Opção: _
Cada vez que uma resposta é avaliada, a sua nota deve ser ajustada automaticamente, ganhando 1 ponto se o usuário tiver concordado com a resposta e perdendo 1 ponto se o usuário tiver discordado da resposta. O número de avaliações deve ser também incrementado.


A ENTIDADE AVALIAÇÃO
O usuário poderá concordar ou discordar de uma resposta. Quanto a avaliação for positiva, a nota da resposta deve ser incrementada em uma unidade. Quando a avaliação for negativa, a nota deve ser decrementada em uma unidade. Além disso, será importante incrementar a conta de avaliações das perguntas. Mas atenção: essa nota e essa quantidade de avaliações devem ser ajustadas na entidade de Resposta.

E temos aquib um desafio adicional: não deixar o usuário avaliar a mesma resposta duas vezes. Para isso, precisaremos de uma nova entidade para representar uma avaliação do usuário. Essa entidade deve ter os seguintes atributos:

int idAvaliacao - Este atributo identificará cada avaliação de forma exclusiva. 
int idUsuario - O ID do usuário que fez essa avaliação deverá ser armazenado neste atributo.
int idResposta - Este atributo será usado para representar o ID da resposta em que o usuário votou.
boolean avaliação - Este atributo indica se a avaliação foi positiva (true) ou negativa (false).

DE VOLTA À ÁREA DO USUÁRIO
Precisamos agora fazer vários ajustes na área do usuário. Os dois ajustes mais simples são a implementação das opções (C) e (D) da tela abaixo.

AJUDA AÍ 1.0
------------

> Início > Minha área

(A) Meus dados
(B) Minhas perguntas
(C) Minhas respostas
(D) Minhas avaliações

(R) Retornar ao menu anterior

Opção: _
Ao escolher "Minhas respostas", o usuário deve ver uma lista das respostas que escreveu. Quando selecionar uma dessas respostas, ele deve ver tanto a pergunta quanto a resposta que ele mesmo escreveu. Sua única opção será arquivar a resposta. Não vou repetir as telas aqui, porque elas são muito parecidas com as telas acima (mas o breadcrumb precisa ser ajustado).

Já ao escolher "Minhas avaliações", o usuário também deve ver uma lista das respostas que avaliou. Ao selecionar uma resposta, a tela deve mostrar a pergunta feita, a resposta avaliada e a avaliação realizada pelo usuário (positiva ou negativa). A única opção do usuário nessa tela será de cancelar a avaliação. Nesse caso, o registro dessa pergunta deve ter sua nota e quantidade de avaliações ajustada. Assim, não será possível mudar uma avaliação. Para isso, o usuário deverá localizar a pergunta e a resposta novamente, e fazer uma nova avaliação.

Obviamente, nas duas operações acima, o usuário também terá a opção de retornar ao menu anterior.

Assim, o ajuste maior que precisaremos fazer na área do usuário está na opção "Minhas perguntas". Quando o usuário escolher uma pergunta, além dos dados da pergunta propriamente dita, deverão ser apresentadas as respostas de outros usuários a essa pergunta. Algo como isso mostrado na tela a seguir:

AJUDA AÍ 1.0
------------

> Início > Minha área > Minhas perguntas > Pergunta

03/08/2026 18:08
É seguro comer pão mofado, se você cortar a parte mofada fora?
Palavras chave: pão;mofado;saúde  

RESPOSTAS
---------
(1) Não, pois o pão ainda está bolorento. Eliminar as partes verdes não elimina
as raízes do fundo, chamadas de hifas. Se você comê-las, certamente vai passar mal.
Respondido por Antônio Carlos Machado em 04/08/2026 às 06:13
Nota: 7  (9 avaliações)

(2) Depende do tipo de mofo. Se forem uns pequenos fragmentos de mofo branco e
azul, corte essas partes foa e coloque o pão na torradeira que vai dar tudo certo.
Mas se as manchas de mofo forem laranja, amarelo ou preto, jogue fora correndo.
Respondido por Juliana Andrade em 05/08/2026 às 16:49
Nota: 6  (8 avaliações)

(3) Só se o seu cérebro também já estiver mofado. Aí não vai fazer diferença nenhuma mesmo.
Respondido por Dick Vigarista em 11/08/2026 às 11:12
Nota: -3  (3 avaliações)

(A) Alterar dados da pergunta
(B) Arquivar pergunta

(R) Retornar

Opção: _
Aqui, as opções (A) e (B) já foram implementadas no TP1. Então, basta implementarmos a visualização da resposta e o possível arquivamento dessa resposta, isto é, o autor da pergunta poderá arquivar uma resposta que ele entender que seja incompatível com a pergunta ou com as regras de ética do sistema.


O QUE DEVE SER FEITO?
Implementar a listagem de perguntas paginada e por ordem decrescente de data. 
Implementar as operações de respostas às perguntas.
Implementar as operações de avaliação das respostas.
Implementar os ajustes à área do usuário para permitir as gestão das respostas (próprias e dos outros), bem como as avaliações.

FORMA DE ENTREGA
Código - Vocês devem postar o seu trabalho no GitHub e enviar apenas o URL do seu projeto. Criem um repositório específico para este projeto (ao invés de mandar o repositório pessoal de algum de vocês em que estejam todos os seus códigos). Acrescentem um arquivo readme.md ao projeto que será o relatório do trabalho de vocês (explicado abaixo).
Relatório - O relatório deve começar com a lista dos participantes do trabalho prático e, em seguida, ter uma descrição completa do que o sistema faz. Capturem algumas telas e citem os nomes das classes que foram criadas. Expliquem todas as operações especiais que foram implementadas. O objetivo é que vocês facilitem ao máximo a minha correção, de tal forma que eu possa entender com facilidade tudo aquilo que fizeram e dar uma nota justa. No relatório, vocês devem, necessariamente, responder ao seguinte checklist (copie as perguntas abaixo para o seu relatório e responda sim/não em frente a elas, justificando a resposta quando necessário):
Há uma listagem paginada de perguntas, que mostra as perguntas em ordem decrescente de data a partir de uma Árvore B+ acrescentada ao CRUD de Perguntas?
Há uma listagem das respostas a uma pergunta selecionada, em ordem decrescente de nota?
O usuário pode responder a uma pergunta?
O usuário pode arquivar a própria resposta?
O usuário pode avaliar uma resposta?
Na área do usuário, o usuário pode visualizar as respostas às suas perguntas?
Na área do usuário, o usuário pode arquivar uma resposta de outro usuário a uma das suas perguntas?
Na área do usuário, o usuário pode visualizar e arquivar as respostas que escreveu?
Na área do usuário, o usuário pode visualizar e arquivar a avaliação que realizou em uma resposta?
A integridade do relacionamento entre perguntas e respostas está mantida em todas as operações?
A integridade do relacionamento entre respostas e avaliações está mantida em todas as operações?
O trabalho compila corretamente?
O trabalho está completo e funcionando sem erros de execução?
O trabalho é original e não a cópia de um trabalho de outro grupo?
Vídeo de demonstração - Grave um vídeo de até 5 minutos (captura de tela com narração em áudio) mostrando as principais operações do seu sistema. Se o vídeo ficar grande demais para o GitHub, vocês podem publicá-lo no YouTube e compartilhar o link ou usar a própria ferramenta do Canvas para captura de vídeo. Nesse seu vídeo, você deve mostrar as seguintes operações:
Listagem das perguntas em ordem decrescente de data (explique também o código responsável por este processo)
Listagem das respostas escritas para uma pergunta
Escrita de uma resposta para uma pergunta (explique também o código responsável por este processo)
Avaliação da resposta de um outro usuário
Na área do usuário, cancelamento de uma resposta de outro usuário
Na área do usuário, visualização de uma resposta específica do usuário a uma pergunta
Na área do usuário, visualização da avaliação feita pelo usuário a uma resposta específica de outro usuário
Cancelamento dessa avaliação (explique também o código responsável por este processo)
Lembre-se de que, para essa atividade, eu avaliarei tanto o esforço quanto o resultado. Portanto, escrevam o relatório e gravem o vídeo de forma que me ajude a observar o resultado.

Atenção: As respostas incorretas ao checklist prejudicaram consideravelmente a nota do grupo. Se vocês disserem que fizeram algo que não foi implementado, a nota final será reduzida em 50% por resposta incorreta (duas respostas incorretas significam a nota zero). Além disso, se vocês disserem que algo está funcionando corretamente, mas a operação não funcionar direito, a nota final será reduzida em 25% por resposta incorreta. Dessa forma, quando necessário, justifiquem as respostas ao checklist. A falta do relatório no repositório implicará em perda de 50% dos pontos obtidos na atividade. A falta do vídeo implicará, da mesma forma, em perda de 50% dos pontos. Se os dois faltarem, a nota será, automaticamente, zero.


DISTRIBUIÇÃO DE PONTOS
Essa atividade vale 4 pontos. A rubrica de avaliação estabelece os critérios que serão usados na correção.

Atenção: o TP é específico por grupo. TPs iguais receberão a nota zero (independentemente de quem realmente fez o trabalho).

Se tiverem dúvidas sobre o trabalho a fazer, me avisem. Não deixem de observar que o URL com o código no GitHub deve ser entregue até o dia especificado na atividade.

