# Plano de UX, linguagem e celular — Crivo

> Como tornar o site e a aplicação do Crivo rigorosos para quem pesquisa e, ao mesmo tempo, simples, convidativos e usáveis no celular para médiuns e consulentes. As regras que já valem estão na `SPEC.md`, seção 1.4. Este documento guarda o diagnóstico, a estratégia e as fases.
>
> Versão 1.1 · novembro/2026 · Fase 1 implementada na SPEC 0.23 · Fase 2 na SPEC 0.24, menos o áudio.

---

## 1. O problema e a ideia central

O Crivo tem dois públicos que costumam se evitar (Por quê, `/por-que`):

- **pesquisadores, céticos e comitês de ética**, que querem rigor verificável: pré-registro, sorteio conferível, trilha carimbada, análise declarada e código aberto;
- **médiuns e consulentes**, que podem ter pouca escolaridade, muitas vezes são mais velhos e chegam pelo celular, quase sempre por um link de WhatsApp. O consulente, além disso, está de luto.

Não são dois sites, e sim **uma verdade só, dita em três profundidades**. O rigor também não precisa ser diluído para o médium: é ele que convence. A página Por quê já diz: "O rigor que convence um é o que dá valor ao resultado do outro".

- Para o médium: aqui, se houver acerto, ninguém vai poder dizer que foi truque.
- Para o pesquisador: um protocolo que se defende sozinho.

As duas frases descrevem o mesmo mecanismo.

## 2. Diagnóstico de 16/11/2026, antes da Fase 1

### 2.1 Legibilidade

Medida nos templates com o índice de Flesch adaptado ao português (Martins et al., 1996):

`Flesch = 248,835 − 1,015 × (palavras por frase) − 84,6 × (sílabas por palavra)`

As faixas são: 75 a 100, muito fácil; 50 a 75, fácil; 25 a 50, difícil; 0 a 25, muito difícil. As sílabas foram contadas por grupos de vogais, então os números são aproximados e servem para comparar páginas entre si.

"Termos técnicos" conta, a cada mil palavras, as palavras de método: cognição anômala, AIR, protocolo, cegamento, desmascaramento, compromisso, hash, semente, trilha, pré-registro, chamariz, pareamento, auditoria, carimbo, criptográfico, exploratório, confirmatório, UTC, alvo e travado.

| Página | Flesch | Termos técnicos por mil palavras | Frases com mais de 25 palavras |
|---|---|---|---|
| Início (`/`) | 48 (difícil) | 62 | 36% |
| `/protocolos` | 32 (difícil) | 91 | 45% |
| `/protocolo/P1` | 42 (difícil) | 63 | 36% |
| Resultado dos testes P3 e P4 | 49 (difícil) | 67 | 50% |
| Painel do médium | 68 (fácil) | 39 | 10% |
| `/consulente` | 63 (fácil) | 5 | 29% |

`/consulente` prova que o projeto já sabe escrever para leigos ("Você perdeu alguém e quer participar. O que acontece."). Mas a página era exceção e ficava escondida: nem a página inicial nem o menu levavam a ela. Só se chegava por uma nota em Participar, por Garantias e pelo rodapé. E não existe página equivalente para o médium.

### 2.2 Linguagem

- A primeira coisa que um médium lê na página inicial é "cognição anômala" e "AIR (*Anomalous Information Reception*)". Para quem não é da área, "anômala" soa como "anormal".
- "Desmascaramento", a tradução usada para *unblinding*, quer dizer no português corrente expor uma fraude. A própria página Por quê usa a palavra nesse sentido, ao falar de casos "desmascarados como fraude". Prometer a um médium um "desmascaramento automático" é o pior sinal possível.
- "Pedido de consulta" cria a expectativa que o quadro "O que este experimento não é" depois precisa desfazer.
- As telas de participante usavam vocabulário de pesquisador: "A trilha pública recebeu o compromisso dela", "chamariz", a coluna "d do par" no painel do médium e prazos em UTC.

### 2.3 Celular

Medido no site publicado, num viewport de 375 × 812 px:

- O cabeçalho mostrava 7 links em duas linhas, em fonte monoespaçada de 12,5 px. Cada link tinha 21 px de altura para o dedo, quando o recomendado é de 44 a 48 px. Os rótulos tinham 11 px.
- Na página inicial, o primeiro botão ficava a 883 px do topo, abaixo da primeira tela. Quem chegava pelo celular via o menu e o parágrafo de jargão, e nenhuma ação.
- O `/protocolo/P1` ocupava 17 telas. "Criar uma conta" só aparecia na terceira, e a tabela do questionário vinha depois de 12 telas e transbordava para o lado.
- O `/consulente` ocupava 10 telas e terminava sem nenhum botão ou ação clara.
- Havia 39 textos com menos de 13 px na página inicial e 79 na do P1.
- Os campos do login tinham fonte de 15,2 px e 39 px de altura. Com menos de 16 px, o Safari do iPhone dá zoom quando a pessoa toca no campo.
- O fragmento `social` não tinha `og:image`. Um link do Crivo colado no WhatsApp, justamente o canal por onde o médium vai recebê-lo, aparecia sem imagem.
- O rascunho da leitura vive só na sessão ("Nada é gravado enquanto você preenche"), e o `application.yml` não define o tempo de sessão. O padrão do Spring Boot é de 30 minutos sem requisição. Um médium que se concentra por mais tempo numa leitura pode perder tudo ao enviar. **Ainda a medir.**

### 2.4 O que já estava certo e fica

- O tom do `/consulente`.
- "Não sei" e "Não recebi" bem explicados.
- Nenhuma fonte ou script externo, o que deixa o site leve em celular barato e em rede 3G.
- Modo escuro, link para pular ao conteúdo e respeito a quem pede menos movimento.
- Os limites declarados em cada protocolo.
- O "Fale com uma pessoa".

## 3. Uma verdade, três profundidades

Cada conceito existe em três camadas, uma dentro da outra:

| Camada | Para quem | Exemplo: o carimbo de tempo |
|---|---|---|
| 1. Em poucas palavras | todo mundo, no celular | "Cada etapa recebe um carimbo de data, como firma reconhecida em cartório. Ninguém consegue mudar depois." |
| 2. Como funciona | quem quer entender: dirigentes, estudantes, jornalistas | "Um serviço independente, fora do Crivo, assina a data e o resumo de tudo o que aconteceu até ali." |
| 3. Detalhe técnico | pesquisadores, céticos e auditores | "A cabeça da trilha, encadeada por SHA-256, é carimbada pelo FreeTSA (RFC 3161) depois de cada marco. Os tokens ficam em `/carimbos` e são conferidos por `airlab verify`." |

As regras entre as camadas:

1. A camada simples nunca afirma o que a técnica não sustenta. Cada bloco simples termina num "Detalhe técnico".
2. Nada é removido. O rigor fica a um toque, dentro de `<details>`, que funciona sem JavaScript e respeita a CSP `default-src 'self'`.
3. A camada técnica não é simplificada.
4. A camada 1 passa por duas revisões: um pesquisador confere a exatidão, e um participante leigo confere se entendeu.

## 4. Arquitetura: portas por papel

Os perfis do Crivo já são fixos (consulente ou médium), então uma entrada por papel corresponde exatamente ao sistema.

- **Página inicial:** uma frase simples e três portas, todas na primeira tela do celular.
  - *Perdi alguém*: leva ao `/consulente`.
  - *Sou médium*: leva ao `/medium`, que é nova.
  - *Pesquiso ou quero conferir*: leva ao `/pesquisa`, também nova.
  - Abaixo das portas ficam um link discreto para o teste rápido, sem cadastro, e uma faixa de confiança: código aberto, resultados publicados mesmo quando nulos, dados protegidos e, quando vier, a aprovação do CEP.
- **Navegação:** no celular, só "Crivo", "Entrar" e "Menu" (feito na Fase 1). Na Fase 2, o menu passa a agrupar quatro seções:
  - Participar: consulente, médium e teste rápido.
  - Como funciona.
  - Ciência e auditoria: protocolos, garantias, auditar, estudos, carimbos, artigo, referências e história.
  - Perguntas frequentes.

  O rodapé, que hoje tem 18 links soltos, vira três grupos com título.
