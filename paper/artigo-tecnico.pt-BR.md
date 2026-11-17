---
title: "Crivo: uma plataforma aberta de protocolos automatizados para o estudo da cognição anômala"
subtitle: "Artigo de desenho, que descreve um método antes de qualquer dado"
author:
  - name: Rogerio Cisi
    affiliation: "TODO: afiliação"
lang: pt-BR
date: "Esboço — novembro de 2026"
bibliography: referencias.bib
link-citations: true
keywords: [mediunidade, recepção anômala de informação, protocolo cego, pré-registro, reprodutibilidade, auditoria, software livre]
status: rascunho
---

<!--
NOTAS DE REDAÇÃO (remover antes de submeter)
- Este arquivo acompanha o texto publicado em https://crivo.psigama.com/artigo/, cuja fonte é
  src/main/resources/templates/site/artigo.html. Quem muda um muda o outro.
- Citações no formato Pandoc [@chave]; chaves em referencias.bib.
- Os trechos "A FAZER" são comentários: aparecem no site como caixas, mas não no PDF.
- Para gerar PDF: pandoc artigo-tecnico.pt-BR.md --citeproc -o artigo.pdf

PENDÊNCIAS ANTES DA SUBMISSÃO
- Ler o texto integral das referências ainda conferidas só pelo resumo e conferir cada número citado.
- Repetir o levantamento de ferramentas (seção 2.5) em bases indexadas, com protocolo registrado, e conferir no texto integral as descrições usadas.
- Efeito esperado e cálculo de poder (seção 4.5).
- Revisão do questionário, hoje em versão de rascunho.
- Normas de ética em pesquisa aplicáveis (seção 7).
- Ensaio completo e auditoria externa (seção 8).
- Afiliação do autor, abstract em inglês e escolha do destino: preprint em português e, depois, revista da área ou de software de pesquisa.
-->

# Resumo

Para que um estudo de mediunidade diga alguma coisa, é preciso mostrar que a informação dada pelo médium não chegou a ele por vias comuns: pistas involuntárias, leitura fria, pesquisa prévia, a boa vontade de quem avalia ou fraude. A literatura de recepção anômala de informação (AIR, de *anomalous information reception*) já descreve protocolos capazes de fechar essas portas. Executá-los, porém, depende de uma equipe e de regras manuais que quem lê o artigo publicado não tem como conferir, e a avaliação continua sujeita ao julgamento de quem decide se uma afirmação se ajusta à pessoa falecida.

Este artigo apresenta o Crivo, uma plataforma de código aberto que executa um protocolo de AIR do início ao fim sem ninguém da equipe no circuito. O consulente, que é a pessoa enlutada, e o médium respondem ao mesmo questionário fechado sobre o falecido, de quem o médium conhece apenas o primeiro nome. O sistema confere as respostas campo a campo, contra o alvo e contra um chamariz, outra pessoa falecida de perfil semelhante que serve de controle. Ninguém precisa julgar se uma afirmação se ajusta. Os requisitos de método tornam-se propriedades do software: o que cada pessoa pode ver é limitado na camada de dados, e não na tela; as respostas são lacradas criptograficamente antes de qualquer informação chegar ao médium; o sorteio é lacrado antes da coleta e aberto no fim; o plano do estudo é registrado e congelado antes dos dados; o cegamento se desfaz sozinho quando a amostra declarada se completa; e cada passo entra numa trilha de auditoria encadeada, atestada por uma autoridade independente de carimbo do tempo.

Resultados nulos e positivos produzem o mesmo relatório e o mesmo pacote de verificação, que qualquer pessoa confere a partir do próprio arquivo. O artigo descreve os requisitos, derivados também da crítica cética, o protocolo, a arquitetura, o que o sistema garante e o que não pode garantir, e as questões de ética e de proteção de dados.

**Palavras-chave:** mediunidade; recepção anômala de informação; protocolo cego; pré-registro; reprodutibilidade; auditoria; software livre.

# 1. Introdução

Quem procura um médium depois de perder alguém às vezes sai da consulta convencido de ter ouvido coisas que ninguém ali poderia saber. A ciência não precisa confirmar nem desmentir essa impressão para levá-la a sério; precisa perguntar de onde veio a informação. A questão, portanto, não é se existem relatos de médiuns que acertam — eles são abundantes —, mas se, sob condições que excluam as explicações convencionais, a informação produzida excede o que se esperaria por acaso.

Essas explicações são conhecidas e nada têm de exóticas. Quem está diante do médium reage, e as reações orientam o que vem a seguir. Frases vagas soam certeiras para quem deseja reconhecê-las. O médium pode ter pesquisado antes, ou simplesmente trapaceado. E quem avalia o que foi dito tende a guardar os acertos e a esquecer os erros. Um estudo só diz alguma coisa se cada uma dessas portas estiver fechada — e se for possível mostrar, depois, que estava.

A literatura experimental chama o fenômeno em questão de recepção anômala de informação, ou AIR: o relato de informação precisa e específica sobre uma pessoa falecida, sem conhecimento prévio, sem retorno sensorial e sem trapaça [@beischel2015]. O termo é deliberadamente neutro quanto à origem. Não afirma que a informação venha da pessoa falecida, nem de alguma capacidade dos vivos, como a telepatia [@beischel2007]. O vocabulário da área segue a mesma cautela: o que o médium produz sobre um falecido chama-se leitura (*reading*), e não mensagem ou comunicação, palavras que pressupõem de onde a informação vem. Ao longo do texto, chamamos de consulente a pessoa enlutada que conheceu o falecido e cujo conhecimento sobre ele serve de referência para conferir as leituras, o *sitter* da literatura em inglês. Este artigo adota essa neutralidade: descreve um método para verificar se a informação existe, e não uma teoria sobre de onde ela viria.

O quadro empírico é dividido. Estudos com cegamento em várias camadas relatam desempenho acima do acaso [@beischel2007; @beischel2015]; outros não encontram efeito algum [@okeeffe2005]. Uma análise comparativa recente atribui parte da divergência a detalhes do desenho, como a seleção dos médiuns e o tipo de interação permitido durante a leitura [@silva2022].

Este artigo defende que o gargalo da área está menos na teoria do que na execução verificável. Um estudo cego depende do cumprimento de dezenas de pequenas regras — quem pode saber o quê, e a partir de quando —, e esse cumprimento costuma ficar registrado, quando fica, em procedimentos manuais que o leitor do artigo publicado não tem como auditar. Os autores de uma das principais séries de estudos positivos apontam, além disso, que todos os seus dados vêm de um único laboratório [@beischel2015], o que torna a replicação independente especialmente valiosa.

A proposta é tratar essas regras como propriedades de um sistema de software, implementadas e testadas com o rigor com que se testam requisitos de segurança, e retirar da pontuação o julgamento subjetivo. O artigo faz quatro contribuições: organiza os requisitos de método como um modelo de ameaças (seção 3); descreve um protocolo pareado, fechado e integralmente automatizado (seção 4); apresenta a arquitetura que o impõe (seção 5); e delimita o que essa arquitetura garante e o que não garante (seção 6). As seções seguintes tratam da ética, do estado atual do projeto e do alcance do que ele poderá mostrar. Por ser um artigo de desenho, o texto não traz resultados: nenhum estudo com participantes reais foi conduzido.

# 2. Trabalhos relacionados

## 2.1 Protocolos de AIR com cegamento

Em pesquisa, cegar alguém é impedir que essa pessoa saiba algo que poderia, mesmo sem querer, influenciar o resultado; desfazer o cegamento no fim do estudo, revelando o que estava oculto, é o desmascaramento. O protocolo de referência para este trabalho, o de @beischel2007, cegou três papéis ao mesmo tempo. O médium, que fazia a leitura por telefone, recebia apenas o primeiro nome da pessoa falecida e, além de falar livremente, respondia a perguntas sobre a aparência, a personalidade, os passatempos e a causa da morte dela. Os consulentes não participavam: eram representados por um experimentador — o consulente substituto, ou *proxy* — que também nada sabia sobre eles nem sobre os falecidos. Os falecidos eram pareados dois a dois, com o mesmo gênero, e o mesmo médium lia os dois de cada par. No fim, cada consulente pontuava, sem saber qual era qual, a leitura feita para o seu falecido, chamada de alvo, e a do outro falecido do par, que servia de controle e é chamada de chamariz; depois, escolhia a que mais se ajustava.

A replicação e extensão de @beischel2015, com 20 médiuns certificados pelo próprio grupo depois de triagem e testes, manteve essa estrutura e voltou a relatar leituras-alvo mais bem avaliadas que os chamarizes. Os autores apontam como principal limitação o fato de todos os dados virem de um único laboratório. Para a descrição das técnicas de laboratório, usamos @beischel2007metodos, sem tomá-la como avaliação da evidência.

## 2.2 Críticas metodológicas e resultados nulos

Nem todo estudo cego encontra efeito. @okeeffe2005 desenharam um método próprio, sem contato entre médiuns e consulentes e com cada consulente avaliando, afirmação por afirmação, todas as leituras do estudo, e obtiveram resultado nulo. O estudo foi criticado pela falta de triagem dos médiuns e pelo formato dos itens, que reuniam várias afirmações num só. Em @kelly2011, a primeira etapa não teve resultado significativo. A segunda teve, mas mudou três coisas, entre elas o envio aos médiuns de fotografias dos falecidos — e uma fotografia diz muito, da idade à época em que a pessoa viveu, o que deixa incompleto o cegamento dessa etapa. Casos assim mostram que detalhes operacionais aparentemente pequenos decidem a validade de um estudo.

As objeções que mais pesam no desenho do Crivo, no entanto, vêm da crítica cética [@hyman1977; @hyman2003; @wiseman2001]. A leitura fria é a técnica, deliberada ou não, de produzir afirmações que soam específicas mas servem para quase todo mundo, corrigindo o rumo pelas reações de quem ouve [@hyman1977]. A validação subjetiva é o outro lado da moeda: quem avalia encontra sentido no que ouve e reconhece como certeira uma frase vaga. Somam-se a elas a pontuação enviesada e a ausência de uma regra de parada fixada antes da coleta, que permite encerrar o estudo no momento em que o resultado parece favorável [@hyman2003].

## 2.3 Revisões, meta-análises e contexto brasileiro

Uma meta-análise combina estatisticamente os resultados de vários estudos para estimar um efeito comum. A de @sarraf2021 reuniu 18 experimentos, publicados em 14 artigos entre 2001 e 2019, e estimou um efeito de 0,18 acima do nível do acaso, num índice padronizado de proporção (intervalo de confiança de 95%: 0,12 a 0,25). A estimativa resistiu a dois testes de viés de publicação, a tendência de os resultados positivos chegarem às revistas com mais facilidade que os nulos. @milton1999 lembram, porém, que meta-análise não substitui replicação independente: o resultado depende de quais estudos entram e de como se trata a diferença entre eles. A lição veio de outra literatura, a do ganzfeld, um experimento de telepatia sob privação sensorial leve.

@silva2022 compararam oito estudos recentes com cegamento ao menos triplo, cinco com resultado significativo e três sem, à procura do que os distinguia. Sete condições coincidiram com resultado positivo: selecionar médiuns com evidência prévia; recrutar consulentes motivados e enlutados por uma perda significativa; permitir alguma interação com o médium, ainda que por um consulente substituto; dar ao médium alguma informação sobre o falecido; combinar fala livre com perguntas objetivas; pontuar tanto a leitura inteira quanto os itens; e evitar um número grande de leituras e de itens. Um dos achados é um ponto que este artigo precisa enfrentar: nos dois estudos em que o médium não interagiu com ninguém, um deles o de @okeeffe2005, o resultado foi nulo. Os próprios autores pedem cautela, porque a comparação é heurística e parte dos estudos não descreve variáveis importantes.