- **Cor com significado:** o `crivo.css` já diz "Verdete: o lado do método. Sépia: o lado de quem participa." Falta aplicar isso em todo o site. Portas e páginas de participante ficam em sépia, mais quentes e com letra maior. Páginas de método ficam em verdete, densas e monoespaçadas.
- **Páginas novas:**
  - **`/medium`**, espelho do `/consulente`. Explica o que se pede (só os primeiros nomes, o questionário e um prazo) e por que essas condições existem: elas protegem a honestidade do médium. Diz também o que ele recebe no fim, quais são os seus direitos e o que aquilo não é. Não é certificado de mediunidade, e o resultado é do estudo, não do dom. Fica na aplicação, como o `/consulente`, porque descreve etapas do P1 (SPEC, seção 1.3, regra 2).
  - **`/pesquisa`**, uma página técnica única. Reúne desenho, matriz de cegamento, sorteio, pré-registro, análise primária, regra de parada, poder estatístico e limites, com links para a SPEC, o artigo, o código, o pacote de verificação, `/estudos` e `/carimbos`. Inclui "Como citar" e uma versão em inglês, porque a comunidade internacional de parapsicologia lê em inglês.
  - **`/perguntas`**, com perguntas como "Preciso ser espírita?", "O médium vai saber quem eu sou?", "Vou receber uma mensagem?", "Posso desistir?" e "Preciso de computador?".

## 5. Linguagem: voz e glossário

A voz é serena, respeitosa, direta e honesta. Nunca sensacionalista, nunca prometendo nada, nunca irônica com crenças. Nas telas do consulente, que está de luto, nada de tom de comemoração: sem exclamação e sem emoji.

Metas para a camada 1:

- frases de até 15 a 20 palavras, com uma ideia por frase;
- "você" e voz ativa;
- Flesch de pelo menos 60;
- no máximo 10 termos técnicos por mil palavras. O `/consulente` tem 5, então é possível.

Essas metas importam: cerca de 3 em cada 10 brasileiros de 15 a 64 anos são analfabetos funcionais, segundo o INAF.

| Termo de método | Na camada 1 |
|---|---|
| recepção anômala de informação (AIR) | quando o médium traz informações certas sobre quem já faleceu, sem ter como saber |
| protocolo cego, cegamento | ninguém sabe o que não deve saber |
| pré-registro congelado | as regras são escritas e lacradas antes de começar |
| compromisso criptográfico, hash | lacre digital, que prova que as respostas não mudaram depois |
| carimbo de tempo | como firma reconhecida em cartório: prova a data |
| trilha de auditoria | registro público, que não aceita borracha |
| sorteio verificável, semente | sorteio que qualquer pessoa pode refazer e conferir |
| chamariz | pessoa de comparação |
| desmascaramento | abertura dos resultados |
| `d` do par | "Sua leitura coincidiu em 7 respostas com a pessoa certa e em 5 com a de comparação." |
| UTC | horário de Brasília |
| código pseudônimo | código, sem o seu nome |

Antes e depois, com textos reais:

- **Painel do médium, depois de enviar a leitura**
  - Antes: "Leitura travada. A trilha pública recebeu o compromisso dela — sem o seu código e sem as respostas."
  - Depois: "Leitura travada e guardada. Ninguém pode mudá-la — nem você, nem a equipe. O resultado aparece aqui quando o estudo terminar."
- **Prazo**
  - Antes: "prazo até 2026-11-27 10:00 (UTC)".
  - Depois: "Responda até sexta-feira, 27 de novembro, às 7h (horário de Brasília)."
- **Página inicial** (Fase 2)
  - Antes: "Crivo é uma plataforma de pesquisa dos fenômenos de cognição anômala. A mediunidade, que é considerada um ramo…"
  - Depois: "O Crivo pesquisa a mediunidade com um método justo: o médium não sabe para quem responde, ninguém mexe nas respostas depois, e todo resultado é publicado, a favor ou contra." Um "Detalhe técnico" leva ao parágrafo de hoje.

Dá para acolher sem quebrar a neutralidade (Constitution P3): "Na sua casa espírita ou no seu terreiro, isso pode ter outro nome. Aqui, para não tomar partido, chamamos de leitura."

A troca de "pedido de consulta" por outro nome ("inscrição", "pedido de leitura") fica para os testes com participantes da Fase 0: o termo é do vocabulário do projeto desde a SPEC 0.5 e só muda com evidência.

## 6. Celular primeiro

O ponto de partida é um Android de entrada, com tela de 360 px, aberto a partir do WhatsApp, usado com uma mão e com dados pré-pagos. Primeiro se desenha para esse aparelho, depois para o computador.