No Brasil, @rocha2014 examinaram 13 cartas psicografadas por Chico Xavier, isto é, escritas pelo médium e atribuídas por ele a um mesmo falecido. Identificaram 99 itens de informação verificáveis e os conferiram com documentos e com o depoimento da irmã e de amigos do falecido: 98% foram classificados como de ajuste claro e preciso. O trabalho mostra o valor de afirmações verificáveis item a item. E, justamente por ser um estudo de caso retrospectivo, que examina material produzido sem nenhum controle do vazamento, mostra também o que um desenho prospectivo e cego acrescentaria.

## 2.4 Pré-registro e flexibilidade analítica

Um estudo não erra apenas por vazamento. Escolhas não declaradas na coleta e na análise — parar de coletar quando o resultado parece bom, decidir depois dos dados qual medida relatar, testar várias análises e mostrar a que deu certo — bastam para fazer um efeito inexistente parecer estatisticamente significativo, isto é, improvável de ter surgido por acaso [@simmons2011]. O pré-registro responde a isso: publicar, antes da coleta, as hipóteses, o tamanho da amostra e a análise que será feita. É o equivalente científico de anunciar a aposta antes de a roleta girar, e torna verificável a distinção entre a pesquisa confirmatória, que testa o que foi declarado, e a exploratória, que procura padrões em dados já vistos [@nosek2018].

## 2.5 Lacuna

Para delimitar a lacuna, fizemos um levantamento exploratório do software usado em estudos de mediunidade e de psi, termo genérico da parapsicologia para a percepção extrassensorial e fenômenos afins. A busca foi feita em novembro de 2026, na web aberta e em repositórios de código, com termos em inglês e em português que combinavam o domínio (mediunidade, AIR, parapsicologia, psi, ganzfeld, percepção extrassensorial) com a função procurada (software, plataforma, código aberto, cegamento, sorteio, pré-registro, trilha de auditoria, pontuação automática). O que apareceu se agrupa em cinco famílias (Tabela 1).

**Tabela 1.** Famílias de software encontradas no levantamento exploratório.

| Família | Exemplos | O que cobre | O que não cobre |
|---|---|---|---|
| Protocolos de mediunidade | @beischel2015; @tressoldi2022; @tressoldi2025 | Cegamento múltiplo, chamariz e avaliação cega, descritos como procedimento | Não localizamos software público que execute ou comprove o cegamento; a pontuação é feita por pessoas |
| Credibilidade verificável em psi | Transparent Psi Project [@kekecs2023] | Depósito direto dos dados em repositório de terceiros, dados abertos desde a origem, relatório em tempo real, software que evidencia adulterações, auditores externos | O domínio é outro, um experimento de precognição em laboratório: não há papéis a cegar entre si nem informação sobre pessoa falecida |
| Registro e dados abertos da área | Registro de estudos da Koestler Parapsychology Unit [@wiseman2026]; repositório Psi Open Data | Pré-registro e publicação dos dados | Não executam o estudo: o vínculo entre o que foi registrado e o que foi coletado depende do pesquisador |
| Testes de psi automatizados | Autoganzfeld [@bem1994]; testes on-line GotPsi [@radin2019]; validação do software de estudo [@pooley2024] | Sorteio e registro por computador; coleta em larga escala pela internet | Nada sobre mediunidade; os testes on-line sem supervisão ficam sujeitos a parada opcional, como apontam revisões da própria área |
| Plataformas genéricas | jsPsych [@deleeuw2015], psiTurk [@gureckis2016], REDCap [@harris2009] | Construção de experimentos on-line; captura de dados com trilha de auditoria e randomização | Não trazem matriz de visibilidade por papel, compromisso e revelação nem desmascaramento automático; teriam de ser construídos por cima |

O vizinho mais próximo é o Transparent Psi Project [@kekecs2023]. Ele parte da mesma tese deste artigo — a de que a credibilidade de um resultado deve ser verificável por terceiros, e não pressuposta — e a aplica a uma replicação multicêntrica de um experimento de precognição. O Crivo deve ser lido como extensão dessa linha a um domínio em que há vários papéis humanos a manter cegos entre si, e não como ideia sem precedente.

Também há antecedente para a ideia de provar a data de um protocolo registrando a sua impressão digital criptográfica, ou hash, com a data atestada por terceiros — o carimbo do tempo, explicado na seção 5. Na pesquisa clínica, @irving2016 propuseram ancorar esse registro numa *blockchain*, um registro público e distribuído como o do Bitcoin; o artigo foi retratado pelos próprios autores em 2017, depois que a revisão por pares aberta apontou problemas de método. Isso recomenda cautela ao citá-lo e reforça a escolha, aqui, de um padrão estabelecido, o RFC 3161, que é o padrão da internet para carimbos do tempo [@adams2001]. Por fim, existe no SourceForge um projeto de código aberto para experimentos de psi on-line, sem atualização desde 2013; não encontramos nele pré-registro, cegamento por papel nem trilha de auditoria.

**A lacuna, na forma em que o artigo pode afirmá-la:** nas fontes consultadas, não localizamos uma plataforma aberta que execute um protocolo de mediunidade de ponta a ponta e que trate o cegamento entre papéis, o sorteio, o pré-registro e a pontuação como propriedades conferíveis por terceiros a partir de artefatos publicados. As peças existem, mas separadas: em outros domínios ou como procedimento manual.

<!-- A FAZER: este levantamento é exploratório, não uma revisão sistemática. As descrições acima vêm de resumos e precisam ser conferidas no texto integral. Antes da submissão, repetir a busca em bases indexadas com protocolo registrado (termos, datas, critérios de inclusão) e perguntar diretamente aos grupos ativos na área se usam software próprio não publicado. -->

# 3. Requisitos: um modelo de ameaças

Na engenharia de segurança, um modelo de ameaças lista tudo o que pode dar errado num sistema e associa a cada risco a defesa correspondente. Aqui, cada explicação convencional para um acerto é tratada como uma ameaça: se ela for capaz de produzir o resultado, o resultado nada prova. A Tabela 2 relaciona cada ameaça à fonte que a descreve e ao controle que o Crivo lhe opõe; onde o controle é só parcial, a tabela diz. Vários controles usam recursos de criptografia, como hashes, compromissos e carimbos do tempo, explicados em termos simples no início da seção 5.

**Tabela 2.** Ameaças à validade de um estudo e os controles correspondentes no Crivo.

| Ameaça | Fonte | Controle no Crivo |
|---|---|---|
| Pista sensorial ou verbal ao médium | @wiseman2001 | Não há sessão: o médium não interage com ninguém e recebe o pedido pelo sistema |
| Informação além do mínimo | @kelly2011 | Só o primeiro nome, sem sobrenome nem lugar, por liberação única e registrada; sem saber quem pede, o médium não tem como pesquisar antes |
| Leitura fria e afirmações genéricas | @hyman1977 | Chamariz de perfil semelhante; conta apenas a diferença entre alvo e chamariz |
| Validação subjetiva e viés de quem avalia | @hyman2003 | Questionário fechado e conferência por igualdade de opções; ninguém julga o ajuste |
| Itens ambíguos ou compostos | Críticas a @okeeffe2005 | Campos com opções fixas, versionados e congelados no pré-registro |
| Tendência de resposta do médium | Decisão de desenho | O mesmo médium faz as duas leituras do par, e a tendência se cancela na diferença |
| Respostas alteradas depois de conhecida a leitura | Decisão de desenho | Respostas travadas, com compromisso criptográfico na trilha, antes da liberação do nome (seção 5.3) |
| Sorteio manipulável | Decisão de desenho | Compromisso sobre a semente antes da coleta; revelação no fim (seção 5.4) |
| Parada oportunista da coleta | @hyman2003; @simmons2011 | Tamanho de amostra (N) pré-registrado, desmascaramento automático (seção 4.2) e nenhuma nota antes dele |
| Análise escolhida depois dos dados | @nosek2018 | Análise primária única no documento congelado por hash |
| Acertos tratados como independentes | @okeeffe2005 | Análise vedada; o teste de permutação não supõe independência (seção 4.5) |
| Reescrita da história | Decisão de desenho | Trilha somente de acréscimo, encadeada por hash e carimbada por autoridade externa (seção 5.6) |
| Viés de publicação | @sarraf2021 | Mesmo relatório para qualquer resultado, em página pública |
| Conluio entre médium e consulente | Decisão de desenho | Parcial: perfil fixo, médium sorteado e sinalização de contas ligadas |

A maior parte desses controles não depende de alguém se lembrar de aplicá-los: eles estão no desenho do banco de dados e nos testes, como mostra a seção 5. A última linha, porém, não tem solução completa. Com inscrição aberta, um médium e um consulente que se conheçam podem combinar respostas usando contas que nada liga entre si; o sistema reduz esse risco, mas não o elimina, e a seção 6 o registra entre os limites.

# 4. O protocolo: leitura estruturada automatizada

## 4.1 Ideia central

O protocolo parte de uma ideia simples: o consulente e o médium respondem ao mesmo questionário fechado sobre a pessoa falecida. O consulente responde porque a conheceu; o médium, conhecendo apenas o primeiro nome. Como não há texto livre, não há o que interpretar. A conferência é uma comparação de igualdade entre opções, feita pelo sistema, e ninguém — nem o consulente — decide se uma afirmação “encaixa”. A ideia de perguntar ao médium sobre aspectos específicos da vida do falecido já estava em @beischel2007; o que muda é que as respostas se fecham em opções.

A troca tem custo, e o protocolo o declara. Ganham-se a pontuação objetiva e a automação integral; perdem-se o consulente substituto e a resposta livre, justamente duas das condições que @silva2022 associaram a resultado positivo. Por isso, um resultado deste protocolo, positivo ou nulo, não vale como replicação dos estudos com consulente substituto da literatura.