1. **Cabeçalho:** um menu único em `<details>`, sem JavaScript, com letra de pelo menos 16 px e alvos de toque de 48 px. *Fase 1.*
2. **Primeira tela:** a frase simples e as três portas cabem antes do ponto em que a pessoa precisa rolar. *Fase 2.*
3. **Tipografia:** nada abaixo de 13 px (*Fase 1*); corpo de 18 px nas páginas de participante; maiúsculas espaçadas e fonte monoespaçada só como enfeite, nunca no que precisa ser lido; teste com zoom de 200%.
4. **Formulários:** campos com pelo menos 16 px de fonte e 48 px de altura (*Fase 1*), opção de mostrar a senha, e erros ao lado do campo, em linguagem simples.
5. **Questionário,** no pedido do consulente e na leitura do médium:
   - cada opção de resposta é um alvo grande, que se toca inteiro (*Fase 1*);
   - cada pergunta num cartão, com um contador "4 de 13 respondidas" que só aparece onde o navegador permite e não é necessário para responder;
   - "Não sei" e "Não recebi" separados visualmente das outras opções.
6. **Sessão:** medir o tempo de sessão e avisar antes de ela expirar no meio de uma leitura (item 2.3).
7. **Tabelas:** a do questionário e a das notas do médium viram cartões em telas estreitas. As notas do médium viraram texto por extenso na Fase 1.
8. **Páginas longas:** ganham um "Em 30 segundos" com 5 itens no topo, um sumário com links para as seções e uma ação clara no fim. O `/consulente` ganhou a ação no fim na Fase 1.
9. **WhatsApp:** uma `og:image` de 1200 × 630 (*Fase 1*); endereços curtos, fáceis de dizer em voz alta, como `crivo.psigama.com/medium` e `/consulente`; QR code no material impresso.
10. **Ouvir a página:** um áudio em voz humana, de 2 a 3 minutos, hospedado no próprio site (a CSP permite), para o `/consulente`, o `/medium` e o termo de participação. Quem lê com dificuldade está acostumado ao áudio de WhatsApp.
11. **Desempenho:** manter o que já é exemplar, sem dependência externa, e fixar um limite de peso, por exemplo menos de 100 KB por página. Testar num aparelho barato de verdade, com o TalkBack ligado.

## 7. Rigor para pesquisadores

- O `/pesquisa` em português e em inglês, com "Como citar" (BibTeX) e o histórico de versões da SPEC.
- Um DOI no Zenodo para cada versão do código, e os pré-registros espelhados no OSF. Um revisor reconhece esses sinais na hora.
- Um único aviso de estado, com três valores: Pronto, Em construção e Aguardando CEP. Ele substitui os vários avisos de "Estado honesto" que existem hoje.
- Cada afirmação técnica com link para o código e para o teste que a garante, como o `BlindingMatrixTest` para o cegamento.
- Nenhuma imagem "espiritual" nas páginas de pesquisa. As páginas em sépia trazem claramente o rótulo de páginas para participantes, e isso protege a credibilidade.
- As páginas técnicas também precisam funcionar no celular, porque o primeiro contato de muitos pesquisadores é um link aberto no telefone.

## 8. Comunicação: quem leva o Crivo até o médium

Pessoas simples confiam em pessoas, não em sites. É o "fluxo em duas etapas" descrito por Katz e Lazarsfeld. O médium vai chegar pelo dirigente do centro ou do terreiro, pelo grupo de estudo ou pelo canal de Espiritismo citado no `VISION.md`. O site precisa dar material a essas pessoas que fazem a ponte.

- **Kit para dirigentes:**
  - folheto A4 em três dobras, com QR code;
  - roteiro de 5 minutos para apresentar numa reunião;
  - mensagens prontas para WhatsApp: texto, áudio de 1 minuto e imagem quadrada;
  - versões impressas do `/consulente` e do `/medium`. A página do consulente já diz que "pode ser impressa e entregue em papel", mas falta uma folha de estilo para impressão.
- **Uma promessa e três provas,** repetidas iguais em todos os canais:
  - a promessa: um jeito justo de pesquisar a mediunidade;
  - as provas: ninguém sabe o que não deve, ninguém muda nada depois, e tudo é publicado;
  - o pesquisador recebe as provas em linguagem técnica, e o participante, em linguagem do dia a dia.
- **O que evitar:**
  - "prove o seu dom": é manipulador, e o resultado de um médium isolado quase não diz nada;
  - "comprovado cientificamente";
  - os clichês visuais de luz e de espíritos;
  - qualquer gamificação. A trava 1 da E10 também a proíbe: durante a espera, mostre a contagem ("o estudo tem 18 de 40 pares"), nunca o desempenho.
- **Selos de confiança:**
  - o número da aprovação do CEP, quando vier, em destaque nas páginas de participante;
  - o apoio institucional. Se o apoio pedido ao NUPES-UFJF se concretizar, é o selo que fala com os dois públicos ao mesmo tempo.