O questionário, ainda em versão de rascunho, tem 16 campos. Três deles — gênero, faixa etária no óbito e período do falecimento — servem para formar os pares e ficam fora da nota: são iguais dentro de cada par, por construção, e o gênero, o próprio nome costuma entregar. Os outros 13 entram na nota e tratam das circunstâncias da morte, da família, da ocupação, do lugar onde a pessoa viveu, do temperamento, da aparência, da religiosidade e de hábitos. Cada campo tem poucas opções, além de “não sei”, para o consulente, e de “não recebi”, para o médium, que responde só aos campos da nota. O questionário em vigor, com todas as opções, está publicado na [página do protocolo](https://app.crivo.psigama.com/protocolo/P1).

## 4.2 Fluxo

Do pré-registro à devolutiva, um estudo percorre oito etapas. Fora o pré-registro, feito pelo pesquisador, e as respostas dos participantes, tudo é feito pelo sistema.

1. **Pré-registro.** O pesquisador declara e congela as hipóteses, o número de pares (N), o questionário, o critério de pareamento, o prazo do médium, os critérios de inclusão, a análise e as regras de exclusão. No mesmo documento vai o compromisso sobre a semente do sorteio (seção 5.4).
2. **Pedido.** O consulente cria a conta, confirma o e-mail e faz o pedido: o primeiro nome do falecido e as respostas ao questionário. As respostas são travadas, e a trilha de auditoria recebe o compromisso correspondente (seção 5.3). O pedido pode anteceder o congelamento, já que ninguém vê as respostas antes do fim; só entra no estudo, porém, o pedido feito na versão do questionário que o pré-registro declara.
3. **Pareamento e sorteio.** O sistema forma pares de pedidos travados do mesmo estrato, isto é, com o mesmo gênero, a mesma faixa etária no óbito e o mesmo período do falecimento, e sorteia um médium para cada par.
4. **Envio.** O médium recebe os dois primeiros nomes do par, em ordem sorteada, e o questionário em branco. Não sabe quem são os consulentes nem tem contato com eles.
5. **Leitura.** Para cada nome, o médium responde a cada campo ou marca “não recebi”. Ao enviar, a leitura é travada, também com compromisso na trilha. Se o prazo declarado vence, o par vai a outro médium, sorteado da mesma semente.
6. **Desmascaramento.** Quando os N pares têm as duas leituras, o próprio sistema encerra a coleta e desfaz o cegamento: mostra que médium leu cada par e abre os compromissos, divulgando os segredos que os mantinham lacrados — os sais e a semente (seções 5.3 e 5.4).
7. **Conferência e análise.** O sistema confere os compromissos, pontua as leituras, roda a análise pré-registrada e publica o relatório e o pacote de verificação.
8. **Devolutiva.** O consulente vê a leitura feita para o seu falecido ao lado das próprias respostas; o médium vê as notas das próprias leituras.

## 4.3 Matriz de visibilidade

O cegamento é especificado como uma matriz: nas linhas, as informações do estudo; nas colunas, os papéis; em cada célula, o que aquele papel pode ver e a partir de quando (Tabela 3). Cada célula proibida corresponde a um teste automatizado que tenta violá-la contra um banco de dados real. Note-se que o administrador — o pesquisador que conduz o estudo — sabe quem participa, mas fica cego para as respostas, as leituras e a ligação entre pedidos e médiuns até o desmascaramento.

**Tabela 3.** Matriz de visibilidade: o que cada papel pode ver, e a partir de quando.

| Informação | Administrador | Médium | Consulente |
|---|---|---|---|
| Identidade das contas | Sim | Só a própria | Só a própria |
| Respostas do consulente | Só após desmascarar | Não | Só as próprias |
| Primeiro nome do falecido | Não | Só dos pedidos recebidos | Só o próprio |
| Ligação entre pedido e médium | Só após desmascarar | Só os próprios pedidos | Só após desmascarar |
| Leitura do médium | Só após desmascarar | Só as próprias | Só após desmascarar |
| Notas e diferenças | Não existem antes do desmascaramento | Idem | Idem |
| Sais e semente | Só após a revelação | Não | Não |

## 4.4 Conferência automática

A regra de pontuação cabe em poucas linhas. Num par formado pelos falecidos A e B, chamamos de *R*~A~ a leitura feita com o nome de A e de *R*~B~ a feita com o nome de B; para simplificar, A e B designam também os consulentes de cada falecido. Contam apenas os campos comparáveis: os campos da nota que os dois consulentes responderam, sem “não sei”. O conjunto é o mesmo para o alvo e para o chamariz, para que nenhum dos dois tenha mais chance de acerto. Para uma leitura *R* e um consulente *X*, *s*(*R*, *X*) é o número de campos comparáveis em que a resposta da leitura coincide com a de *X*; “não recebi” nunca coincide. A diferença do par é

$$d = [s(R_A, A) - s(R_A, B)] + [s(R_B, B) - s(R_B, A)]$$

Cada colchete pergunta se uma leitura se parece mais com a pessoa para quem foi feita do que com a outra pessoa do par. Se as leituras não carregam informação sobre os alvos, as duas comparações tendem a se equilibrar, e *d* fica em torno de zero.

::: {.quadro}
**Um exemplo, com nomes fictícios**

Suponha um par de homens que morreram entre os 60 e os 79 anos, há entre um e cinco anos: Antônio, pai de Ana, e Carlos, marido de Beatriz. Ana e Beatriz responderam aos 13 campos da nota, sem nenhum “não sei”, e todos são comparáveis. O médium sorteado recebe os dois nomes e preenche o questionário duas vezes, uma para cada nome. A leitura feita para Antônio coincide com as respostas de Ana em 7 campos e com as de Beatriz em 5; a feita para Carlos coincide com as de Beatriz em 6 e com as de Ana, também em 6. A diferença do par é *d* = (7 − 5) + (6 − 6) = +2.

Sozinho, esse número nada significa: um par pode dar +2 por acaso, como outro pode dar −2. A análise pergunta se, somados todos os pares do estudo, os valores positivos predominam além do que o acaso explicaria.
:::

O chamariz é indispensável porque o número de acertos, sozinho, não diz nada. Respostas frequentes — “morreu do coração”, “era casado”, “tinha filhos” — coincidem com as de muita gente. Comparando a mesma leitura com o alvo e com um chamariz de perfil semelhante, a frequência de cada resposta se cancela, e o que sobra é o quanto a leitura se ajusta ao alvo mais do que ao chamariz. É por isso que a leitura fria, que vive de afirmações frequentes, não rende pontos neste protocolo. E, como o mesmo médium faz as duas leituras do par, a tendência dele a repetir certas respostas entra dos dois lados da diferença e também se cancela.

Isso permite declarar o acaso antes da coleta, em vez de estimá-lo a partir dos dados. A hipótese nula, a que o estudo põe à prova, é a de que a leitura feita com o nome de A não traz sobre A nenhuma informação que não traga também sobre B. Se ela for verdadeira, trocar entre si as respostas dos dois consulentes apenas inverte o sinal de *d*. Logo, *d* é simétrico em torno de zero, e, entre os pares com *d* diferente de zero, a chance de *d* ser positivo é de 50%. Como o primeiro nome pode carregar alguma informação sobre a pessoa, essa hipótese não vale de graça; a seção 6 discute o vazamento que resta.

## 4.5 Análise

A unidade de análise é o par: cada par contribui com um único *d*. A análise primária é o teste de postos sinalizados de Wilcoxon, unilateral, sobre os *d* de todos os pares. Em termos simples, o teste ordena as diferenças pelo tamanho e verifica se as positivas pesam mais do que as negativas além do que o acaso permitiria; é unilateral porque a hipótese só prevê diferenças positivas. Com até 20 pares em que *d* é diferente de zero, o teste é exato; acima disso, usa a aproximação normal, uma fórmula que fica precisa com amostras maiores, e o relatório diz qual das duas foi usada.

Duas análises secundárias olham os mesmos dados por outros ângulos. O teste binomial exato do sinal ignora o tamanho de *d* e pergunta apenas se os pares positivos passam da metade. O teste de permutação embaralha as leituras entre os alvos do estudo, dentro de cada estrato, e verifica com que frequência uma atribuição feita ao acaso produz tantas coincidências quanto a verdadeira. Ele segue a lógica do procedimento de Pratt e Birge, provavelmente o mais adotado na área, e não supõe que as respostas de uma mesma leitura sejam independentes entre si [@okeeffe2005]. A permutação é exata enquanto os arranjos possíveis cabem numa enumeração; acima disso, usa 100 mil arranjos sorteados da semente revelada, que qualquer pessoa pode refazer.

O que a análise não faz também é declarado: multiplicar as probabilidades de acerto de cada campo como se fossem independentes, como fez Hyslop em 1919. O procedimento infla a evidência, porque as respostas sobre uma mesma pessoa se correlacionam [@okeeffe2005].

A regra de parada é vinculante: a coleta termina no N declarado, sem análise intermediária. E não há como espiar, porque as notas não existem antes do desmascaramento — não há resultado parcial a consultar, nem para o pesquisador. Qualquer análise que não esteja no pré-registro aparece no relatório rotulada como exploratória.

<!-- A FAZER: declarar o efeito esperado e o cálculo de poder, isto é, o tamanho de amostra que dá a um efeito real, se ele existir, boa chance de aparecer. Os efeitos publicados vêm de leituras livres pontuadas por consulentes, como os reunidos por @sarraf2021; não há estimativa prévia para um questionário fechado com conferência automática, e essa incerteza precisa estar escrita no artigo. -->

# 5. Arquitetura

Esta seção descreve como o software impõe o protocolo. Três ideias de criptografia sustentam quase todas as garantias, e vale apresentá-las antes.

::: {.quadro}
**Em termos simples**

Um **hash** é uma impressão digital de um conteúdo: uma sequência curta, calculada a partir dele, que muda por completo se uma única vírgula mudar. Do hash não se volta ao conteúdo, a não ser testando todas as possibilidades, o que só é viável quando elas são poucas (seção 5.3). O Crivo usa o SHA-256, um padrão.

Um **compromisso** funciona como um envelope lacrado: publica-se o hash de uma informação mantida em segredo e, quando o segredo é revelado, qualquer pessoa confere que ele é o mesmo de antes.

Um **carimbo do tempo** é o equivalente digital de um cartório: uma autoridade independente assina que certo hash já existia num dado instante.
:::

## 5.1 Visão geral

O Crivo é um monólito modular, isto é, um único programa dividido em módulos cujas fronteiras são verificadas por testes. É escrito em Java 21, com Spring Boot e Spring Modulith, guarda os dados no PostgreSQL, com as migrações de esquema feitas pelo Flyway, e renderiza a interface no servidor com Thymeleaf. A escolha atende a uma restrição de projeto: o sistema precisa ser operável por um único mantenedor, e a complexidade operacional é, ela própria, fonte de erro. Pela mesma razão, um auditor roda tudo na própria máquina só com o kit de desenvolvimento Java (JDK), sem contêiner: em desenvolvimento, a aplicação sobe sozinha uma instância embutida do PostgreSQL.

## 5.2 Cegamento como invariante de dados

Em computação, uma invariante é uma condição que precisa valer em qualquer estado do sistema. No Crivo, o cegamento é uma delas: ele não é aplicado na tela, e sim na camada de dados. Cada papel dispõe de consultas e objetos próprios que, por construção, não contêm os campos proibidos para ele: o campo não é escondido na interface, ele simplesmente não chega à resposta do servidor. Os dados que identificam as pessoas ficam cifrados, isto é, criptografados, num esquema separado do banco, que um único módulo pode ler; o restante do sistema trabalha com códigos pseudônimos, que não revelam quem é a pessoa. E as regras que não podem falhar são impostas pelo próprio banco de dados, não só pelo código da aplicação: o perfil de uma conta, consulente ou médium, não muda; nenhum nome é liberado ao médium sem pedido travado; e nenhuma nota pode existir antes do desmascaramento.

## 5.3 Compromissos salgados

Um compromisso feito com o hash puro das respostas seria frágil. Como cada campo tem poucas opções, o número de combinações possíveis é pequeno o bastante para que um computador as teste uma a uma até encontrar a que reproduz o hash publicado — e a trilha de auditoria é pública. Por isso o compromisso mistura às respostas um sal, um número aleatório de 32 bytes, guardado cifrado e revelado só no desmascaramento. Sem o sal, testar as combinações não leva a nada. A regra vale tanto para o pedido do consulente quanto para a leitura do médium, e o modo de cálculo é público, para que um terceiro o reimplemente.

## 5.4 Sorteio com compromisso e revelação

Todos os sorteios do estudo — o médium de cada par e a ordem em que os dois nomes lhe são apresentados — derivam de uma semente, um número secreto de 32 bytes produzido por um gerador criptográfico e guardado cifrado. Cada sorteio é uma função pública da semente e de entradas registradas; a lista de médiuns elegíveis, por exemplo, muda ao longo do estudo, e por isso é registrada a cada sorteio, com o hash na trilha. Como os pares se formam aos poucos, não há uma lista de sorteios para comprometer de antemão: o compromisso publicado no pré-registro é sobre a própria semente. Revelada a semente no fim, qualquer pessoa refaz todos os sorteios e confere que nenhum foi escolhido a dedo.

## 5.5 Pré-registro congelado

O pré-registro é um documento canônico, escrito numa forma única para que o mesmo conteúdo produza sempre o mesmo hash. Ele é resumido por SHA-256 e leva o questionário inteiro em anexo. Depois de congelado, o banco recusa alterá-lo: emendas criam uma nova versão, datada e justificada, e a original continua visível.

## 5.6 Trilha de auditoria e ancoragem externa

Todo evento do estudo entra numa tabela somente de acréscimo: nela se acrescentam registros, mas nenhum pode ser alterado, e cada um carrega o hash do anterior. Funciona como um livro de atas em que cada página traz a impressão digital da página anterior: alterar um registro antigo obriga a refazer todos os que vieram depois. E não se trata de convenção: o próprio banco recusa alterar ou apagar registros dessa tabela.

A cadeia, sozinha, não impede que quem opera a instalação a reescreva inteira, recalculando todos os hashes. Por isso, a cabeça da trilha — o hash do último evento, que resume todos os anteriores — é carimbada por uma autoridade independente de carimbo do tempo, segundo o RFC 3161. O carimbo vem logo depois de cada marco do estudo, como o congelamento do pré-registro, a revelação da semente e a publicação do relatório, e uma vez por dia, se a trilha tiver mudado. Hoje a autoridade é o FreeTSA, um serviço gratuito. Só o hash sai do servidor, e os comprovantes ficam públicos, para que qualquer pessoa os confira com ferramentas comuns, como o OpenSSL, sem depender do Crivo.

## 5.7 Ciclo automático e desmascaramento

Um ciclo periódico do servidor, executado a cada minuto, forma os pares, sorteia os médiuns, controla os prazos, reenvia os pedidos vencidos e, quando o N é atingido, desmascara o estudo. O ciclo é idempotente, isto é, rodá-lo duas vezes seguidas não muda nada. Ninguém escolhe o momento do desmascaramento, nem o pesquisador. Até lá, a trilha não liga nenhum médium a nenhum par: o vínculo fica guardado no banco e só se torna público depois.

## 5.8 Verificação por terceiros

A verificação acontece em dois níveis. Quem opera a instalação, ou um auditor com acesso a ela, usa uma ferramenta de linha de comando, o `airlab verify`, que refaz a cadeia de hashes, confere o hash de cada pré-registro, reconfere os carimbos do tempo e, quando a semente já foi revelada, reproduz o sorteio comprometido. Quem só tem acesso ao que é público conta com a [página dos estudos](https://app.crivo.psigama.com/estudos), que mostra o pré-registro e contagens durante a coleta e o relatório inteiro depois do desmascaramento, e com o pacote de verificação.

O pacote é escrito uma única vez, no desmascaramento, e o hash dele entra na trilha, que é então carimbada. Traz o pré-registro congelado, a semente revelada, as respostas de cada pedido e de cada leitura usada na conferência, o sal de cada leitura, os envios aos médiuns com a lista de elegíveis de cada sorteio, as notas por par, as três análises, o relatório e a trilha do estudo. Uma segunda ferramenta, o `airlab verificar-pacote`, confere tudo isso a partir do arquivo, sem banco de dados e sem servidor: refaz a cadeia, confere o pré-registro e a semente, refaz cada sorteio e cada compromisso de leitura, recalcula a nota de cada par e as três análises e confere o hash do relatório.

Por proteção de dados, o pacote não traz o primeiro nome dos falecidos nem o sal dos pedidos (seção 7). O custo é declarado. Os compromissos dos pedidos não se refazem por fora e ficam atestados pela plataforma, que os confere no desmascaramento — e a ferramenta diz isso, em vez de passar calada. O pareamento também não se refaz por fora, porque depende das sinalizações de contas ligadas, que não são públicas.

# 6. Garantias e limites

Um software não torna um estudo válido por si só. Esta seção diz, com a precisão possível, o que o Crivo garante e o que não garante. Convém distinguir dois tipos de garantia. As garantias sobre o software, como a de que nenhum papel recebe informação proibida, vêm do código aberto e dos testes, que qualquer pessoa pode ler e rodar. As garantias sobre um estudo em particular vêm dos artefatos que ele deixa: o pré-registro, os carimbos, a trilha e o pacote de verificação.

## O que o sistema garante, de forma verificável

- O pré-registro e o compromisso do sorteio já existiam no instante atestado pelo carimbo do tempo do congelamento.
- As respostas do consulente estavam travadas antes de o nome chegar ao médium, e a leitura estava travada antes do desmascaramento. No caso da leitura, qualquer pessoa confere com o pacote. No caso do pedido, a ordem dos eventos é pública e carimbada, mas a correspondência entre as respostas publicadas e o compromisso é atestada pela plataforma (seção 5.8).
- Nenhum papel recebeu, pelo sistema, informação proibida pela matriz de visibilidade; é o que sustentam os testes que tentam violar cada célula.
- A sequência de eventos registrada não foi alterada depois de carimbada.
- Os sorteios foram os que a semente comprometida determina, e a pontuação e as análises são determinísticas: qualquer pessoa as refaz a partir do pacote.

## O que o sistema não garante

- Não distingue sobrevivência de telepatia com os vivos: as respostas existem na memória do consulente e no banco de dados.
- Não tem consulente substituto. Como os dois estudos sem interação analisados por @silva2022 deram nulo, um resultado nulo aqui não refuta os protocolos com *proxy*.
- O questionário fechado só captura o que cabe nos campos. As afirmações livres e específicas, que costumam ser as mais marcantes de uma leitura, ficam fora da nota.
- O primeiro nome carrega informação: o gênero e, às vezes, a geração e a região. O pareamento e os campos fora da nota reduzem esse vazamento, sem eliminá-lo, e um resultado positivo precisa ser lido à luz dele.
- Vazamento fora do sistema, como uma conversa entre participantes, e conluio entre um médium e um consulente que se conhecem e usam contas sem ligação aparente não são detectáveis com certeza. O sorteio do médium e a sinalização de contas ligadas reduzem o risco.
- Uma reescrita da trilha entre um marco e o carimbo seguinte, numa janela de cerca de um minuto em operação normal, não é detectada, e uma única autoridade de carimbo é um ponto único de confiança. A fase seguinte prevê somar uma âncora que não depende de nenhum operador, o OpenTimestamps, ancorado no Bitcoin.
- Os compromissos dos pedidos e o pareamento não se refazem por fora (seção 5.8).
- Validade de construto — se o questionário mede o que se pretende medir — e generalização para outros médiuns e outras condições são questões de método, não de software.

A delimitação é intencional: um sistema que alega mais do que pode garantir compromete a credibilidade que pretende produzir.

# 7. Considerações éticas e proteção de dados

Os estudos envolvem pessoas enlutadas e informações delicadas: a vida e a morte de alguém próximo e, por tabela, a crença de quem participa. O simples cadastro como médium pode revelar convicção religiosa, que a Lei Geral de Proteção de Dados Pessoais classifica como dado pessoal sensível, ao lado dos dados de saúde [@brasil2018, art. 5º, II]. As respostas sobre a pessoa falecida, que incluem a causa da morte e a religiosidade, recebem o mesmo cuidado.

As medidas são as de praxe em pesquisa com dados sensíveis, com a diferença de que o sistema as impõe: minimização, isto é, coletar só o necessário; pseudonimização; cifragem em repouso; consentimento versionado; desistência a qualquer momento, com apagamento dos dados identificáveis; e dados guardados no Brasil. O pacote de verificação é público, mas não traz primeiro nome nem dado de conta, e quem participa é informado, antes de aceitar o termo, de que uma desistência posterior ao desmascaramento não altera o pacote já publicado.

A inscrição só abre com a aprovação de um comitê de ética em pesquisa, no sistema CEP/Conep, e com um contato humano alcançável, porque alguém pode se inscrever em luto agudo; o sistema impõe as duas condições. A página do protocolo diz o que ele não é: não é carta nem mensagem da pessoa falecida, nem resposta a perguntas do consulente. A devolutiva usa linguagem cuidadosa e lembra que um resultado individual não fala da pessoa falecida nem do médium, porque o que o estudo avalia é o conjunto. E nenhum modelo de inteligência artificial participa da avaliação ou da pontuação.

<!-- A FAZER: citar as resoluções do Conselho Nacional de Saúde e as demais normas de ética em pesquisa aplicáveis, conferindo o texto vigente. -->

# 8. Estado atual e avaliação planejada

O fluxo inteiro está implementado, do cadastro ao relatório e ao pacote de verificação, com testes de cegamento e de integridade que rodam contra um banco de dados real a cada mudança no código. Nenhum estudo com participantes reais foi conduzido: em produção, a inscrição permanece fechada até a aprovação do comitê de ética, e o questionário ainda está em revisão. A avaliação planejada tem três etapas, nesta ordem:

1. **Testes de propriedade:** testes automatizados que tentam, de forma sistemática, violar cada célula da matriz de visibilidade e adulterar a trilha.
2. **Ensaio completo:** o protocolo inteiro, com participantes fictícios e tentativas deliberadas de quebrar o cegamento.
3. **Auditoria externa:** um pesquisador independente recebe só o material público e tenta reproduzir a análise e confirmar a integridade do estudo.

Só depois dessas etapas está previsto um estudo piloto.

# 9. Discussão e conclusão

O Crivo troca validade ecológica por controle. Validade ecológica é a semelhança entre as condições do estudo e aquelas em que o fenômeno costuma ocorrer — aqui, uma consulta de verdade, com conversa e resposta livre. A análise de @silva2022 sugere que essa semelhança importa, e o protocolo abre mão de boa parte dela. As consequências precisam ser ditas com clareza. Um resultado nulo neste protocolo dirá apenas que, nestas condições e com este tamanho de amostra, não houve evidência de desempenho acima do acaso; não refutará a mediunidade nem os protocolos com consulente substituto. Um resultado positivo terá a seu favor o fato de ter sido obtido sem que ninguém julgasse o ajuste e com cada etapa conferível por quem duvida dele. Ainda assim, precisará ser lido à luz do que o primeiro nome pode revelar, e não distinguirá entre as hipóteses sobre a origem da informação.

O desenho também abre caminhos. Vários grupos podem rodar o mesmo software e publicar no mesmo formato de verificação, o que permite uma replicação multicêntrica em que nenhum laboratório precisa confiar no outro. Pesquisadores céticos são usuários naturais do material de verificação, e o desenho foi pensado para eles. Duas extensões estão no horizonte: a qualificação cega de médiuns pelo mesmo mecanismo, antes da entrada num estudo, já que a seleção de médiuns com evidência prévia é uma das condições que a literatura associa a resultado positivo [@sarraf2021; @silva2022]; e protocolos de resposta livre com juízes externos.

Em resumo, o Crivo propõe transferir os detalhes operacionais de um estudo de mediunidade do domínio da confiança para o da verificação: cada regra de cegamento vira um teste, cada sorteio vira um compromisso público, e cada evento vira um elo numa cadeia carimbada. O objetivo não é favorecer uma conclusão, mas permitir que qualquer conclusão, positiva ou nula, seja conferida por quem duvida dela.

<!-- A FAZER: desenvolver esta seção depois do ensaio completo e da auditoria externa. -->

# Disponibilidade

Código-fonte e documentação, sob licença MIT: [github.com/rogeriocisi/crivo](https://github.com/rogeriocisi/crivo). Plataforma: [crivo.psigama.com](https://crivo.psigama.com). Pré-registros, relatórios e pacotes de verificação: [página dos estudos](https://app.crivo.psigama.com/estudos).

# Referências

::: {#refs}
:::