- **Cuidado com o luto:** o CVV (188, ligação gratuita, 24 horas, ou chat em cvv.org.br) no `/consulente` e na devolutiva. *Fase 1.*
- **Termo de participação em camadas:** hoje o termo aparece como um parágrafo único de cerca de 130 palavras. A proposta tem três partes:
  - seis tópicos curtos: o que pedimos, o que guardamos, quem vê, o que fica público, como sair e com quem falar;
  - o termo completo;
  - o áudio.

**O termo está no caminho crítico do CEP.** A Resolução CNS 466/2012, no item IV.1, exige linguagem clara e acessível, adequada à cultura e à condição socioeconômica do participante. A camada simples deve ficar pronta antes da submissão ao CEP, que deve receber os mesmos textos que vão para o site. Lembrete da SPEC, seção 1.3, regra 2: depois da aprovação, mudar o `/consulente` pode exigir emenda.

## 9. Como validar

- **Antes de reescrever:** 5 ou 6 sessões com médiuns e consulentes de escolaridades diferentes, no celular deles e a partir de um link de WhatsApp, e mais 3 sessões com pesquisadores.
- **Teste de compreensão,** depois de a pessoa ler o `/medium` ou o `/consulente`:
  - "O médium vai saber quem você é?"
  - "Como você desiste?"
  - "Quando sai o resultado?"
  - "O resultado diz se o médium é bom?"
  - "Você vai receber uma mensagem da pessoa?"

  A meta é que 80% das pessoas acertem pelo menos 4 das 5.
- **Tarefas para pesquisadores:** "Em 3 minutos, ache o pré-registro do estudo e confira o hash" e "Baixe o pacote de verificação".
- **Teste de 5 segundos na página inicial:** a pessoa diz para que o site serve e para quem é.
- **Funil,** depois que as inscrições abrirem: conta criada, e-mail confirmado, pedido ou leitura travada. As contagens já existem no painel do administrador, sem rastreador de terceiros (Constitution P6 e CSP).
- **A regra vira teste,** como no resto do Crivo: um teste que falha se uma página de participante cair abaixo de Flesch 60 ou passar de 10 termos técnicos por mil palavras, com a fórmula e a lista de termos da seção 2.1.

## 10. Fases e estado

| Fase | O que entra | Estado |
|---|---|---|
| 0. Escuta | Entrevistas e testes, glossário, guia de voz e metas de legibilidade | A fazer |
| 1. Correções rápidas | Menu no celular e alvos de toque; tamanhos mínimos de letra; campos de 16 px; horário de Brasília nas telas de participante e no e-mail do pedido de leitura; mensagens reescritas nas telas de participante; "abertura dos resultados" e "pessoa de comparação"; notas do médium por extenso, sem "d do par"; CVV; `og:image`; ação no fim do `/consulente` | Feita na SPEC 0.23 |
| 2. Camadas | Página inicial com três portas; `/medium`; `/perguntas`; `/pesquisa` com versão em inglês; detalhe técnico em `<details>` em Garantias e Como funciona; termo em camadas, com áudio. Tudo pronto para a submissão ao CEP | Feita na SPEC 0.24, com o menu e o rodapé agrupados e o termo `conta-v3`. Falta o áudio, que espera gravação em voz humana |
| 3. Comunicação | Kit para dirigentes; vídeo de 2 minutos e versões curtas na vertical; preprint, OSF e Zenodo | A fazer, perto da aprovação do CEP |
| 4. Medir e ajustar | Funil; novo teste de compreensão a cada versão; teste de legibilidade rodando a cada mudança | Depois que as inscrições abrirem |

A Fase 1 veio antes da Fase 0 porque não depende de escuta: são correções de leitura, toque e vocabulário que valem para qualquer público.

## 11. Cuidados com as regras do projeto

- O trabalho segue a SPEC: cada página nova entra primeiro numa versão da SPEC (CLAUDE.md, regras de trabalho).
- O `/medium` fica na aplicação, como o `/consulente`, porque descreve etapas do P1. As páginas do site estático não podem repetir etapas e só leem `site`, `aplicacao`, `secaoAtual` e `logado`, o que o `StaticSiteTest` verifica.
- Nada do plano fere os princípios da Constitution: o vocabulário continua neutro (P3), nenhum resultado parcial aparece (E10, trava 1) e nenhuma IA avalia nada (P10).
- Na PsiGama (repositório `psique`), a página `psigama.com/crivo` também está na faixa difícil, com Flesch 42. Ela pode virar um resumo curto que aponta para as três portas.
