# Especificação Macro — Crivo (codinome AIR Lab)

> Spec de alto nível. Cada épico abaixo vira uma spec detalhada própria antes da implementação.
> Conformidade obrigatória com `CONSTITUTION.md`. Contexto e motivação em `VISION.md`. Fundamentação científica em `REFERENCES.md`.
>
> Versão 0.29 · novembro/2026 · 0.29: **instalação única na VPS** (seção 1.3, revista; Constitution v1.8.0) — o site editorial sai do GitHub Pages e passa a ser servido pela própria VPS, do disco, atrás do mesmo Caddy da aplicação e num endereço só, `crivo.psigama.com`, com 301 do `app.crivo.psigama.com`, que está em e-mails já enviados; quem decide quem responde é o proxy — o arquivo estático quando existe, a aplicação para todo o resto —, e por isso nenhuma lista de rotas se repete na borda e uma rota nova responde sozinha; a implantação passa a ser automática a cada push na `main`, num workflow só, com a aplicação trocando antes do site e volta à versão anterior se a saúde não voltar; `AIRLAB_SITE_URL` fica vazia em produção, e a `og:image` passa a sair do endereço público da instalação, que o Open Graph exige absoluto; onde é a VPS — endereço, usuário, porta, caminhos, serviço — vive nos Secrets e Variables do GitHub, fora do repositório · 0.28: **dois idiomas** (seção 1.5) — o site e a aplicação passam a existir em português e inglês, com o português como idioma de origem e o inglês como tradução dele (Constitution v1.7.0); cada idioma tem o seu endereço, o inglês com o prefixo `/en`, e `/pesquisa/en` passa a `/en/pesquisa` com 301; a primeira visita cai no idioma do navegador, uma vez, sem consultar localização por IP, e o cabeçalho traz o seletor `PT · EN`, que leva à mesma página; a tradução é sobreposição, e o endereço em inglês sem tradução responde em português com aviso; catálogo, questionário, validações, e-mails e datas por extenso passam a existir nas duas línguas no código; o termo passa a `conta-v4` e o questionário a `v3-rascunho`, com as mesmas chaves da `v2-rascunho` e rótulos nos dois idiomas, porque o anexo congelado inclui os rótulos; o idioma não entra no pareamento, na trilha, na análise nem no pacote de verificação, e a tradução do termo e do questionário vai ao CEP · 0.2: seção 2.1 "O que é uma leitura", regra "um item = uma afirmação" e referências · 0.3: critérios de inclusão de médiuns no pré-registro, regra de parada vinculante e unidade de análise por par (seções 5 e 9) · 0.4: medida primária pareada com Wilcoxon exato, teste de permutação de Pratt e Birge, controles de comprimento e de pistas temporais, escala de itens de cinco categorias e desvios de protocolo — derivados dos artigos em `references/` · 0.5: *sitter* passa a ser **consulente** em todo o texto em português, com a forma inglesa registrada no glossário e mantida nos identificadores do código · 0.6: épico E10, autoatendimento e automação, com as travas obrigatórias · 0.7: o Crivo passa a ser uma plataforma de protocolos de cognição anômala, com catálogo de protocolos (seção 1.1) e os épicos E11 e E12 — dois testes integralmente automatizáveis · 0.8: a semente dos testes de autoatendimento passa a ter duas metades, uma do servidor e uma do participante, o que remove a necessidade de confiar na plataforma quanto à ordem dos fatos · 0.9: protocolo **P4, leitura estruturada automatizada** (épico E13), o ramo AIR integralmente automatizado: contas com perfil fixo (consulente ou médium), questionário fechado preenchido pelo consulente e pelo médium e conferência automática contra o alvo e o chamariz; o catálogo passa a se dividir em **protocolos AIR** e **experimentos extras** · 0.10: fase B do P4 — questionário versionado `v1-rascunho`, pedido de consulta travado com compromisso salgado, rascunho só na sessão, campos de pareamento iguais aos do P1 e formulário fechado em produção sem referência do CEP; seção 10 atualizada · 0.11: fase C do P4 — estudo P4 congelado numa tela com o questionário no anexo, compromisso sobre a semente, ciclo automático de pareamento e envio, um médium sorteado por par, leitura travada com compromisso salgado, prazo com reenvio, e nenhum código de médium na trilha antes do desmascaramento · 0.12: fase D do P4 — desmascaramento automático no N, conferência com integridade verificada, análise primária e secundárias, relatório com hash, devolutiva do consulente e do médium; e o esclarecimento, na página inicial, de que o P4 não é mensagem do falecido nem resposta a perguntas · 0.13: fase E do P4 — sinalização de contas ligadas por índice cego, sem bloqueio e com revisão humana; teto de pares por médium no pré-registro; página pública dos estudos e relatórios · 0.14: **o P1, leitura pareada conduzida por equipe, é descontinuado** — não será implementado; o Crivo segue só com o protocolo AIR totalmente automático (P4). Os épicos E4 a E7, que eram do P1, ficam no texto como registro e sem implementação · 0.15: **ancoragem externa da trilha por carimbo de tempo RFC 3161, fase 1, com o FreeTSA** (E8) — a cabeça da trilha é carimbada logo depois de cada marco (congelamento, emenda, revelação da semente, relatório) e uma vez por dia se mudou; o token é conferido antes de ser guardado, fica público e a CLI o reconfere · 0.16: **os testes de ESP — P2 (E11) e P3 (E12) — saem do catálogo e da navegação do site do Crivo** e passam a ser apresentados pela PsiGama, na página de testes de percepção extrassensorial (`https://psigama.com/pes/`); o motor, as rotas e as garantias continuam no servidor do Crivo (seção 1.1, regra 7) · 0.17: **renumeração dos protocolos, antes de qualquer dado real** — P1-automático (era P4), P2-supervisionado (era P1, descontinuado), P3-precognição (era P2), P4-visão-remota (era P3) —; os testes de ESP voltam a ser listados no site do Crivo; uma página de protocolos (`/protocolos`) e uma por protocolo (`/protocolo/P1` a `/protocolo/P4`), com as rotas `/testes/...` redirecionadas; o questionário em vigor publicado, campo a campo, na página do P1 · 0.18: vocabulário — o Crivo é a **plataforma**; os quatro **protocolos** são de dois tipos, dois **experimentos** de mediunidade (P1, P2) e dois **testes** de percepção extrassensorial (P3, P4); o catálogo ganha a coluna Tipo, e o glossário, os quatro termos · 0.19: regra 1 do catálogo separa as garantias de toda sessão (sorteio com compromisso e revelação, alvo fora do alcance de quem responde, trilha encadeada e carimbada) das de todo estudo (pré-registro, rótulo confirmatório pela política e pacote de verificação); as sessões anônimas dos testes têm as primeiras, não as segundas · 0.20: **protocolos antes do catálogo** (seção 1.2) — os estágios de conjectura e de concepção, que não aparecem em público; o **P5**, o Áxion, precognição com e sem retorno, entra **em concepção** como **experimento**, com o épico E14 em rascunho e as questões 14 a 18 em aberto, sem implementação e fora do catálogo; **o tipo de um protocolo passa a seguir a forma, não o ramo** — experimento roda em estudo pré-registrado, com conta, e só tem resultado no fim; teste roda em sessão anônima, com resultado na hora (seção 1.1, questão 13) —, sem mudar o tipo de P1 a P4; o código **P6** fica reservado para a chave póstuma, uma conjectura; o catálogo continua com P1 a P4, e nenhum código muda · 0.21: **site e aplicação em endereços separados** (seção 1.3) — as páginas editoriais, escritas à mão e sem nada que venha do código dos protocolos ou do banco, viram um site estático no GitHub Pages, em `crivo.psigama.com`, publicado a cada push na `main` sem passar pela VPS; a aplicação vai para `app.crivo.psigama.com`, com as páginas dos protocolos e o questionário, `/consulente`, contas, testes, estudos, carimbos e o estado da trilha, agora em `/verificar`; a página inicial, que tinha catorze seções, fica curta, e as seções vão para Participar, Por quê, Garantias e Auditar e para a página do P1, com o menu refeito · 0.22: **pacote de verificação do P1** (E9) — decisão de LGPD: público, depois do desmascaramento, com as respostas dos consulentes e das leituras e o sal de cada leitura, mas sem primeiro nome e sem o sal do pedido; escrito uma vez, com hash na trilha (`VERIFICATION_PACKAGE_EXPORTED`) e carimbado; uma checagem por lista fechada de chaves e opções retém a exportação se aparecer qualquer outra coisa; `airlab verificar-pacote` confere o arquivo sem banco e sem servidor; o termo de participação passa a `conta-v2` e diz o que o pacote publica · 0.22.1: no perfil de produção, a criação de conta do P1 também espera a referência da aprovação do CEP (E10, trava 4, que já pedia a aprovação antes de a inscrição abrir); antes, só os pedidos de consulta esperavam (E13, "Inscrição fechada até o CEP") · 0.22.2: o esboço do artigo técnico passa a ser página do site, em `crivo.psigama.com/artigo/`, no menu Biblioteca; o endereço antigo, `psigama.com/artigo-crivo/`, leva para lá · 0.23: **linguagem, celular e acessibilidade** (seção 1.4) — o plano para os dois públicos fica em `docs/UX.md`, e a sua primeira fase entra: as telas de participante deixam o vocabulário de método no detalhe técnico ("abertura dos resultados" no lugar de desmascaramento, "pessoa de comparação" no lugar de chamariz, as notas do médium por extenso); prazos e datas para participantes saem por extenso no horário de Brasília, também no e-mail do pedido de leitura, e as páginas de auditoria continuam em UTC; no celular, a navegação fica atrás de um Menu, sem script; nenhum texto com menos de 13 px, campos com 16 px e 48 px de altura; o CVV em `/consulente` e na devolutiva; `og:image` em toda página; e uma ação no fim de `/consulente`, conforme o estado da inscrição · 0.24: **segunda fase da seção 1.4** — a página inicial abre com uma frase simples e três portas por papel (consulente, médium, pesquisador), e o parágrafo técnico vai para um detalhe que se abre; três páginas novas na aplicação: `/medium`, espelho do `/consulente`, `/perguntas`, as perguntas frequentes, e `/pesquisa`, a página técnica única, com a versão em inglês em `/pesquisa/en`; o menu se agrupa em Participar, Como funciona, Perguntas, Pesquisa e Biblioteca, e o rodapé, em três grupos; Garantias e a página do P1 ganham a camada "em poucas palavras"; o termo de participação passa a `conta-v3`, com um resumo em seis pontos antes do texto completo, que não muda; o áudio das páginas de participante fica pendente, à espera de gravação em voz humana · 0.25: **questionário `v2-rascunho` e poder do P1** — o questionário vigente passa a ter 20 campos na nota, escolhidos pelos critérios da questão 9, agora escritos junto com a regra que os sustenta (o sinal de um campo cresce com 1 − Σp², a chance de dois falecidos do mesmo estrato terem respostas diferentes); a `v1-rascunho` continua legível para os pedidos feitos nela, e o pareamento não muda; a questão 1 ganha uma proposta de efeito, *d*<sub>z</sub> = 0,25 com N = 120 pares, calculada por simulação do próprio protocolo (`paper/poder/SimulacaoPoder.java`) e conferida pelo `PowerCheck` · 0.26: **só maiores de 18 anos criam conta** (E13) — a criação de conta exige a autodeclaração "Declaro ter 18 anos ou mais", uma caixa própria, separada do aceite do termo; o domínio recusa a conta sem ela, e a trilha registra no `ACCOUNT_CREATED` só o booleano; a data de nascimento não é pedida (minimização, Constitution P6), e o termo continua `conta-v3` · 0.27: **contato** (seção 1.4, regra 12) — o rodapé de toda página, do site e da aplicação, ganha um quarto grupo, Contato, com o endereço público do projeto, `contato@psigama.com`, e o menu ganha o item Contato, que leva a ele; o endereço fica escrito no fragmento, porque as páginas do site não leem configuração (seção 1.3), e não se confunde com o contato humano da inscrição (`AIRLAB_CONTACT_EMAIL`, E10, trava 4)

---

## 1. Escopo

O Crivo é uma plataforma de **protocolos cegos para pesquisa de cognição anômala**. O primeiro ramo é a **recepção anômala de informação (AIR)**, isto é a mediunidade, e nele está o protocolo que motivou o projeto: a leitura pareada com cegamento múltiplo. O segundo ramo são os testes de **percepção extrassensorial (ESP)**, nos quais não há informação sobre pessoa falecida e o alvo é sorteado pelo próprio sistema; eles rodam no Crivo e, desde a v0.17, voltam a ser listados no site dele, ao lado dos protocolos AIR e separados deles pelo ramo; a PsiGama também os apresenta.

A distinção entre os dois ramos não é cosmética. Na literatura, *anomalous information reception* designa especificamente a obtenção de informação sobre um falecido sem via sensorial; um teste de precognição com alvo gerado depois da resposta não é AIR, e chamá-lo assim seria impreciso. O termo que cobre os dois é **cognição anômala**, e é o que a plataforma usa. O codinome técnico **AIR Lab** permanece nos pacotes Java, na CLI e nos artefatos Maven, como herança do primeiro protocolo.

O ramo AIR tem **um protocolo ativo, o P1**, que roda inteiro pelo site, sem ninguém da equipe no circuito: o consulente responde a um questionário fechado sobre o falecido, o médium responde ao mesmo questionário conhecendo só o primeiro nome, e o sistema confere as respostas. O preço que ele paga — a ausência de experimentador *proxy* e de resposta livre — está declarado entre os seus limites (seção 1.1, regra 2).

**Decisão da v0.14: o P2 está descontinuado.** A leitura pareada, conduzida por uma equipe (experimentador *proxy* e redator) e mais próxima da literatura, não será implementada. O projeto concentra o esforço no que pode rodar sem equipe e com pontuação objetiva. O P2 **fica no site apenas como referência**: a página dele descreve o desenho e os limites, não tem inscrição e diz que não é implementado; o catálogo não o oferece entre os protocolos para participar. O código P2 fica reservado. O que o P2 já tinha e é compartilhado com o P1 — estudo e pré-registro (E1), participantes (E2), sorteio (E3), trilha e CLI (E8) — continua valendo; os épicos exclusivos do P2 (E4 a E7) ficam nesta especificação só como registro.

**Decisão da v0.17: renumeração e um catálogo só.** Antes de qualquer dado real — em produção não havia estudo, pedido, sessão nem evento de trilha —, os protocolos foram renumerados para que a numeração siga a ordem de importância: o protocolo ativo vem primeiro. Ao mesmo tempo, os testes de ESP, que a v0.16 tinha passado a apresentar só pela PsiGama, voltam a ser listados no site do Crivo, numa página de protocolos (`/protocolos`) com uma página por protocolo (`/protocolo/P1` a `/protocolo/P4`). A PsiGama continua apresentando os testes de ESP e aponta para essas páginas. O motor, as garantias e os dados não mudam. A correspondência com a numeração anterior, que vale para o histórico de versões e para o que foi escrito até a v0.16:

| Até a v0.16 | Desde a v0.17 | Protocolo |
|---|---|---|
| P4 | **P1-automático** | Leitura estruturada automatizada |
| P1 | **P2-supervisionado** | Leitura pareada cega (descontinuado na v0.14) |
| P2 | **P3-precognição** | Escolha forçada precognitiva |
| P3 | **P4-visão-remota** | Visão remota com quatro alvos |

**Dentro do escopo**: um estudo por vez e por protocolo, um pesquisador (o **administrador** da instalação), exportação de dados e verificação; o P1 com inscrição aberta por conta; os dois testes de ESP, P3 e P4, em autoatendimento público no site do Crivo.
**Fora do escopo**: multi-tenant, ganzfeld (exige emissor e cabine), presentimento e DMILS (exigem sensores), ESP em sonho (exige pernoite), juízes externos remunerados, aplicativo mobile, integração com IA na avaliação.
**Antes do catálogo** (v0.20): o P5, em concepção, e o P6, conjectura (seção 1.2). Nenhum dos dois é implementado, e nenhum aparece em público além do que a seção 1.2 permite.

### 1.1 Catálogo de protocolos

No site do Crivo, o catálogo é uma página só, **Protocolos** (`/protocolos`), com os quatro em ordem de código: o P1 em destaque, o P2 como referência e os testes de ESP, P3 e P4, que se fazem na hora e anônimos. Os protocolos AIR e os de ESP aparecem separados, com o ramo declarado (regra 4). Até a v0.15 os testes de ESP formavam a seção de **experimentos extras**; na v0.16 passaram a ser apresentados só pela PsiGama; desde a v0.17 voltam a ser listados aqui. O Crivo é a **plataforma**; cada um dos quatro é um **protocolo**, e eles são de dois tipos: os **experimentos** (P1 e P2, mediunidade), que envolvem consulente e médium, pedem conta e só têm resultado no fim de um estudo pré-registrado, e os **testes** (P3 e P4, percepção extrassensorial), anônimos, rápidos e com resultado na hora. **Desde a v0.20, o tipo sai da forma do protocolo, não do ramo**: experimento é o protocolo que roda em estudo pré-registrado, com conta, e só tem resultado no fim do estudo; teste é o que roda em sessão anônima, com resultado na hora. Para P1 a P4 as duas regras dão o mesmo tipo; a diferença aparece no P5, em concepção, que será um experimento do ramo ESP (seção 1.2). A implementação ainda deriva o tipo do ramo (`Protocol.Branch`) e passa a declará-lo em cada protocolo quando o P5 entrar no catálogo. O tipo aparece ao lado do rótulo, como em "P1-automático · experimento".

| Código | Nome curto | Tipo | Nome público | Ramo | Acaso | Automação | Épico |
|---|---|---|---|---|---|---|---|
| **P1** | automático | Experimento | Leitura estruturada automatizada | AIR (mediunidade) | 50% | **Integral**: conta, pedido, leitura, conferência e avisos sem ninguém da equipe | E13 |
| **P2** | supervisionado | Experimento | Leitura pareada cega — **descontinuado (0.14)**, só referência | AIR (mediunidade) | 50% | Parcial: exigia equipe — sessão síncrona com experimentador *proxy* e redator | E1–E9 (E4–E7 não serão implementados) |
| **P3** | precognição | Teste | Escolha forçada precognitiva | ESP (precognição) | 25% | **Integral** | E11 |
| **P4** | visão remota | Teste | Visão remota com quatro alvos | ESP (resposta livre) | 25% | **Integral** | E12 |

O rótulo de cada protocolo é o código seguido do nome curto — **P1-automático**, **P2-supervisionado**, **P3-precognição**, **P4-visão-remota**. A numeração mudou uma vez, na v0.17, antes de qualquer dado real. Daqui em diante os códigos não são renumerados nem reaproveitados: eles entram nos compromissos publicados, nas sessões, na trilha e no repositório.

Regras do catálogo:

1. **Garantias de toda sessão e garantias de todo estudo.** Todo protocolo oferece, em cada sessão, sorteio com compromisso → revelação, o alvo fora do alcance de quem responde até a resposta travar e trilha de auditoria encadeada por hash e carimbada. Quando um protocolo roda como **estudo** — o que os experimentos sempre fazem, e um teste só faz num estudo confirmatório à parte, com participantes identificados —, acrescentam-se pré-registro congelado, rótulo confirmatório decidido pela política e pacote de verificação. As sessões anônimas dos testes (P3 e P4) são exploratórias (E11, E12): têm as garantias de sessão, não as de estudo, e as páginas não prometem mais que isso. Um protocolo que não possa oferecer as garantias de sessão, ou as de estudo quando rodar como estudo, não entra.
2. **Cada protocolo declara o que o seu resultado não sustenta.** O P2 não distingue entre sobrevivência e telepatia com os vivos; o P3 e o P4 não dizem nada sobre falecidos. Isso é texto obrigatório na página do protocolo, não nota de rodapé.
3. **O acaso é declarado e verificável**, não estimado a partir dos dados.
4. **P3 e P4 não substituem um protocolo AIR.** Eles existem porque são integralmente automatizáveis e porque qualquer pessoa pode conferir a lisura do próprio teste depois; servem de demonstração do método, não de prova sobre mediunidade. Por isso a página de protocolos os mostra separados dos protocolos AIR, com o ramo (ESP) declarado.
5. **Um resultado do P1 não vale como replicação dos estudos com *proxy* da literatura.** O P1 ganha pontuação objetiva — ninguém julga se um item "encaixa" — e automação integral; perde o experimentador *proxy* e a resposta livre. Era esse o caminho inverso do P2, agora descontinuado.
6. **Protocolo descontinuado fica só como referência**: mantém o código e a página, com o status "Só referência, não implementado", e sai da lista de protocolos para participar. Códigos nunca são reaproveitados; a renumeração da v0.17, antes de qualquer dado real, foi a única exceção.
7. **Endereços** (v0.17). `/protocolos` lista os quatro protocolos; `/protocolo/P1` a `/protocolo/P4` são as páginas de cada um, com os limites declarados (regra 2). Os testes de ESP rodam em `/protocolo/P3/rodar` e `/protocolo/P4/rodar`, com os passos e o resultado sob o mesmo caminho. As rotas antigas — `/testes`, `/testes/{nome}`, `/testes/{nome}/rodar` e `/testes/{nome}/resultado` — respondem com redirecionamento permanente (301) para as novas, porque a página de ESP da PsiGama e links já publicados apontavam para elas. A página do P1 publica o questionário em vigor, campo a campo, com as opções e o que entra na nota (anexo do E13). Desde a v0.21 todos esses endereços são da aplicação, e desde a v0.29 ela e o site estão num endereço só, `crivo.psigama.com` (seção 1.3): o proxy manda para a aplicação tudo o que o site não tem, e os redirecionamentos 301 continuam sendo feitos por ela.

   **Aceite da regra 7**: a página de protocolos lista os quatro, com links para `/protocolo/P1` a `/protocolo/P4`; cada página imprime os limites; a do P1 mostra o questionário em vigor; as rotas antigas redirecionam com 301; e nenhum teste de E11 ou E12 é desativado.

### 1.2 Protocolos antes do catálogo

**Decisão da v0.20.** O catálogo da seção 1.1 lista só os protocolos que existem: P1 a P4. Antes de entrar nele, um protocolo novo passa por dois estágios que não aparecem em público.

| Estágio | O que existe | Onde aparece |
|---|---|---|
| **Conjectura** | Um código reservado e uma ideia, sem desenho | Só nesta seção e no documento de visão que a cite |
| **Em concepção** | Código, épico em rascunho nesta SPEC e documento de visão em `docs/` | Só nesta SPEC e em `docs/`; na PsiGama, no máximo o nome do projeto e a indicação "em concepção" |
| **No catálogo** | Épico aprovado e implementação, em construção ou pronta | `ProtocolCatalog`, `/protocolos`, a página do protocolo e as páginas da PsiGama |

| Código | Estágio | Nome | Tipo | Ramo | Onde está |
|---|---|---|---|---|---|
| **P5** | Em concepção (v0.20) | Precognição com e sem retorno — nome público **Áxion**, rótulo provisório **P5-retorno** | Experimento | ESP (precognição) | E14 e `docs/VISION-AXION.md` |
| **P6** | Conjectura (v0.20) | Chave póstuma | — | AIR (mediunidade) | Esta linha e `docs/VISION-AXION.md`, seção 12.3 |

Regras:

1. **Fora do catálogo, fora do site.** Um protocolo em conjectura ou em concepção não entra no `ProtocolCatalog`, nem com o status `PLANNED` ("Planejado"); não tem rota, página, tela, aviso nem e-mail no Crivo; e não é citado nas páginas da PsiGama que apresentam o Crivo, que continuam falando de quatro protocolos.
2. **O que a PsiGama pode dizer.** Um projeto em concepção pode ter página própria na PsiGama com o nome e a indicação "em concepção", sem desenho, sem código de protocolo e sem data. É o caso do Áxion (`psigama.com/axion`). Uma conjectura não aparece.
3. **Código reservado não volta.** P5 e P6 estão reservados. Se uma conjectura ou uma concepção for abandonada, o código fica sem uso e não é reaproveitado (seção 1.1, regra 6).
4. **Concepção não é especificação.** O épico de um protocolo em concepção é rascunho: muda sem emenda, e nada dele é implementado. Sair da concepção exige uma versão desta SPEC que decida as questões em aberto do protocolo e aprove o épico. Só então o protocolo entra no catálogo, com o status que a implementação justificar, e vale a regra 2 da seção 1.1: a página dele imprime o que o resultado não sustenta.
5. **De conjectura a concepção.** Uma conjectura passa a concepção quando ganha documento de visão e épico em rascunho. No caso do P6, o desenho da devolutiva depende do resultado do P5 (`docs/VISION-AXION.md`, seção 12.3).

### 1.3 Site e aplicação

**Decisão da v0.21, revista na v0.29 (instalação única).** O Crivo tem **um endereço**, `crivo.psigama.com`, e uma instalação, a VPS; o que muda é **quem responde** cada caminho. As **páginas editoriais** continuam sendo um **site estático** — texto escrito à mão, sem nada que venha do código dos protocolos ou do banco —, gerado pelo próprio JAR e espelhado em disco na VPS, de onde o Caddy as serve. Todo o resto é a **aplicação**, atrás do mesmo Caddy (Constitution, regras de implantação 3 e 9). Onde uma página mora continua dependendo da origem do conteúdo dela, e a diferença que isso faz continua valendo: publicar o site é escrever arquivo em disco, e não reinicia a aplicação. Até a v0.28 eram dois endereços, o site no GitHub Pages em `crivo.psigama.com` e a aplicação em `app.crivo.psigama.com`; o endereço antigo da aplicação responde com 301 para o único, e nenhum link já publicado ou já enviado por e-mail quebra.

| Quem responde | O que serve | Quando muda |
|---|---|---|
| O disco (site estático, `/var/www/crivo`) | Início (`/`), Participar (`/participar`), Por quê (`/por-que`), Garantias (`/garantias`), Auditar (`/auditar`), História (`/historia`), Referências (`/referencias`), Glossário (`/sobre`) e Artigo (`/artigo/`) | A cada push na `main`, pelo mesmo workflow que implanta, sem reiniciar a aplicação |
| A aplicação (`127.0.0.1:8080`) | O que vem do código dos protocolos ou do banco: `/protocolos` e `/protocolo/P1` a `/protocolo/P4`, com o questionário (seção 1.1, regra 7); `/consulente`, `/medium`, `/perguntas` e `/pesquisa`; contas e painéis; os testes de ESP; `/estudos`; `/carimbos`; e o estado da trilha (`/verificar`) | A cada push na `main`, com troca de JAR e reinício (Constitution, regra de implantação 8) |

Desde a v0.28 (seção 1.5), cada endereço das duas linhas existe também com o prefixo `/en`, do mesmo lado: o site escreve as suas páginas nos dois idiomas, e a aplicação responde às suas nos dois.

Regras:

1. **A página do protocolo é código.** O catálogo, os limites de cada protocolo (seção 1.1, regra 2) e o questionário em vigor saem do código que roda os protocolos. As páginas que os mostram ficam na aplicação, para nunca descreverem uma versão diferente da que está rodando.
2. **`/consulente` fica na aplicação.** Ela descreve o procedimento do P1 e os direitos de quem participa. Durante um estudo, isso não pode mudar com um push, e com CEP pode exigir emenda.
3. **O texto editorial não repete o normativo.** As páginas do site não reproduzem acaso, limites, etapas nem o questionário: apontam para a página do protocolo. O gerador do site entrega aos templates só os dois endereços e a seção atual, então uma página editorial não tem como ler o catálogo.
4. **Link para rota nova da aplicação só depois do deploy que a cria.** Desde a v0.29 os dois vão no mesmo push, e por isso a ordem está no workflow: a aplicação troca primeiro, e o site depois. Site novo com aplicação velha quebraria o link; o contrário, não.
5. **Endereço desconhecido é da aplicação.** Quem decide é o proxy: o arquivo estático quando ele existe, a aplicação para todo o resto — sem repetir ali nenhuma lista de rotas, então uma rota nova passa a responder sozinha (Constitution, regra de implantação 3). Isso cobre as rotas `/testes/...` (seção 1.1, regra 7), os links já publicados para `crivo.psigama.com/protocolo/...` e os links de e-mail. A página 404 do site continua sendo gerada, mas não é usada na instalação única: ela existe para uma cópia do site servida sozinha, sem aplicação atrás (Constitution, regra de implantação 9).
6. **Um processo só.** Com `AIRLAB_SITE_URL` vazia, a aplicação também serve as páginas do site, e o script único continua subindo tudo: assim no perfil `local` e, desde a v0.29, **também em produção**, onde o arquivo estático responde primeiro e a aplicação fica como rede de segurança para o caso de o arquivo não estar lá. `AIRLAB_SITE_URL` definida faz a aplicação responder às rotas do site com redirecionamento permanente (301) para o endereço configurado; ela só serve, por isso, quando o site mora **noutro** endereço — apontada para a própria instalação, seria um 301 para si mesma.
7. **O estado da trilha é da aplicação.** Eventos na trilha, estudos cadastrados e o hash do último evento saem do banco e ficam em `/verificar`. Num site estático, o hash ficaria congelado, o que numa página de auditoria seria enganoso.
8. **Um endereço, uma origem** (v0.29). Site e aplicação compartilham origem: o link de um para o outro é relativo, e o cookie, a CSP `default-src 'self'` e os arquivos estáticos valem para os dois. O que precisa de endereço absoluto — o link do e-mail e a imagem de compartilhamento (seção 1.4, regra 6) — sai do endereço público da instalação: `AIRLAB_BASE_URL` na aplicação, `--aplicacao` no gerador do site. Nenhum dos dois está escrito no repositório.

**A página inicial.** Até a v0.20, a página inicial tinha catorze seções. Desde a v0.21 ela é curta: a abertura, o esclarecimento de que o P1 não é mensagem do falecido (E13, telas por perfil) e os caminhos para as outras páginas. As demais seções foram para páginas próprias:

| Seções da página inicial até a v0.20 | Desde a v0.21 |
|---|---|
| O AIR automatizado, como participar, o que cada perfil vê, quatro formas de participar | Participar (`/participar`); o passo a passo e as telas continuam também na página do P1 |
| As oito etapas do P1 | Página do P1 (`/protocolo/P1`) |
| O que é a pesquisa, o problema, os dois públicos | Por quê (`/por-que`) |
| Garantias, o que o Crivo atesta e o que não atesta, o que é pedido e o que é garantido | Garantias (`/garantias`) |
| O estado desta instância, código aberto | Auditar (`/auditar`); o estado ao vivo, em `/verificar`, na aplicação |

**Menu**: Protocolos · Participar · Por quê · Garantias · Auditar · Biblioteca (História, Referências, Glossário) · Entrar, ou Painel para quem entrou. Protocolos, Entrar e Painel levam à aplicação. "Para participantes" (`/consulente`) e "Criar conta" saem do menu e ficam em Participar, na página do P1 e no rodapé. Quem exclui a conta vai para a página de entrada, com o aviso de conta encerrada, porque o site não tem como mostrá-lo. Desde a v0.23, no celular, o mesmo menu fica atrás de um botão Menu, e Entrar (ou Painel) continua à vista (seção 1.4, regra 4).

**Aceite da seção 1.3**: o gerador produz o site sem banco, só com as páginas do site, o CSS, o favicon, a imagem de compartilhamento e a página 404. As páginas do site e as da aplicação apontam umas para as outras pelos endereços configurados, que na instalação única são o mesmo. Com `AIRLAB_SITE_URL` definida, as rotas do site respondem com 301; sem ela, a aplicação as serve. Na VPS, o mesmo endereço entrega a página estática quando ela existe e a aplicação em todo o resto, e o endereço antigo da aplicação responde 301. A página inicial traz o esclarecimento do P1, `/protocolo/P1` traz as oito etapas, e `/verificar` mostra o estado da trilha. Nenhum teste de cegamento, de E11, de E12 ou da trilha é desativado.

### 1.4 Linguagem, celular e acessibilidade

**Decisão da v0.23.** O Crivo fala com dois públicos que costumam se evitar (Por quê, `/por-que`). De um lado, pesquisadores, céticos e comitês, que precisam de rigor verificável. Do outro, médiuns e consulentes, que podem ter pouca escolaridade e chegam pelo celular, quase sempre por um link de WhatsApp. O plano completo, com o diagnóstico medido e as fases, está em `docs/UX.md`. Esta seção fixa as regras que já valem e a primeira fase, que entra na v0.23. As fases seguintes entram em versões próprias.

Regras:

1. **Uma verdade, três profundidades.** Cada conceito pode ser dito em três camadas: em poucas palavras, como funciona e o detalhe técnico. A camada simples nunca afirma o que a técnica não sustenta, e nada é removido: o detalhe técnico fica a um toque, num `<details>`, que funciona sem script e passa pela CSP `default-src 'self'`.
2. **As telas de participante falam a língua de quem participa.** São elas: `/consulente`, criar conta, confirmar e-mail, entrar, os painéis do consulente e do médium, o pedido, a leitura, a devolutiva e minha conta. Nelas, o vocabulário de método fica no detalhe técnico:
   - compromisso criptográfico vira lacre digital;
   - trilha pública de auditoria vira registro público;
   - chamariz vira pessoa de comparação;
   - desmascaramento vira abertura dos resultados, porque no português corrente "desmascarar" é expor uma fraude;
   - o `d` do par é dito por extenso, como a diferença entre as duas contagens;
   - código pseudônimo vira código, sem o seu nome.

   As páginas de método (protocolos, estudos, carimbos, `/verificar`, artigo e as editoriais) mantêm os termos técnicos. O vocabulário continua neutro (Constitution P3).
3. **Horário de Brasília para quem participa, UTC para quem audita.** Nas telas de participante e nos e-mails para participantes, prazos e datas saem por extenso no horário de Brasília (`America/Sao_Paulo`), como em "sexta-feira, 27 de novembro, às 7h (horário de Brasília)". O elemento `<time>` guarda o instante em UTC. As páginas que ficam ao lado da trilha (administração, estudos, carimbos) continuam em UTC e dizem isso. Quando uma tela de participante mostra um instante que também está na trilha, o UTC aparece no detalhe técnico.
4. **Celular primeiro.** A referência é um Android de entrada, com tela de 360 px, aberto a partir do WhatsApp.
   - Nenhum texto tem menos de 13 px.
   - Campos de formulário têm fonte de 16 px e 48 px de altura.
   - Botões, links do menu e opções de resposta têm pelo menos 44 px de altura para o dedo.
   - Em telas de até 48rem, a navegação fica atrás de um botão Menu, num `<details>`, sem script, e Entrar (ou Painel) continua à vista.
5. **Apoio a quem está de luto.** `/consulente` e a devolutiva indicam um apoio concreto: o CVV, pelo telefone 188, ligação gratuita, 24 horas, ou pelo chat em cvv.org.br.
6. **Imagem de compartilhamento.** Toda página declara `og:image` (PNG de 1200 × 630), para que o link colado no WhatsApp mostre a imagem. O endereço é absoluto: a aplicação aponta para o site, e o site aponta para a aplicação. O gerador copia a imagem para o site, e a aplicação a serve.
7. **A página do participante termina com uma ação.** `/consulente` termina dizendo o que fazer: criar a conta, se a inscrição estiver aberta, ou, enquanto ela espera o CEP, saber disso e falar com uma pessoa.

**Aceite da seção 1.4 (v0.23)**:
- As telas de participante não mostram "desmascaramento", "chamariz", "d do par" nem UTC fora do detalhe técnico.
- Prazos e datas saem por extenso no horário de Brasília, também no e-mail do pedido de leitura.
- No celular, a navegação fica atrás do Menu.
- Nenhum texto tem menos de 13 px, e os campos têm 16 px e 48 px de altura.
- `/consulente` e a devolutiva indicam o CVV.
- Toda página declara `og:image`, e o gerador copia a imagem.
- `/consulente` termina com uma ação conforme o estado da inscrição.
- Nenhum teste de cegamento, de E11, de E12 ou da trilha é desativado.

#### Segunda fase (v0.24)

Regras:

8. **Três portas na página inicial.** A abertura diz numa frase o que o Crivo é e oferece três portas, que cabem na primeira tela do celular: *Perdi alguém* (`/consulente`), *Sou médium* (`/medium`) e *Pesquiso ou quero conferir* (`/pesquisa`). Abaixo delas ficam um link para os testes rápidos, sem cadastro, e uma faixa de confiança. O parágrafo técnico de antes (cognição anômala, AIR) continua na página, num detalhe técnico. Portas e páginas de participante usam a cor sépia, e as de método, o verdete.
9. **`/medium` fica na aplicação**, pelo mesmo motivo do `/consulente` (seção 1.3, regra 2): descreve o procedimento do P1 e os direitos de quem participa. Diz o que se pede ao médium, por que as condições protegem a honestidade dele, o que ele recebe no fim, os seus direitos e o que aquilo não é: não é certificado de mediunidade, e o resultado é do estudo. Termina com uma ação, como o `/consulente`.
10. **`/perguntas` fica na aplicação.** As respostas descrevem o procedimento, então mudam com o deploy, não com um push. Cada pergunta é um `<details>`, e cada resposta aponta para a página que tem o detalhe.
11. **`/pesquisa` é a página técnica única**, em português, com a versão em inglês em `/en/pesquisa` (até a v0.27, `/pesquisa/en`; seção 1.5, regra 1). Resume o desenho do P1, o sorteio, o pré-registro, a análise, a regra de parada, a trilha, o pacote de verificação e o que o resultado não sustenta, e aponta para a SPEC, o artigo, o código, `/estudos` e `/carimbos`. O acaso e os limites do P1 saem do catálogo (seção 1.1, regra 2), como na página do protocolo. Traz "Como citar".
12. **Menu agrupado.** Participar (para consulentes, para médiuns, testes rápidos, todas as formas de participar) · Como funciona (`/protocolo/P1`) · Perguntas · Pesquisa (para pesquisadores, protocolos, garantias, auditar, estudos, carimbos) · Biblioteca (por quê, história, referências, glossário, artigo) · Contato (desde a v0.27) · Entrar ou Painel. O submenu que contém a página atual fica marcado. O rodapé se divide nos mesmos três assuntos: participar, pesquisa e auditoria, e sobre o projeto; e, desde a v0.27, num quarto grupo, Contato (`#contato`), com o endereço público do projeto, `contato@psigama.com`, que é para onde o item Contato do menu leva.
13. **Garantias e a página do P1 em camadas.** Cada garantia abre com uma frase simples, e o texto técnico de hoje fica no detalhe técnico. A página do P1 abre com "em poucas palavras": cinco frases simples antes do desenho completo.
14. **Termo `conta-v3`** (desde a v0.28, `conta-v4`, com a tradução ao lado e o texto em português inalterado; seção 1.5, regra 9)**.** O termo de participação abre com um resumo em seis pontos (o que pedimos, o que guardamos, quem vê, o que fica público, como sair e com quem falar), e o texto completo da `conta-v2` continua igual logo abaixo. O resumo não diz nada que o texto completo não diga. O áudio das páginas de participante e do termo espera uma gravação em voz humana. Nenhuma página promete o áudio antes de ele existir.
15. **Ordem de publicação.** A página inicial e o menu do site apontam para rotas novas da aplicação. Para não quebrar a regra 4 da seção 1.3, o deploy da aplicação vai logo depois do push que publica o site.

**Aceite da segunda fase (v0.24)**:
- A página inicial tem as três portas, com links para `/consulente`, `/medium` e `/pesquisa`, e o parágrafo técnico num detalhe técnico.
- `/medium`, `/perguntas`, `/pesquisa` e `/en/pesquisa` respondem sem login e levam o cabeçalho e o rodapé de sempre.
- `/pesquisa` e `/en/pesquisa` mostram o acaso e os limites do P1 vindos do catálogo.
- No menu, a página atual e o seu submenu ficam marcados.
- A criação de conta exige o termo `conta-v3`, com o resumo antes do texto completo.
- Nenhum teste de cegamento, de E11, de E12 ou da trilha é desativado.

### 1.5 Dois idiomas

**Decisão da v0.28.** O site e a aplicação passam a existir em **português e inglês**. O português é o idioma de origem: é nele que o texto é escrito e revisado, e o inglês é tradução dele (Constitution, Parte III, regra de idioma, v1.7.0). O motivo é o público: a literatura de AIR é em inglês, os revisores, céticos e comitês que podem auditar o método quase todos leem inglês, e até a v0.27 só uma página, `/pesquisa/en`, falava com eles. Do outro lado, os participantes são brasileiros, e nada do que eles leem muda.

Regras:

1. **Um endereço, um idioma.** O português fica nos endereços de hoje (`/garantias`), e o inglês ganha o prefixo `/en` (`/en/garantias`). Nenhum endereço muda de conteúdo conforme quem o pede: o link compartilhado mostra a quem o recebe o que o remetente viu, a imagem de compartilhamento é a da página, e nada depende de cache por visitante. `/pesquisa/en`, da v0.24, passa a ser `/en/pesquisa`, com redirecionamento permanente (301) do endereço antigo, que já está publicado no rodapé e fora do projeto.
2. **A primeira visita cai no idioma do navegador.** Quem chega a um endereço em português sem ter escolhido idioma é levado **uma vez** ao equivalente em inglês, se o navegador não pedir português; a escolha fica guardada (cookie `idioma`, um ano) e o encaminhamento não se repete. Vale só em `GET`, nunca no envio de um formulário. **Não se consulta a localização por IP**: o idioma do navegador é o que a própria pessoa configurou, não exige serviço externo (Constitution P8) nem manda o endereço de IP de ninguém para terceiros (P6), e acerta o brasileiro no exterior, que o IP erraria. No site estático, o encaminhamento é um script próprio (`js/idioma.js`), que passa pela CSP `default-src 'self'`; na aplicação, é feito no servidor, antes de qualquer tela.
3. **Seletor sempre à vista.** O cabeçalho de toda página traz `PT · EN`, com o idioma atual marcado. Escolher fixa o cookie e leva à **mesma página** no outro idioma, nunca à inicial.
4. **Toda página se declara.** O atributo `lang` diz o idioma do conteúdo em **toda** página. Nas **páginas públicas**, que são as que têm a imagem de compartilhamento (seção 1.4, regra 6), vai também o par `<link rel="alternate" hreflang>` das duas versões, com `x-default` no português, e o `og:locale` do idioma da página. As telas de quem entrou levam `noindex` e não têm o que declarar a um buscador; elas mudam de idioma pelo seletor, como as outras.
5. **A tradução é sobreposição, e a falta dela se admite.** Um template em inglês mora em `templates/en/...` com o mesmo nome do português; se não existir, o endereço `/en/...` responde com a página em português e um aviso de uma linha, em inglês, de que aquela página ainda não foi traduzida. Isso permite publicar o idioma por partes sem link quebrado e sem página em branco, e torna visível o que falta — em vez de uma tradução automática que ninguém revisou.
6. **O que vem do código sai nos dois idiomas.** Catálogo (nome, resumo e limites de cada protocolo), questionário (rótulos e opções), mensagens de validação, e-mails e datas por extenso existem nas duas línguas no próprio código, e a página recebe o texto no idioma pedido. Nenhuma tela traduz nada na hora.
7. **Datas e prazos.** Para participantes, nos dois idiomas, continuam no fuso de Brasília e por extenso, no idioma da página: "sexta-feira, 27 de novembro, às 7h (horário de Brasília)" e "Friday, 27 November, at 7 a.m. (Brasília time)". O `<time datetime>` continua em UTC, e as páginas de auditoria e de administração continuam em UTC (seção 1.4, regra 3).
8. **O e-mail sai no idioma da conta.** A conta guarda o idioma escolhido na criação — preferência de interface, não dado pessoal novo —, e confirmação, pedido de leitura, aviso de prazo e devolutiva saem nele.
9. **O termo e o questionário em inglês são versões novas.** O termo de participação passa a **`conta-v4`**: o texto em português não muda uma palavra, e a tradução entra ao lado. O questionário passa a **`v3-rascunho`**, com **os mesmos campos, as mesmas opções e as mesmas chaves** da `v2-rascunho` e os rótulos nas duas línguas. A versão nova é obrigatória porque o anexo canônico congelado no pré-registro inclui os rótulos (E13, fase C): acrescentar um idioma muda o documento, e documento congelado não se altera (Constitution P2). A `v2-rascunho` continua legível para os pedidos feitos nela (E13).
10. **O idioma não é variável do estudo.** As chaves dos campos e das opções são as mesmas; o que muda é o rótulo que a pessoa leu. Uma resposta é, por isso, comparável qualquer que seja o idioma em que o questionário foi lido: o idioma **não entra no pareamento, nem nos estratos, nem na trilha, nem no pacote de verificação**, e não há estrato de idioma — ele fragmentaria os estratos sem necessidade. O anexo congelado publica os rótulos nas duas línguas, para que o auditor veja exatamente o que cada pessoa leu.
11. **A tradução do termo e do questionário vai ao CEP.** Antes da coleta com participantes reais, a tradução do termo e do questionário entra na submissão ao comitê, junto com o protocolo (Constitution P6). Enquanto não houver a referência da aprovação, a criação de conta continua fechada em produção, como a trava 4 do E10 já exige; o inglês não abre nada.
12. **Cegamento, sorteio, trilha e análise não mudam.** O idioma é camada de apresentação. Nenhuma regra da matriz da seção 4 muda, nenhum teste de cegamento ou de trilha é tocado, e o pacote de verificação de um estudo é o mesmo nos dois idiomas.

**Aceite da seção 1.5 (v0.28)**:
- Toda página do site e da aplicação responde em `/…` e em `/en/…`, com `lang` e o seletor `PT · EN` que leva à mesma página; as públicas trazem também o par `hreflang`.
- O endereço antigo `/pesquisa/en` responde com 301 para `/en/pesquisa`.
- Quem chega com navegador em inglês e sem cookie cai uma vez na versão em inglês; quem já escolheu não é mais encaminhado; nenhum `POST` é encaminhado.
- Um endereço `/en/…` sem tradução responde em português com o aviso de uma linha, nunca em branco nem com 404.
- O catálogo, o questionário, as mensagens de validação, os e-mails e as datas por extenso saem no idioma pedido, vindos do código.
- O termo é `conta-v4` e o questionário em vigor é `v3-rascunho`, com as mesmas chaves da `v2-rascunho` e rótulos nas duas línguas; o anexo canônico traz os dois idiomas.
- O gerador escreve o site nos dois idiomas, e a página 404 não encaminha nenhum endereço do site, em nenhum dos dois, para a aplicação.
- Nenhum teste de cegamento, de E11, de E12 ou da trilha é desativado.

## 2. Glossário

| Termo | Definição |
|---|---|
| **Estudo** | Uma instância de protocolo, com pré-registro, participantes e análise |
| **Plataforma** | O Crivo: o software, as garantias comuns e o catálogo de protocolos |
| **Protocolo** | Cada um dos quatro desenhos do catálogo (seção 1.1), de P1 a P4, com código e nome curto. O P5 e o P6 têm código, mas ainda não são protocolos do catálogo (seção 1.2) |
| **Experimento** | Protocolo que roda em estudo pré-registrado, com conta, e só tem resultado no fim do estudo; desde a v0.20, o tipo segue a forma, não o ramo. Hoje, o P1 e o P2, de mediunidade (AIR), com consulente e médium; o P5, em concepção, será um experimento do ramo ESP |
| **Teste** | Protocolo que roda em sessão anônima, rápida e sem conta, com resultado na hora. Hoje, o P3 e o P4, de percepção extrassensorial (ESP) |
| **Conjectura** | Ideia de protocolo com código reservado e nenhum desenho, que não aparece em público (seção 1.2). Hoje, o P6 |
| **Em concepção** | Estágio de um protocolo com código, épico em rascunho e documento de visão, sem implementação e fora do catálogo público (seção 1.2). Hoje, o P5 |
| **Médium** | Participante que produz as leituras |
| **Consulente** (em inglês, *sitter*) | Participante que conheceu uma pessoa falecida e avalia as leituras. A literatura internacional usa *sitter*: é o termo a procurar nos artigos e o identificador que permanece no código (`sitter_code`, `ParticipantRole.SITTER`) |
| **Falecido-alvo** | A pessoa falecida indicada por um consulente; o médium recebe apenas o primeiro nome |
| **Par** | Dois falecidos-alvo de perfil semelhante (por exemplo, mesmo gênero e faixa etária). Cada um serve de chamariz para o outro |
| **Experimentador (proxy)** | Quem conduz a sessão com o médium; conhece apenas o primeiro nome do falecido |
| **Leitura** | Conteúdo produzido pelo médium numa sessão (áudio e transcrição) |
| **Leitura redigida** | A leitura convertida em lista numerada de itens, sem pistas não relacionadas ao conteúdo (datas de sessão, referências ao experimentador etc.) |
| **Chamariz** | A leitura do outro falecido do par, apresentada ao consulente como alternativa |
| **Desmascaramento** | Revelação controlada de qual leitura era de qual alvo, depois que todas as avaliações forem fechadas |
| **Pacote de verificação** | Conjunto exportado que permite a um terceiro auditar e reexecutar a análise |
| **Administrador** | Quem opera a instalação e conduz o estudo: o pesquisador. Não é perfil que alguém escolha ao se inscrever; a conta vem da configuração do servidor |
| **Pedido de consulta** (P1) | O que o consulente envia: o primeiro nome do falecido e as respostas dele ao questionário, travadas com hash antes de qualquer médium receber o pedido |
| **Questionário** (P1) | Lista versionada de campos fechados sobre o falecido, a mesma para o consulente e para o médium, congelada no pré-registro |
| **Leitura estruturada** (P1) | As respostas do médium ao questionário, conhecendo só o primeiro nome do alvo |
| **Retorno** (P5; em inglês, *feedback*) | Ver, depois de responder, o alvo da própria tentativa. No P5, cada tentativa é sorteada com retorno ou sem retorno, e o alvo de uma tentativa sem retorno nunca é mostrado ao participante |
| **Farol de aleatoriedade** (P5; em inglês, *randomness beacon*) | Serviço público que publica, em intervalos fixos, valores aleatórios assinados que ninguém conhece antes da publicação. No P5, a rede drand, da League of Entropy |
| **Gráfico de exclusão** (P5) | Para cada condição, o maior efeito compatível com os dados. Faz de um resultado nulo um limite superior publicado |

### 2.1 O que é uma leitura

**Leitura** é tudo o que o médium produz numa sessão a respeito de um falecido-alvo: as afirmações que ele faz sobre aquela pessoa. É a matéria-prima do estudo, o dado bruto a ser avaliado.

O termo vem do inglês *reading*, usado na pesquisa sobre mediunidade. No vocabulário espírita, o mais próximo seria "comunicação" ou "mensagem" (por psicofonia ou psicografia). A troca é deliberada: "comunicação" já pressupõe que alguém está se comunicando, enquanto "leitura" descreve só o que aconteceu (o médium falou e isso foi registrado) sem afirmar de onde veio a informação. É o princípio P3 da Constitution, de neutralidade.

**Ciclo de vida de uma leitura no protocolo:**

1. **Sessão:** o experimentador diz ao médium apenas o primeiro nome do alvo (por exemplo, "Antônio"). O médium fala livremente sobre essa pessoa (aparência, personalidade, circunstâncias da morte, hobbies, relações, recados), e tudo é gravado.
2. **Leitura bruta:** a gravação e a sua transcrição.
3. **Leitura redigida:** a transcrição é convertida numa lista numerada de **itens**, cada um com **uma única afirmação verificável**. Saem as pistas espúrias, como o primeiro nome, referências ao experimentador e datas. Exemplo inventado:
   1. Era um homem alto, de mãos grandes.
   2. Gostava de mexer com marcenaria.
   3. Faleceu de forma repentina, ligada ao coração.
   4. Tinha uma relação difícil com um irmão.
   5. Mostra um relógio de bolso como algo importante.
4. **Avaliação cega:** o consulente recebe duas leituras redigidas, a do seu falecido e a do chamariz, sem saber qual é qual. Ele pontua cada item, dá uma nota global para cada leitura e escolhe qual acredita ser a sua.

**Por que itens e chamariz:** afirmações genéricas ("era uma pessoa querida pela família") tendem a "acertar" para qualquer um. O chamariz expõe exatamente isso: só conta como evidência o que se ajusta ao alvo verdadeiro **mais** do que ao chamariz, de forma consistente e acima do acaso. A decomposição em itens verificáveis segue a prática da literatura (ver `REFERENCES.md`, Rocha et al. 2014 e Beischel et al. 2015).

## 3. Fluxo do protocolo (visão ponta a ponta)

```
[1] Pesquisador cria o estudo e escreve o pré-registro (hipóteses + plano de análise)
        │
[2] Consulentes dão consentimento e cadastram um falecido-alvo (dados ficam no esquema identity)
        │
[3] Sistema forma os pares por critérios pré-registrados
        │
[4] Sistema sorteia: ordem das leituras, médium por par, experimentador, ordem de apresentação
    → publica o hash (semente + alocações) + carimbo de tempo   ◄── CONGELAMENTO DO PRÉ-REGISTRO
        │
[5] Sessões: o experimentador recebe só o primeiro nome do alvo; o médium lê os dois alvos do par
        │
[6] Transcrição (opcional, local) → redação em lista de itens, feita por alguém que não sabe a qual consulente cada leitura pertence
        │
[7] Cada consulente recebe as duas leituras do par em ordem sorteada, sem rótulo, e:
      • pontua cada item
      • dá uma nota global para cada leitura
      • escolhe qual das duas acredita ser a sua
        │
[8] Todas as avaliações são fechadas e bloqueadas
        │
[9] Desmascaramento (irreversível, auditado) → revelação da semente
        │
[10] Análise pré-registrada + exploratória (rotulada) → relatório → pacote de verificação
```

## 4. Camadas de cegamento (matriz de visibilidade)

Esta matriz é a **especificação de segurança central**. Cada célula "✗" vira um teste automatizado (Constitution P1).

| Informação ↓ / Papel → | Pesquisador | Experimentador | Médium | Redator | Consulente |
|---|---|---|---|---|---|
| Identidade do consulente | ✓ | ✗ | ✗ | ✗ | só a própria |
| Dados do falecido além do primeiro nome | ✓ (até o congelamento) | ✗ | ✗ | ✗ | só o próprio |
| Primeiro nome do falecido | ✓ | ✓ | ✓ (na sessão) | ✗ | só o próprio |
| Ligação leitura → falecido/consulente | ✗ até o desmascaramento | ✓ (só da própria sessão) | ✓ (só da própria sessão) | ✗ | ✗ até o desmascaramento |
| Leitura bruta (áudio) | ✗ | ✓ (própria) | ✗ | ✓ | ✗ |
| Leitura redigida | ✗ até o desmascaramento | ✗ | ✗ | ✓ | ✓ (o par dela, sem rótulo) |
| Avaliações de outros consulentes | ✗ até o desmascaramento | ✗ | ✗ | ✗ | ✗ |
| Semente do sorteio | ✗ até a revelação | ✗ | ✗ | ✗ | ✗ |

> Nota: experimentador e redator devem ser **pessoas diferentes**. Se faltar gente no piloto, o desvio é registrado como limitação no pré-registro.

## 5. Épicos

> **E4, E5, E6 e E7 eram do P2 e estão descontinuados desde a v0.14.** O texto deles fica como registro das decisões de desenho, e nada neles deve ser implementado. E1, E2, E3, E8 e E9 continuam valendo para o P1 naquilo em que se aplicam; o E10 e o E13 são o centro do projeto. O E14 é do P5, em concepção (seção 1.2): é rascunho, e nada nele deve ser implementado.

### E1. Estudo e pré-registro
**Objetivo**: criar um estudo e congelar o pré-registro de forma verificável.
- Criar o estudo com título, protocolo (v1: pareado), hipóteses, **critérios de recrutamento e triagem de médiuns**, critérios de pareamento, escalas de pontuação, análise primária e secundária, regras de exclusão, **medida primária**, **efeito esperado**, **unidade de análise**, **regra de parada**, tamanho-alvo da amostra e **diferença máxima de itens entre as duas leituras de um par**.
- O documento congelado declara o **poder estatístico** do desenho: o mínimo de unidades para o efeito declarado e se o tamanho-alvo o atende. Um desenho subdimensionado não é recusado — um piloto é legítimo — mas fica dito no próprio pré-registro, para que um resultado nulo não possa depois ser apresentado como ausência de efeito.
- **Desvios de protocolo** são registrados contra a versão congelada, com data e motivo, em tabela *append-only*, e aparecem no relatório ao lado do resultado. Desvio não é emenda: a emenda muda o plano e cria nova versão, o desvio registra que a execução se afastou do plano que já estava congelado (Nosek et al., 2018).
- Os critérios de inclusão de médiuns são pré-registrados porque selecionar médiuns por desempenho anterior infla o efeito. Os resultados positivos da literatura vêm de "médiuns selecionados" sem que os critérios de seleção tenham sido publicados; aqui eles entram no documento com hash, antes da coleta.
- Gerar o documento canônico do pré-registro (Markdown/JSON normalizado) → SHA-256 → carimbo de tempo (RFC 3161/OpenTimestamps) → espelho opcional no OSF.
- Emendas versionadas, com justificativa; as versões anteriores continuam visíveis.

**Aceite**: depois do congelamento, nenhum campo do pré-registro pode ser alterado; a verificação do hash pela CLI confere com o documento publicado.

### E2. Participantes e consentimento
**Objetivo**: cadastrar participantes com privacidade e ética.
- Termo de consentimento versionado, com aceite específico e destacado para dados sensíveis (LGPD) e opção explícita de desistência.
- O consulente cadastra um falecido-alvo: primeiro nome, gênero, faixa etária no óbito e período do falecimento. Detalhes adicionais são opcionais e ficam só para a devolutiva.
- Código pseudônimo gerado para cada participante; os dados identificáveis ficam apenas no esquema `identity`, com criptografia.
- Desistência: os dados do participante são removidos ou anonimizados conforme o termo, e o evento é registrado na trilha.

**Aceite**: nenhuma consulta fora do módulo `participant` acessa o esquema `identity` (teste de arquitetura).

### E3. Pareamento e sorteio
**Objetivo**: formar os pares e sortear as alocações de forma verificável.
- Pareamento por critérios pré-registrados (por exemplo, mesmo gênero e faixa etária).
- Sorteio com `SecureRandom`: médium por par, experimentador por sessão, ordem das leituras, ordem de apresentação ao consulente e **qual dos dois consulentes do par é a unidade independente da análise confirmatória** (ver E7).
- Esquema compromisso → revelação (Constitution P5).

**Aceite**: com a semente revelada, uma CLI reproduz exatamente as mesmas alocações.

### E4. Sessões — *descontinuado (P2)*
**Objetivo**: conduzir as leituras sem vazamento.
- Agenda de sessões **contrabalanceada**: o sorteio atribui índices de dia e de horário, de modo que nenhum par ocupe sistematicamente o primeiro ou o último horário do dia. É a defesa contra pistas temporais — uma referência ao tempo dentro da leitura ("aquele acidente de hoje") permite ao consulente deduzir qual leitura é a dele. O'Keeffe e Wiseman (2005) foram os primeiros a controlar isso.
- O experimentador vê apenas o primeiro nome do alvo e um roteiro padronizado.
- Upload do áudio com renomeação para ID aleatório e remoção de metadados.
- Registro de ocorrências (por exemplo, "o médium perguntou algo", "a ligação caiu").
- Transcrição local opcional (whisper.cpp), com revisão humana.

**Aceite**: o arquivo armazenado não contém nome original, metadados de dispositivo nem dados de geolocalização.

### E5. Redação — *descontinuado (P2)*
**Objetivo**: transformar a leitura em lista de itens avaliáveis, sem pistas espúrias.
- O redator vê a transcrição sem saber a qual consulente ela pertence.
- Divide em itens numerados, com **uma única afirmação verificável por item**. Itens com várias afirmações dificultam a pontuação, uma crítica feita ao formato de O'Keeffe & Wiseman, 2005 (ver `REFERENCES.md`).
- Remove referências ao experimentador, datas da sessão e o primeiro nome do alvo. Essa última remoção é necessária porque, dentro de um par, o nome revelaria a leitura.
- Cada redação fica versionada e auditada.

**Aceite**: as duas leituras de um par chegam ao consulente em formato idêntico e sem o primeiro nome.

### E6. Avaliação cega — *descontinuado (P2)*
**Objetivo**: o consulente avalia as duas leituras do par sem saber qual é a dele.
- Acesso por link de uso único e expirável (sem conta permanente). Vale para o P2; no P1 o consulente tem conta, mas não avalia leitura nenhuma (E13).
- Para cada item: escala pré-registrada de cinco categorias — *encaixe óbvio / encaixe parcial ou interpretado / sem encaixe / **encaixa em outra pessoa próxima** / não sei avaliar*. As duas categorias intermediárias exigem que o avaliador **escreva a interpretação**, e só o encaixe óbvio conta para a acurácia da medida primária. A categoria "encaixa em outra pessoa" existe porque, sem ela, o item que descreve um parente vivo é forçado a virar acerto ou erro, e em qualquer dos dois casos a explicação alternativa desaparece da conta.
- Os itens são apresentados em **ordem sorteada**, derivada da semente, para que uma pista num item não contamine a leitura da lista inteira.
- As duas leituras de um par chegam com **comprimento comparável**: se a diferença de itens passar da tolerância pré-registrada, a mais longa é cortada por subamostra sorteada da semente. Nos dados de O'Keeffe e Wiseman as leituras iam de 6 a 92 afirmações, e a mais longa e mais genérica recebeu as notas mais altas de todos os consulentes — comprimento, por si só, é pista e é confundidor.
- Nota global de 0 a 6 para cada leitura e escolha forçada entre as duas, com grau de confiança.
- Ao enviar, a avaliação é bloqueada.

**Aceite**: a interface e a API do consulente não expõem nenhum identificador que permita inferir qual leitura é a dele, nem por ordem, ID sequencial ou data.

### E7. Desmascaramento e análise — *descontinuado (P2); no P1, ver E13, fase D*
**Objetivo**: revelar e analisar exatamente como foi pré-registrado.
- Desmascaramento permitido só quando todas as avaliações estiverem fechadas (ou o prazo pré-registrado tiver expirado). É uma ação explícita e irreversível.
- Revelação da semente.
- **Análise primária**: diferença pareada alvo − chamariz, uma observação por par, com **teste de Wilcoxon exato unilateral**. A diferença é medida na nota global ou no percentual de itens com encaixe óbvio, conforme declarado no pré-registro.
- **Análises secundárias**: proporção de escolhas forçadas corretas comparada a 0,5 (binomial exato), e **teste de permutação de Pratt e Birge** sobre a matriz de notas, que é a análise mais endossada no campo por não assumir independência entre os itens de uma leitura.
- A escolha forçada **deixa de ser a medida primária**. Ela descarta o tamanho da diferença, e Beischel e Schwartz já esperavam que fosse o indicador menos sensível — os números deles confirmaram (notas globais p = 0,007; escolha forçada p = 0,01). Com o efeito que a meta-análise de Sarraf et al. (2021) reporta, a troca decide a viabilidade do estudo: ver a tabela em `docs/PLAN.md`, seção 4.
- **Análise vedada**: multiplicar as probabilidades individuais dos itens como se fossem independentes (Hyslop, 1919). Infla a acurácia, porque os itens de uma leitura são correlacionados por construção, e já foi refutada na própria literatura do campo.
- **Unidade de análise: o par.** Os dois consulentes de um par avaliam o mesmo material, em espelho, e por isso as duas escolhas não são independentes. Entra na análise confirmatória apenas a escolha do consulente que o sorteio designou para aquele par, definida antes da primeira sessão. A escolha do outro consulente é coletada e relatada como análise secundária.
- **Regra de parada vinculante.** A coleta para no tamanho-alvo pré-registrado, sem análise interina. Se os dados não corresponderem ao que foi declarado — coleta interrompida antes, continuada depois, ou pré-registro não congelado —, o sistema **rotula o resultado como exploratório** e imprime o motivo ao lado. Isso responde à crítica de parada opcional (*optional stopping*).
- Análises secundárias: nota global alvo × chamariz (Wilcoxon pareado) e proporção de itens corretos alvo × chamariz.
- Análises não pré-registradas aparecem numa seção "Exploratória".
- Relatório final com o mesmo modelo para qualquer resultado.

**Aceite**: a mesma análise, rodada pelo script em R do pacote de verificação, produz os mesmos números (dentro da tolerância numérica). Uma análise fora dos termos pré-registrados nunca aparece rotulada como confirmatória, nem no relatório nem no pacote exportado.

### E8. Auditoria e verificação
**Objetivo**: permitir que qualquer terceiro confira o estudo.
- Trilha de eventos append-only encadeada por hash.
- CLI `airlab verify` que valida: a cadeia de hashes, o hash do pré-registro, a reprodução do sorteio a partir da semente e a reexecução da análise.
- Registro de incidentes de cegamento, com as sessões afetadas marcadas.

**Aceite**: a alteração manual de qualquer registro no banco é detectada pela CLI.

#### Ancoragem externa — fase 1: FreeTSA (v0.15)

A trilha encadeada por hash detecta qualquer alteração de um evento isolado, mas não impede que quem opera a instalação **reescreva a cadeia inteira** e recalcule todos os hashes. No P1 isso importa: o compromisso da semente e o hash do pré-registro são publicados pela própria plataforma, e nenhum participante está no circuito no momento do congelamento (questão em aberto 8). A ancoragem externa fecha essa porta: um terceiro independente atesta que a cabeça da trilha já existia num instante, e a reescrita posterior passa a ser detectável.

- **O que se carimba: a cabeça da trilha.** O valor enviado é o próprio hash do último evento — 32 bytes, já um SHA-256 —, como `messageImprint` com algoritmo SHA-256. Como cada evento contém o hash do anterior, um carimbo atesta a trilha inteira até aquele evento, inclusive o hash do pré-registro e o compromisso da semente (evento de congelamento) e o hash do relatório.
- **Só sai o hash.** Nenhum dado pessoal, nenhum conteúdo: o carimbador recebe 32 bytes que não revelam nada (P6). O FreeTSA guarda só o IP de quem pede, para coibir abuso, e não guarda os hashes.
- **Quando**: (1) **logo depois de cada marco** — congelamento e emenda de pré-registro, revelação de semente e relatório gerado —, verificado a cada minuto pelo servidor; (2) **uma vez a cada 24 horas**, se a trilha mudou desde o último carimbo. Nunca mais de um pedido por verificação, bem abaixo do limite do FreeTSA (um por segundo por IP).
- **Autoridade de carimbo**: FreeTSA (`https://freetsa.org/tsr`), gratuito, uso comercial e automatizado permitido, sem SLA. Os certificados da autoridade e da raiz vão no repositório, e a aplicação confere o SHA-256 de cada um contra o valor publicado pelo FreeTSA antes de usá-los. A URL e os certificados são configuráveis, para trocar de autoridade sem mudar código.
- **O token é conferido antes de ser guardado**: situação "concedido", o mesmo `messageImprint` enviado, o mesmo *nonce*, assinatura válida pelo certificado fixado, e esse certificado emitido pela raiz fixada, com uso estendido de carimbo do tempo. Token que não confere é recusado e registrado como falha.
- **Trilha**: o carimbo concedido entra como evento, com o código do carimbo, a posição e o hash carimbados, a hora atestada pela autoridade, o número de série e o SHA-256 do token. **A falha não entra na trilha**: ela não muda o estado de nenhum estudo, e um carimbador fora do ar por um dia encheria a trilha pública de ruído. Ela fica registrada na tabela dos carimbos, com o motivo, e o servidor tenta de novo depois de 15 minutos.
- **Nada no estudo espera pelo carimbo**, nesta fase. Uma indisponibilidade do FreeTSA atrasa a âncora, não a coleta; a janela entre o marco e o carimbo fica registrada pelas duas horas, a do evento e a atestada.
- **Público**: a página `/carimbos` lista cada carimbo concedido — posição, hash, hora atestada, autoridade — e entrega o token (`.tsr`) para quem quiser conferir por fora com `openssl ts -verify`, sem depender do Crivo.
- **CLI**: `airlab verify` reconfere cada token guardado (assinatura, certificados e `messageImprint`) e confere que o hash carimbado é o do evento daquela posição na trilha.
- **Append-only**: o banco recusa alterar ou apagar um carimbo, como recusa na trilha.

**Aceite da ancoragem**: nenhum carimbo é guardado sem conferência do token; o hash carimbado é sempre o de um evento da trilha; um marco sem carimbo é carimbado na verificação seguinte; a CLI detecta token adulterado ou hash que não é o da trilha; nada pessoal sai para o carimbador.

**Limites declarados desta fase**: um operador que reescreva a trilha **antes** do primeiro carimbo depois do marco não é detectado; a janela é de cerca de um minuto em operação normal e cresce enquanto o FreeTSA estiver fora do ar. Uma autoridade só é um ponto único de confiança: se o FreeTSA for comprometido, a âncora perde valor — a fase 2 prevista é somar o OpenTimestamps, ancorado no Bitcoin, que não depende de nenhum operador. Para conferir a âncora de ponta a ponta por fora, o auditor precisa também da trilha, que sai no pacote de verificação (E9).

### E9. Exportação e dados abertos
**Objetivo**: publicar o pacote de verificação.
- O pacote contém: pré-registro, comprovantes de carimbo de tempo, semente, alocações, leituras redigidas, avaliações anonimizadas, trilha de auditoria e script de análise.
- Os dados identificáveis **nunca** entram no pacote; uma checagem automática bloqueia a exportação se detectar um campo do esquema `identity`.

#### P1 — pacote de verificação (v0.22)

**Decisão de LGPD**: o pacote do P1 é **público**, mas **sem primeiro nome e sem o sal do pedido**. Refazer a conferência por fora exige as respostas sobre a pessoa falecida, que são dados sensíveis (Constitution, LGPD); elas saem só com códigos pseudônimos. O compromisso de cada pedido cobre o primeiro nome junto com as respostas, então refazê-lo exigiria publicar o nome e o sal: por isso ele fica **atestado pela plataforma**, e o conferidor diz isso em vez de passar calado. As alternativas descartadas eram o pacote completo só para auditores identificados e as duas camadas (pública sem nome, restrita com nome).

- **Quando**: escrito uma vez, no desmascaramento, na mesma transação, logo depois do relatório. O banco recusa o pacote de estudo não desmascarado e recusa qualquer mudança depois (`V24`, a mesma trava da `V20`). O hash do documento vai à trilha (`VERIFICATION_PACKAGE_EXPORTED`, com a posição da trilha que ele inclui), e esse evento é marco do carimbo de tempo (E8).
- **Onde**: `/estudos/{código}/pacote.json`, com o link e o hash na página do estudo concluído. Antes do desmascaramento, 404.
- **Conteúdo** (formato `crivo-pacote-p1/1`, JSON canônico): o pré-registro congelado com o hash; a semente revelada, o compromisso e a entrada canônica; os campos pontuados; cada pedido dos pares ativos com o estrato e as respostas; cada par ativo com os seus envios — tentativa, lista de médiuns elegíveis, médium, ordem dos nomes e estado; cada leitura usada na conferência, com as respostas, o **sal** e o compromisso; as notas por par e os três p; o relatório com o hash; e a trilha do primeiro evento até o relatório.
- **O sal da leitura entra**: o compromisso da leitura não tem nome, e o sal só prova que as respostas publicadas são as que travaram.
- **O que não entra**: primeiro nome, sal e compromisso do pedido, nome e e-mail de conta, código do consulente, pedidos e leituras de pares desfeitos.
- **Checagem automática**: o pacote só pode ter chaves de uma lista fechada, toda resposta tem de ser opção do questionário, e fora do pré-registro, do relatório, do nome do método e das cargas da trilha toda cadeia de texto tem de ter forma de código, hash, opção, instante ou estrato — um e-mail, por exemplo, não passa. Se algo escapar, o pacote **não é publicado**, a trilha registra `VERIFICATION_PACKAGE_WITHHELD` com o caminho que falhou (nunca o valor), e o desmascaramento segue: o relatório não depende do pacote. O usuário do pesquisador (`AIRLAB_RESEARCHER_USER`) entra na trilha como código de ator e, por isso, não pode ser um e-mail.
- **Conferidor**: `airlab verificar-pacote ARQUIVO` (`structured.VerificationPackageCheck`), sem Spring, sem banco e sem servidor, refaz a cadeia da trilha incluída, confere o pré-registro e a semente contra o que a trilha congelou, refaz o médium e a ordem dos nomes de cada envio a partir da semente e da lista de elegíveis (cujo hash a trilha gravou no envio), o compromisso de cada leitura, a conferência de cada par e as três análises (`structured.StructuredAnalysis`, a mesma função do desmascaramento), e confere o hash do relatório.
- **Limites declarados**: os compromissos dos pedidos não se refazem por fora; o pareamento não se refaz, porque depende das sinalizações de contas ligadas, que não são públicas; e a trilha do pacote vai até o relatório daquele estudo.
- **Desistência depois do desmascaramento**: o pacote já publicado não muda. O termo de participação (desde a `conta-v2`) diz isso antes do aceite.

**Aceite do E9 no P1**: nenhum pacote existe antes do desmascaramento, nem por SQL; o pacote não contém nenhum primeiro nome, nome de conta nem e-mail; o hash está na trilha; `airlab verificar-pacote` aprova o pacote publicado e reprova uma resposta de leitura alterada, um `d` alterado, um evento da trilha alterado e uma lista de elegíveis alterada; a checagem recusa chave fora da lista e resposta fora das opções sem pôr o valor na mensagem.

### E10. Autoatendimento e automação do estudo

**Objetivo**: permitir que um estudo rode de ponta a ponta pela internet — consulentes se
inscrevem, consentem e cadastram o alvo; médiuns se cadastram e recebem o primeiro nome pelo
sistema; a avaliação acontece no site por link de uso único; a análise roda sozinha ao atingir o
N pré-registrado — **sem que a automação enfraqueça nenhuma garantia do protocolo**.

O critério que decide o que entra neste épico é um só: *automatizar o que fecha uma via de
vazamento ou remove um grau de liberdade do pesquisador; não automatizar o que só reduz esforço
à custa de validade.*

#### O que a automação fortalece (entra)

- **Liberação do primeiro nome sem intermediário.** Quanto menos gente vê o dado, menor a
  superfície de vazamento. A entrega pelo sistema é mais cega do que a entrega por uma pessoa.
- **Link de uso único e expirável** para a avaliação, com o token guardado apenas como hash.
- **Pareamento, sorteio e ordem de apresentação** sem intervenção manual — já implementados.
- **Qualificação cega de médiuns**: rodada prévia de leituras avaliadas pelo mesmo mecanismo,
  antes da entrada no estudo. É a triagem que a literatura associa mais fortemente a resultado,
  e é integralmente automatizável.
- **Fechamento automático** quando o N pré-registrado é atingido, com a análise rodando sob a
  política de rótulo confirmatório já existente.

#### O que a automação enfraquece (fica de fora)

- **A sessão não vira formulário.** Substituir o experimentador *proxy* por um campo de texto
  elimina a variável que mais separa os estudos positivos dos nulos na comparação de Silva e
  Moreira-Almeida (2022): dos dois estudos em que o médium não interagiu com ninguém, ambos
  deram nulo. O agendamento, a liberação do nome, o upload do áudio e a transcrição são
  automatizados; a sessão permanece síncrona, com *proxy*. Um braço assíncrono só é admissível
  como variante **declarada no pré-registro**, nunca como padrão. **O P1 (E13) é essa variante**,
  declarada como protocolo próprio, com a ausência do *proxy* escrita entre os limites dele; o
  P2 continua síncrono.
- **Nenhuma avaliação automática de conteúdo**, em nenhuma etapa (P10).

#### Travas obrigatórias

1. **Nenhum resultado parcial antes do desmascaramento.** O sistema mostra *progresso* —
   quantas unidades independentes faltam para o alvo — e nunca taxa de acerto, diferença entre
   alvo e chamariz, ou qualquer função dos escores. A projeção de progresso não tem campo de
   desfecho, por construção, e há teste estrutural que verifica isso. Sem esta trava, a
   automação reintroduz a parada opcional que o P3 existe para impedir.
2. **Papel único por pessoa em cada estudo.** Ninguém é médium e consulente no mesmo estudo, e
   experimentador e redator são pessoas diferentes. A tentativa é recusada no domínio e
   registrada na trilha. Com cadastro aberto, o conluio é o risco de fraude mais sério, e é o
   único que destrói o resultado sem deixar rastro óbvio.
3. **Identidade verificada antes da inscrição valer.** Confirmação de e-mail no mínimo; para
   estudo com fim de publicação, verificação mais forte definida no pré-registro. Contas
   ligadas (mesmo contato, mesmo alvo cadastrado duas vezes) são sinalizadas para revisão
   humana, não bloqueadas em silêncio.
4. **Comitê de ética antes do ar, não depois.** Recrutamento público de pessoas enlutadas, com
   coleta de dado sensível, exige aprovação do CEP/Plataforma Brasil **antes** de a inscrição
   ser aberta. O sistema registra a referência da aprovação no pré-registro.
5. **Um humano alcançável.** Alguém pode se inscrever em luto agudo. A inscrição exibe um canal
   de contato humano e o estudo registra quem é a pessoa responsável; resposta automática não
   substitui isso.
6. **Consentimento sempre na versão corrente.** Uma inscrição feita sob versão antiga do termo
   não é aceita sem novo aceite, e a versão aceita fica gravada com a inscrição.

**Aceite**: um estudo completo roda sem intervenção manual exceto a sessão e a revisão de
sinalizações; em nenhum momento, antes do desmascaramento, qualquer tela ou API expõe função
dos escores; e a tentativa de acumular papéis conflitantes é recusada e auditada.

### E11. Escolha forçada precognitiva (P3)

**Objetivo**: um teste que qualquer pessoa faz pelo navegador, sem intermediário, e cuja lisura ela pode conferir depois por conta própria.

**Apresentação (v0.17)**: listado na página de protocolos do Crivo, com página em `/protocolo/P3` e teste em `/protocolo/P3/rodar`; a PsiGama também o apresenta e aponta para cá (seção 1.1, regra 7).

**Desenho**: a cada tentativa, a pessoa escolhe entre quatro alternativas. O alvo daquela tentativa é derivado de uma semente sorteada no início da sessão e **só é calculado e mostrado depois que a resposta está travada**. Acaso = 25%, declarado.

- A sessão tem número de tentativas **fixo e declarado antes de começar** (padrão: 24). A quantidade não muda no meio.
- **A semente tem duas metades, e nenhum dos dois lados a escolhe sozinho:**

  ```
  semente = SHA-256( metade do servidor || metade do participante )
  ```

  O servidor sorteia a metade dele com `SecureRandom`, guarda cifrada e **publica o hash dela antes de a outra metade existir**. Só então o navegador do participante sorteia a metade dele (16 bytes de `crypto.getRandomValues`) e a envia. Para dirigir o resultado, o servidor precisaria de uma metade que, combinada com a do participante, desse a semente desejada — e ele já estava comprometido antes de ver aquela metade. Isso é um ataque de pré-imagem ao SHA-256.

- **Antes da contribuição do participante a sessão não tem semente nem tentativas.** A regra é imposta no banco, não só no código: um *trigger* recusa qualquer linha de tentativa de sessão sem a metade do participante (migração V16). É essa ordem que sustenta a afirmação do protocolo.
- As duas metades são reveladas no fechamento. Com elas e o algoritmo público (SHA-256 em modo contador, `support.SeedStream`), qualquer pessoa recomputa os alvos de todas as tentativas e confere que eles não foram escolhidos depois da resposta.
- O alvo de cada tentativa nunca sai do servidor antes de a resposta estar travada — não há campo, API ou projeção que o exponha, e há teste que tenta obtê-lo.
- **Nenhum resultado parcial dentro da sessão** (E10, trava 1): durante a sessão a pessoa vê quantas tentativas faltam, nunca quantas acertou. O desfecho aparece de uma vez, no fim.
- Sessão abandonada é registrada como abandonada e **entra no denominador** da análise do pool conforme a regra pré-registrada. Desistir de uma sessão ruim não melhora o número agregado.

**Por que este teste, apesar das evidências**: as replicações grandes dos achados de Bem deram nulo (Ritchie, Wiseman & French, 2012; Galak et al., 2012), e a página da plataforma diz isso. O valor dele aqui não é a expectativa de efeito: é ser o único protocolo em que o vazamento sensorial é **impossível por construção** — no instante da resposta o alvo ainda não foi calculado — e em que não existe julgamento subjetivo nenhum. Serve de demonstração limpa do método de verificação.

**Pool público é exploratório.** O autoatendimento anônimo não tem controle de quem testa, quantas vezes nem em que condições; os dados públicos são abertos e rotulados **exploratórios**. Um resultado confirmatório exige estudo pré-registrado com participantes identificados, N fixo e pool declarado antes da coleta — a política de rótulo já existente é quem decide.

**Verificação na mão de quem testou**: a página de resultado traz a semente num campo editável e um botão que, **no navegador da pessoa e sem falar com o servidor**, faz duas conferências. Primeira, refaz o compromisso — `SHA-256(semente | protocolo | tentativas | alternativas | banco)` — e compara com o que foi publicado no início; é isso que liga a semente revelada no fim àquela que já estava valendo antes da primeira resposta. Segunda, recomputa os alvos e compara com os gravados. Sem a primeira conferência a segunda não vale nada: qualquer semente gera "os alvos certos" se tiver sido escolhida depois das respostas. O código do verificador é servido como arquivo legível (`/js/verificador-alvos.js`). Isso tem alcance limitado e a página diz qual: prova que a semente revelada gera os alvos gravados, não que a aplicação é honesta — para isso a regra precisa ser reimplementada por fora, e é por isso que ela cabe em oito linhas e tem **vetor de teste público** (semente de 64 zeros, 12 tentativas, 4 alternativas → 1, 1, 3, 3, 2, 3, 2, 1, 1, 4, 1, 3), fixado em teste tanto na implementação em Java quanto no verificador.

**A conferência de ordem é automática**: o navegador do participante guarda o compromisso no momento em que recebe a tela da entropia — antes de sortear a metade dele — e o verificador compara, no fim, com o que a página mostra. Quem quiser a prova à mão ainda pode copiar o valor, que aparece em todas as telas de tentativa. Quando a sessão começou noutro navegador, em janela privativa ou com armazenamento bloqueado, essa conferência não existe, e a página diz isso em vez de omitir.

**Aceite**: nenhuma tentativa existe antes de o participante contribuir com a metade dele, recusado também pelo banco; as duas metades reveladas reproduzem a semente efetiva, e ela reproduz exatamente os alvos gravados; a metade do servidor confere com o compromisso publicado antes de a outra existir; o alvo não é obtível por nenhuma via antes da resposta travada; durante a sessão nenhuma tela expõe função dos escores; duas sessões com a mesma semente e as mesmas respostas dão o mesmo resultado; e o verificador do navegador acusa divergência quando a semente é alterada num único dígito.

### E12. Visão remota com quatro alvos (P4)

**Objetivo**: um teste de resposta livre, também integralmente automatizado, mais próximo do que a literatura chama de *free-response*.

**Apresentação (v0.17)**: como o P3, listado na página de protocolos do Crivo, com página em `/protocolo/P4` e teste em `/protocolo/P4/rodar` (seção 1.1, regra 7).

**Desenho**: o sistema sorteia um alvo entre as imagens de um banco versionado. A pessoa escreve a impressão dela **antes** de ver qualquer candidata. Só então o sistema apresenta quatro imagens — o alvo e três chamarizes, sorteados da mesma forma — e a pessoa as ordena da mais parecida com a impressão à menos parecida. Acerto = alvo em primeiro lugar; acaso = 25%, declarado.

- A impressão escrita é **travada antes** de as candidatas aparecerem, e a trilha registra o hash dela. Sem isso, a pessoa pode reescrever a impressão depois de ver as imagens, que é a objeção padrão ao autojulgamento.
- O alvo e os três chamarizes saem da mesma semente comprometida, pelo mesmo mecanismo do P3.
- O banco de imagens é **versionado**, e a versão usada fica gravada na sessão: trocar as imagens depois não muda o que foi analisado.
- Vale a mesma regra de resultado parcial, de sessão abandonada e de pool exploratório do E11.

**Limite declarado**: quem julga a correspondência é a própria pessoa, e isso reintroduz a crítica de "como se pontua" que o P3 não tem. É o preço do formato de resposta livre, e fica escrito na página do protocolo. A estatística é de ordenação, com acaso exato.

**Aceite**: a impressão não pode ser alterada depois de as candidatas aparecerem; a semente revelada reproduz alvo e chamarizes; e a versão do banco de imagens usada na sessão é a que o pacote de verificação exporta.

### E13. Leitura estruturada automatizada (P1)

**Objetivo**: um protocolo AIR que roda inteiro pelo site, do cadastro ao relatório, sem ninguém da equipe no circuito — e sem que ninguém, nem o consulente, julgue se uma afirmação "encaixa".

**Ideia central**: o consulente e o médium respondem ao **mesmo questionário fechado** sobre o falecido. O consulente responde porque conheceu a pessoa; o médium responde conhecendo só o primeiro nome. O sistema confere campo a campo. Não há redator, porque não há texto livre para dividir em itens, e não há avaliação subjetiva, porque a comparação é de igualdade entre opções. Isso elimina a principal fonte de viés da avaliação pelo consulente — a tendência de encontrar sentido em afirmações genéricas — e dispensa a IA, que o P10 proíbe na pontuação.

#### Perfis e contas

| Perfil | Como se obtém | O que faz |
|---|---|---|
| **Consulente** | Escolhido ao criar a conta | Faz o pedido de consulta: primeiro nome do falecido e respostas ao questionário |
| **Médium** | Escolhido ao criar a conta | Recebe pedidos com o primeiro nome e responde ao questionário |
| **Administrador** | Configuração do servidor (`AIRLAB_RESEARCHER_USER`, `AIRLAB_RESEARCHER_PASSWORD_HASH`) | Conduz o estudo, recebe os avisos, revisa sinalizações; nunca vê respostas, nomes nem notas antes do desmascaramento |

- **Criar conta**: nome, e-mail, senha (mínimo de 10 caracteres), perfil, declaração de maioridade e aceite do termo na versão corrente (E10, trava 6). A conta só vale depois da **confirmação do e-mail** por link de uso único, expirável em 48 horas e guardado apenas como hash (E10, trava 3).
- **Só maiores de 18 anos** (v0.26). Consulentes e médiuns declaram ter 18 anos ou mais numa caixa própria, obrigatória e separada do aceite do termo; sem ela, o domínio recusa a conta, com a mensagem "Para participar é preciso ter 18 anos ou mais.", e nada é gravado. A plataforma **não pede data de nascimento**: a idade exata não serve à pesquisa, e a declaração basta para a regra (minimização, Constitution P6; LGPD, art. 6º, III). A trilha registra, no evento `ACCOUNT_CREATED`, só `adultDeclared: true`, ao lado da versão do termo. A declaração não é verificada contra documento, e isso fica dito entre os limites da ética (artigo técnico, seção 7). Contas criadas antes da v0.26 só existem em dados fictícios, porque em produção a inscrição espera o CEP (v0.22.1).
- **Perfil fixo**: escolhido uma vez, nunca muda — imposto por *trigger* no banco, não só no código. É a forma mais simples da trava 2 do E10: com um mesmo e-mail ninguém é médium e consulente, em estudo nenhum.
- **Uma conta por e-mail**, comparada pelo índice cego (HMAC) do endereço, sem guardar o e-mail em claro. Contas ligadas por outros sinais são **sinalizadas** ao administrador, não bloqueadas em silêncio.
- **Nenhum caminho do site cria administrador.** O formulário aceita só consulente ou médium.
- **Dados da conta no esquema `identity`**, cifrados; a pesquisa trabalha com o código pseudônimo (`CON-…`, `MED-…`). Só o módulo `participant` lê a conta.
- **Desistência a qualquer momento**, sem justificativa: os dados identificáveis são apagados e o código pseudônimo permanece, marcado como inativo (P6).
- **Contato humano**: a página de inscrição mostra o canal da pessoa responsável (E10, trava 5).
- **Inscrição fechada até o CEP**: no perfil de produção, a criação de conta só abre com envio de e-mail configurado (trava 3), canal de contato humano (trava 5) e a referência da aprovação do comitê de ética (`AIRLAB_CEP_REFERENCE`, trava 4: aprovação antes de a inscrição abrir). Fechada só por falta da aprovação, a página diz que as inscrições abrem depois dela; fechada por outro motivo, diz apenas que estão fechadas. Os pedidos de consulta seguem a inscrição.

#### Fluxo

```
[1] Consulente cria a conta e confirma o e-mail
        │
[2] Consulente faz o pedido: primeiro nome do falecido + questionário
    → respostas travadas; a trilha recebe SHA-256(respostas canônicas || sal)   ◄── COMPROMISSO
        │
[3] Sistema pareia pedidos travados (mesmo gênero, faixa etária e período) e sorteia o médium do par
        │
[4] Médium recebe o pedido: os dois primeiros nomes do par, em ordem sorteada, e o questionário em branco
        │
[5] Médium responde a cada campo, ou marca "não recebi"; ao enviar, a leitura trava com hash na trilha
        │
[6] Atingido o N pré-registrado: desmascaramento automático, revelação dos sais e da semente
        │
[7] Conferência automática, análise pré-registrada e relatório
        │
[8] Devolutiva: cada consulente vê a leitura do seu alvo e a nota; o médium vê as notas das próprias leituras
```

- **O compromisso leva sal.** Respostas fechadas têm pouca entropia: o hash puro de meia dúzia de opções se quebra por força bruta, e a trilha é pública. O sal (32 bytes de `SecureRandom`) fica em custódia cifrada e é revelado no desmascaramento, junto com as respostas.
- **Sem pedido travado, nada é liberado ao médium.** Imposto no banco: não existe linha de pedido ao médium cujo pedido de consulta não esteja travado.
- **Prazo do médium** pré-registrado (padrão: 14 dias). Pedido vencido é registrado como vencido e reenviado a outro médium sorteado da mesma semente; o vencimento fica na trilha.

#### Conferência automática

Para cada par (A, B), com a leitura `R_A` (feita com o nome de A) e `R_B`:

- **Campos comparáveis** do par: os campos pontuáveis que **os dois** consulentes responderam (nenhum dos dois marcou "não sei"). O conjunto é o mesmo para alvo e chamariz, para que nenhum dos dois tenha mais chances de acerto.
- `s(R, X)` = número de campos comparáveis em que a resposta da leitura `R` é igual à do consulente `X`. "Não recebi" nunca acerta.
- **Diferença do par**: `d = [s(R_A, A) − s(R_A, B)] + [s(R_B, B) − s(R_B, A)]`.
- **Análise primária**: Wilcoxon exato unilateral sobre os `d` dos pares (a implementação de `analysis` já existe). **Secundárias**: binomial exato do sinal de `d` contra 0,5 e permutação das leituras entre todos os alvos do estudo, dentro dos estratos do pareamento.
- **Campos fora da nota**: gênero (o primeiro nome o entrega) e os critérios de pareamento — faixa etária e período do falecimento —, que são iguais dentro do par por construção e não discriminam nada. Eles são coletados, servem ao pareamento e aparecem na devolutiva.
- **Por que o chamariz é indispensável**: o número de acertos, sozinho, não diz nada. "Morreu do coração" ou "era idoso" acertam com qualquer pessoa porque são frequentes. Comparando a mesma leitura com o alvo e com o chamariz de perfil semelhante, a frequência de cada resposta se cancela, e o que sobra é o que se ajusta ao alvo **mais** do que ao chamariz.
- **Acaso declarado**: sob a hipótese nula, `d` é simétrico em torno de zero; entre os pares com `d ≠ 0`, a chance de `d > 0` é 50%.
- A regra é determinística, pública e reproduzível pelo pacote de verificação. **Nenhum texto livre entra na nota** (P10).

#### Telas por perfil

| Perfil | Tela | Conteúdo |
|---|---|---|
| Público | Página do protocolo | O que é o P1 e **o que ele não é** — não é carta nem mensagem da pessoa falecida, nem resposta a perguntas do consulente, só um experimento de confirmação (ou não) da recepção anômala de informação —, como participar, o que o resultado não sustenta. O mesmo esclarecimento está na página inicial; o questionário em vigor, campo a campo, com as opções e o que entra na nota (`/protocolo/P1`) |
| Público | Criar conta | Nome, e-mail, senha, perfil (consulente ou médium), termo, contato humano |
| Público | Confirmar e-mail | Resultado do link de confirmação |
| Consulente | Painel | Situação do pedido: rascunho, travado, aguardando par, enviado ao médium, leitura recebida, aguardando o fechamento do estudo, devolutiva |
| Consulente | Novo pedido | Primeiro nome do falecido e questionário; revisão e confirmação antes de travar |
| Consulente | Devolutiva | Depois do desmascaramento: as respostas do médium ao lado das suas e a nota, com linguagem cuidadosa |
| Médium | Painel | Pedidos recebidos e prazos; leituras enviadas, sem nota até o desmascaramento |
| Médium | Leitura | Os dois primeiros nomes do par e o questionário; revisão antes de travar |
| Consulente e médium | Minha conta | Dados, termo aceito, excluir conta |
| Administrador | Painel | Contas por perfil e situação; pedidos e leituras por etapa; progresso até o N — **contagens, nunca escores** |
| Administrador | Avisos | Conclusão de cada etapa (abaixo) |
| Administrador | Sinalizações | Contas ligadas para revisão humana |
| Administrador | Estudo | Pré-registro, questionário e congelamento; relatório depois do desmascaramento |

#### Avisos ao administrador

Um aviso por etapa concluída: conta confirmada, pedido travado, par formado e enviado ao médium, leitura recebida, prazo vencido e reenvio, sinalização de contas ligadas, N atingido e estudo desmascarado, relatório pronto. Aparecem no painel e, quando há envio de e-mail configurado (`AIRLAB_ADMIN_EMAIL`), também por e-mail. **Um aviso contém só tipo, código pseudônimo e hora** — nunca nome, resposta ou nota, porque e-mail e painel não são canais cegos.

O médium recebe e-mail quando chega um pedido; o consulente, quando a devolutiva está disponível.

#### Matriz de visibilidade do P1

| Informação ↓ / Papel → | Administrador | Médium | Consulente |
|---|---|---|---|
| Identidade das contas | ✓ | só a própria | só a própria |
| Respostas do consulente ao questionário | ✗ até o desmascaramento | ✗ | só as próprias |
| Primeiro nome do falecido | ✗ | ✓ (só dos pedidos recebidos) | só o próprio |
| Ligação pedido → médium | ✗ até o desmascaramento | só os próprios pedidos | ✗ até o desmascaramento |
| Leitura do médium | ✗ até o desmascaramento | só as próprias | ✗ até o desmascaramento |
| Notas e diferenças | ✗ até o desmascaramento | ✗ até o desmascaramento | ✗ até o desmascaramento |
| Sais e semente | ✗ até a revelação | ✗ | ✗ |

**As notas não existem antes do desmascaramento**: a conferência só roda depois dele. Isso é mais forte do que escondê-las (E10, trava 1).

#### Travas próprias do P1

Além das seis travas do E10:

1. Questionário versionado, com a lista de campos fora da nota, congelado no pré-registro.
2. Respostas do consulente travadas, com compromisso salgado na trilha, **antes** da liberação do nome ao médium — imposto no banco.
3. Leitura do médium travada, com hash na trilha, antes do desmascaramento.
4. Perfil fixo imposto no banco; nenhum caminho do site cria administrador.
5. Conferência só depois do desmascaramento, por regra determinística e publicada.

#### O que o resultado do P1 não sustenta

- Não distingue sobrevivência de telepatia com os vivos: as respostas existem na memória do consulente e no banco.
- Não tem experimentador *proxy*. Nos dois estudos da comparação de Silva e Moreira-Almeida (2022) em que o médium não interagiu com ninguém, o resultado foi nulo; um nulo no P1 não refuta o P2.
- O questionário fechado só captura o que cabe nos campos. As afirmações livres e específicas, que costumam ser as mais marcantes de uma leitura, ficam fora da nota.
- O primeiro nome carrega informação (gênero, às vezes geração e região). O pareamento e os campos fora da nota reduzem esse vazamento, mas não o eliminam.
- Com inscrição aberta, o conluio entre um médium e um consulente que se conhecem e usam e-mails diferentes não é detectável com certeza. O sorteio do médium e a sinalização de contas ligadas reduzem o risco.

#### Entrega em fases

| Fase | Conteúdo | Estado |
|---|---|---|
| A | Catálogo em duas seções, páginas do P1, contas com perfil fixo, confirmação por e-mail, painéis por perfil, avisos de conta ao administrador | Implementada na SPEC 0.9 |
| B | Questionário versionado e pedido de consulta com compromisso salgado | Implementada na SPEC 0.10 |
| C | Pareamento, sorteio do médium, pedido ao médium, leitura, prazos e reenvio | Implementada na SPEC 0.11 |
| D | Desmascaramento automático no N, conferência, análise, relatório e devolutiva | Implementada na SPEC 0.12 |
| E | Sinalização de contas ligadas, teto de pares por médium, página pública dos estudos | Implementada na SPEC 0.13 |

#### Fase B — decisões

- **O rascunho não é gravado.** Enquanto o consulente preenche e revisa, as respostas ficam só na sessão do navegador. O banco recebe o pedido uma vez, já travado. É minimização (P6) e fecha a pergunta "o pedido mudou entre o rascunho e a trava?".
- **Compromisso**: `SHA-256(JSON canônico || "|" || sal em hexadecimal)`, onde o JSON canônico (`support.CanonicalJson`) tem o protocolo, a versão do questionário, o primeiro nome e as respostas, por chave de campo. A regra está em `participant.ConsultationCommitment`, pública, para que o pacote de verificação a reimplemente por fora.
- **A trilha recebe o código do pedido, o código do consulente, a versão do questionário e o compromisso — nada mais.** Nem os campos de pareamento entram nela: só o compromisso precisa ser público antes da leitura.
- **Onde fica cada coisa**: primeiro nome, respostas e sal, cifrados, em `identity.consultation_detail`; em `research.consultation_request`, o código, o consulente, a versão, os três atributos de pareamento e o compromisso. O banco recusa alterar o pedido travado e recusa `DELETE`; a única alteração admitida no detalhe é o apagamento, na desistência.
- **Um pedido ativo por consulente.** Vários pedidos da mesma pessoa criariam dependência entre unidades de análise. Imposto por índice único parcial.
- **Campos de pareamento são obrigatórios**: gênero, faixa etária e período do falecimento não aceitam "não sei", porque sem eles não há par. As faixas são as mesmas do P2, para que o pareamento seja o mesmo código.
- **Pedido antes do congelamento.** Como no P2, em que os alvos são cadastrados antes do sorteio, o pedido vem antes do congelamento do estudo. A garantia é outra: na fase C, só entra no estudo o pedido cuja versão de questionário é a declarada no pré-registro congelado.
- **Comitê de ética (E10, trava 4)**: em produção, o formulário de pedido fica fechado até que `AIRLAB_CEP_REFERENCE` esteja definida. No perfil local ele abre, porque ali os dados são fictícios.
- **Desistência**: excluir a conta retira o pedido ativo — o detalhe cifrado é apagado, o pedido fica como retirado, e a trilha registra.

**Aceite**: nenhuma conta muda de perfil, nem por SQL direto; nenhum caminho do site cria administrador; nenhum nome é liberado a médium sem pedido travado com compromisso na trilha; nenhuma nota existe antes do desmascaramento; e a conferência refeita a partir do pacote de verificação dá os mesmos `d` de cada par.

#### Fase C — decisões

- **Estudo P1 numa tela, sem rascunho gravado.** O administrador preenche o pré-registro — título, número de pares (N), prazo do médium, efeito esperado, hipóteses e critérios de inclusão de médiuns —, revê e congela num passo só. Como no pedido do consulente, o banco recebe o estudo já congelado. O documento congelado leva um **anexo canônico** com o questionário inteiro (versão, campos, opções e campos fora da nota), o critério de pareamento, o prazo e o N (trava 1 do P1). Um estudo P1 aberto por vez.
- **Questionário em rascunho não congela em produção.** Uma versão terminada em `-rascunho` só é aceita no perfil local, onde os dados são fictícios. Em produção, o estudo exige a versão revisada pelo pesquisador (questão em aberto 9).
- **Compromisso sobre a semente.** No P2 o sorteio acontece de uma vez e o compromisso cobre o plano inteiro. No P1 os pares se formam ao longo do estudo, então o compromisso, publicado no documento congelado, é sobre a semente: `SHA-256(semente || "|" || entrada canônica do estudo)`. Cada sorteio é função pública da semente e de entradas registradas: o médium do par é uma escolha de `SeedStream(semente, "p4-medium:" + par + ":" + tentativa)` sobre os médiuns elegíveis em ordem canônica, e a ordem dos dois nomes é um embaralhamento com `"p4-name-order:" + par + ":" + tentativa`. A semente fica em custódia cifrada e é revelada no desmascaramento (fase D).
- **Ciclo automático.** Um ciclo do servidor — a cada minuto, por padrão, e logo depois do congelamento — faz, nesta ordem: desfaz pares com pedido retirado; vence os pedidos ao médium fora do prazo; cancela os pedidos de médium que excluiu a conta; forma pares; envia ao médium os pares que estão sem médium. Rodar duas vezes seguidas não muda nada.
- **Pareamento.** Entram os pedidos travados na versão do questionário congelada. Mesmo estrato (gênero, faixa etária e período do falecimento); dentro do estrato, por ordem de travamento (e pelo código, no empate), dois a dois, até N pares ativos. Um pedido sem par espera, e o painel do consulente diz isso.
- **Um médium por par, que faz as duas leituras**, uma com cada nome, como no P2. Assim a tendência própria do médium — responder "morreu do coração" a todo pedido, por exemplo — entra nos dois lados de `d` e se cancela.
- **Médiuns elegíveis**: contas de médium confirmadas e ativas no momento do sorteio, menos quem já recebeu este par. A lista vai ao banco em forma canônica e o hash dela, à trilha, para que o sorteio seja refeito depois. Sem médium elegível, o par espera.
- **O médium responde só aos campos da nota.** Gênero, faixa etária e período não contam e são iguais (ou dedutíveis do nome) dentro do par; perguntá-los seria trabalho sem uso. Cada campo exige uma opção ou "não recebi" — nada fica em branco.
- **Leitura travada com compromisso salgado**, pela mesma razão do pedido: as respostas fechadas do médium também têm pouca entropia, e a trilha é pública. Respostas e sal ficam cifrados; a regra está em `structured.ReadingCommitment`. Revisão antes de travar, com o rascunho só na sessão.
- **A trilha não liga médium a par antes do desmascaramento.** Nem o envio, nem a liberação dos nomes, nem a leitura travada levam o código do médium à trilha (matriz do P1: ligação pedido → médium ✗ para o administrador). O vínculo fica no banco e entra no pacote de verificação depois do desmascaramento.
- **Nomes só para quem recebeu.** Só o módulo `participant` lê o primeiro nome, e só o entrega a um médium que tenha liberação registrada (`identity.first_name_release`) e não revogada para aquele pedido. Vencimento, cancelamento e retirada revogam a liberação.
- **Prazo**: o declarado no pré-registro (padrão: 14 dias). Vencido, o pedido ao médium é encerrado como vencido, as leituras dele que já estavam travadas ficam registradas mas **fora da análise**, e o par vai a outro médium sorteado (tentativa seguinte). A mesma regra vale para o médium que exclui a conta. As duas regras de exclusão entram no pré-registro.
- **Retirada do consulente**: o par se desfaz, o pedido ao médium é cancelado, e o outro pedido volta a aguardar par. Par desfeito não entra na análise.
- **E-mail.** O médium recebe o aviso de pedido novo — sem nome, com o prazo e o endereço do painel (`AIRLAB_BASE_URL`). O administrador recebe "par enviado ao médium", "leitura recebida" e "prazo vencido", com o código do par.

**Aceite da fase C**: nenhum nome chega a médium sem liberação registrada sobre pedido travado; o médium vê só os nomes dos próprios pedidos; a trilha não tem código de médium em envio, liberação ou leitura; com a semente revelada, o médium e a ordem dos nomes de cada envio se refazem a partir das entradas registradas, em qualquer ordem de entrada; leitura travada não muda nem some, nem por SQL.

#### Fase D — decisões

- **Desmascaramento automático, no N.** Quando os N pares ativos têm as duas leituras travadas, o próprio ciclo encerra a coleta e desmascara, numa transação só: o estudo passa a coleta encerrada; a semente é revelada; o estudo passa a desmascarado; os pedidos dos pares passam a "estudo concluído"; a conferência, a análise e o relatório rodam; e o estudo passa a relatado. Ninguém escolhe o momento — nem o administrador. O desmascaramento do P1 mora no módulo `structured`, onde estão as entradas dele; o módulo `unblinding` continua reservado ao P2.
- **Nenhuma nota antes, imposto no banco.** As tabelas de notas e de relatório recusam linha de estudo que não esteja desmascarado. As respostas do consulente só saem do módulo `participant` para pedido em "estudo concluído".
- **Integridade conferida antes de pontuar.** Com os sais revelados, o sistema recalcula o compromisso de cada pedido e de cada leitura, e o relatório diz quantos conferiram.
- **Conferência**: exatamente a regra da seção "Conferência automática", sobre os pares ativos e a leitura do envio concluído. Pares desfeitos e leituras de envios vencidos ou cancelados ficam fora, como o pré-registro declara.
- **Análise.** Primária: Wilcoxon exato unilateral sobre os `d` (exato até 20 pares com `d` diferente de zero; acima disso, aproximação normal, e o relatório diz qual foi usado). Secundárias: binomial exato do sinal de `d`; permutação das leituras entre os alvos, **dentro dos estratos** do pareamento, sobre a matriz consulente × leitura em que cada célula conta as coincidências nos campos que o consulente respondeu. A permutação é exata enquanto os arranjos cabem na enumeração; acima disso, usa 100 mil arranjos sorteados da semente revelada, e qualquer pessoa os refaz.
- **Relatório**: documento canônico com hash na trilha. Traz o hash do pré-registro, o compromisso e a semente revelada, as três análises, o poder declarado, a integridade, as exclusões, a tabela por par — com o médium, agora revelado — e por médium, e o que o resultado não sustenta. A linguagem é neutra: "p abaixo de 0,05" é uma afirmação sobre o teste, não sobre o fenômeno (P3).
- **Devolutiva do consulente**: as próprias respostas ao lado das do médium para o seu alvo, campo a campo, com os campos fora da comparação marcados. Mostra também quantos campos coincidiram com as suas respostas e, para comparação, quantos coincidiram com as do outro consulente do par — só a contagem, nunca as respostas dele —, além do resultado do estudo inteiro. O texto diz que um resultado individual não fala da pessoa falecida nem do médium: o que o estudo avalia é o conjunto.
- **Vazamento residual declarado**: o campo marcado como fora da comparação revela ao consulente que o outro respondeu "não sei" ali, e a contagem contra o chamariz revela quantas respostas do outro coincidem com a leitura. Nenhuma resposta do outro consulente é mostrada.
- **Devolutiva do médium**: para cada leitura, as coincidências com o alvo e com o chamariz e o `d` do par — nunca as respostas dos consulentes.
- **Administrador**: o relatório inteiro. Depois dele, o estudo deixa de estar aberto e um novo estudo P1 pode ser criado; os pedidos que esperavam par continuam travados e entram no próximo.
- **Avisos e e-mail**: "estudo desmascarado" e "relatório pronto" para o administrador, com o código do estudo. Os consulentes e médiuns do estudo recebem e-mail dizendo que a devolutiva está disponível, sem nota nenhuma na mensagem.
- **O que fica para o E9**: o pacote de verificação público, com respostas, sais, envios e listas de elegíveis, dependia de uma decisão de LGPD sobre publicar respostas sobre pessoas falecidas — decidida na v0.22 (E9): público, sem primeiro nome e sem o sal do pedido.

**Aceite da fase D**: nenhuma nota nem relatório antes do desmascaramento, nem por SQL; o desmascaramento acontece sozinho no N; a conferência refeita a partir das respostas reveladas dá os mesmos `d`; o relatório tem hash na trilha.

#### Fase E — pendências: decisões

- **Sinalização de contas ligadas.** Dois sinais, calculados na confirmação do e-mail e guardados só como índice cego (HMAC), como o próprio e-mail. O **e-mail canônico**: minúsculas, sem o sufixo "+…" e, no Gmail, sem os pontos — `jo.ao+x@gmail.com` e `joao@gmail.com` são a mesma caixa. O **nome completo normalizado**: minúsculas, sem acentos, com espaços simples, e só com duas palavras ou mais — "Maria", sozinho, coincide com gente demais para significar alguma coisa. Quando uma conta confirmada compartilha um sinal com outra conta ativa, nasce uma sinalização para revisão humana, com os dois códigos e o tipo de sinal, e o administrador recebe o aviso "contas ligadas sinalizadas". Nenhum IP, aparelho ou outro rastro é coletado (P6).
- **Sinalizar não é bloquear.** Nenhuma conta é recusada nem suspensa. Enquanto a sinalização está pendente, ou depois que o administrador confirmar que as contas são da mesma pessoa, o sistema só evita o encontro: o médium sinalizado não é sorteado para o par de um consulente sinalizado com ele, e dois consulentes sinalizados entre si não formam par. Se o administrador decidir que as contas não têm relação, a restrição cai. A decisão é registrada uma vez e não muda.
- **Trilha**: a sinalização e a revisão entram com o código da sinalização, o tipo de sinal e a decisão — sem os códigos das contas, porque a trilha é pública e uma suspeita não é um fato.
- **A desistência apaga os sinais** da conta, como o resto dos dados identificáveis. A sinalização fica, só com os códigos pseudônimos.
- **Limite declarado**: os sinais pegam o caso descuidado — o mesmo nome, um apelido do mesmo e-mail. Quem se cadastra com nome e e-mail diferentes não é detectado; isso já está entre o que o P1 não sustenta.
- **Teto de pares por médium**, declarado no pré-registro (padrão: 10, ou N se N for menor) e no anexo canônico. O médium que já tem o teto de pares no estudo, somando pedidos abertos e concluídos, sai da lista de elegíveis. A lista registrada em cada envio já é a filtrada, e o sorteio continua reproduzível. Com poucos médiuns, os pares esperam, e o painel do administrador mostra quantos (questão 12).
- **Página pública dos estudos** (`/estudos`): lista os estudos P1 e, para cada um, o documento do pré-registro com o hash e o compromisso da semente. Enquanto o estudo coleta, só contagens; depois do desmascaramento, o relatório inteiro, com o hash que está na trilha. Só o que é público por construção (P2, P7): códigos pseudônimos, contagens, notas por par e a semente revelada — nunca nome, resposta ou leitura.
- **Vazamento residual declarado**: quem conhece o código do próprio pedido acha o par na trilha e, no relatório público, as quatro contagens do par. São contagens, nunca respostas.

**Aceite da fase E**: nenhuma sinalização bloqueia conta; médium e consulente sinalizados nunca se encontram num par enquanto a sinalização não for descartada; nenhum médium passa do teto; a página pública não mostra, antes do desmascaramento, nada além do pré-registro e de contagens.

#### Anexo — questionário, versão `v2-rascunho` (vigente desde a v0.25)

Rascunho para revisão de pesquisadores da área (questão 9); o que vale é a versão congelada no pré-registro. Implementado em `catalog.Questionnaires` e publicado, campo a campo, na página do P1 (`/protocolo/P1`). Todo campo pontuável tem também "não sei" (consulente) e "não recebi" (médium); os campos de pareamento são obrigatórios para o consulente e não mudaram em relação à `v1-rascunho`.

**Critérios de escolha dos campos** (questão 9). O sinal que um campo pode render é proporcional à chance de dois falecidos do mesmo estrato terem respostas diferentes, 1 − Σp², em que p são as frequências das opções: se a leitura acerta o alvo numa fração θ dos campos além do chute, a diferença esperada entre coincidir com o alvo e coincidir com o chamariz, num campo comparável, é (1 − taxa de "não recebi") · θ · (1 − Σp²). Daí os critérios:

1. **Um fato verificável por campo**, que o consulente sabe responder: pouco "não sei".
2. **Opções mutuamente exclusivas e exaustivas**, com "outro" onde for preciso.
3. **Nenhuma opção dominante**: campos de uma opção quase universal rendem pouco sinal e alongam o formulário.
4. **Nada que o primeiro nome ou o estrato entreguem** na nota: sem região, sem data, sem gênero.
5. **Nada que identifique a pessoa sozinho**: sem cidade, profissão específica, nome de instituição ou data, porque as respostas vão ao pacote de verificação público (E9).
6. **Os temas que a literatura pergunta ao médium**: aparência, personalidade, passatempos e causa da morte, como na seção de perguntas de Beischel et al. (2015), além de família e trajetória de vida.
7. **Linguagem cuidadosa com o luto**: nenhuma opção obriga a nomear uma morte violenta, que cabe em "outra causa externa".
8. **Tamanho**: mais campos aumentam o poder (seção 9, questão 1), mas alongam o preenchimento para quem está de luto. Vinte campos na nota levam poucos minutos.

| Grupo | Campo | Opções | Na nota? |
|---|---|---|---|
| Pareamento | Gênero | feminino, masculino, outro | não (o nome entrega; pareamento) |
| Pareamento | Idade ao falecer | até 17, 18–39, 40–59, 60–79, 80 ou mais | não (pareamento) |
| Pareamento | Há quanto tempo faleceu | menos de 1 ano, 1–5 anos, 5–15 anos, mais de 15 anos | não (pareamento) |
| Morte | Como foi a morte | súbita, sem aviso; depois de dias ou semanas de doença; depois de meses ou anos de doença | sim |
| Morte | Causa principal | coração ou circulação, inclusive AVC; câncer; infecção, inclusive covid-19 e pneumonia; outra doença respiratória; outra doença; acidente; outra causa externa | sim |
| Morte | Onde faleceu | hospital, em casa, na rua ou no trânsito, outro lugar | sim |
| Família | Estado civil na época | solteiro, casado ou em união, viúvo, separado | sim |
| Família | Quantos filhos teve | nenhum, um, dois, três, quatro ou mais | sim |
| Família | Lugar entre os irmãos | filho único, o mais velho, do meio, o caçula | sim |
| Família | Parentesco de quem fez o pedido | filho, cônjuge ou companheiro, pai ou mãe, irmão, neto, outro parente, amigo | sim |
| Trajetória | Até onde estudou | não completou o fundamental, fundamental, médio, superior | sim |
| Trajetória | Ocupação principal | campo; indústria, construção ou ofício manual; comércio ou vendas; transporte; escritório ou serviços; saúde; educação; segurança, polícia ou forças armadas; cuidou da casa e da família; outra | sim |
| Trajetória | Onde viveu a maior parte da vida | cidade grande, cidade média ou pequena, zona rural | sim |
| Personalidade | Temperamento | mais expansivo, mais reservado | sim |
| Personalidade | Traço mais marcante do jeito de ser | bem-humorado, carinhoso, sério ou exigente, determinado ou teimoso, calmo, preocupado ou ansioso | sim |
| Aparência | Estatura, comparada à de pessoas do mesmo gênero e geração | abaixo da média, na média, acima da média | sim |
| Aparência | Porte físico na vida adulta | magro, médio, robusto ou acima do peso | sim |
| Aparência | Cabelo, como a pessoa é mais lembrada | escuro; claro, loiro ou ruivo; grisalho ou branco; calvo | sim |
| Aparência | Usava óculos | sim, não | sim |
| Crenças e hábitos | Religião que seguia | católica, evangélica, espírita, umbanda ou candomblé ou outra de matriz africana, outra, nenhuma | sim |
| Crenças e hábitos | Passatempo mais marcante | música, trabalhos manuais, futebol ou outro esporte, leitura, cozinha, plantas, pesca ou vida ao ar livre, jogos de cartas ou de tabuleiro, outro | sim |
| Crenças e hábitos | Fumo | nunca fumou, fumou e parou, fumava na época em que faleceu | sim |
| Crenças e hábitos | Animal de estimação mais marcante | cachorro, gato, pássaro, outro, nenhum | sim |

**O que mudou em relação à `v1-rascunho`**: entram lugar entre os irmãos, parentesco de quem fez o pedido, escolaridade, traço mais marcante, porte físico, cabelo e animal de estimação; "tinha filhos" (sim ou não, com uma opção quase universal na faixa de 60 a 79 anos) vira o número de filhos; "fumava" ganha "fumou e parou"; "como foi a morte" separa dias ou semanas de meses ou anos; a causa ganha infecção, inclusive covid-19, e troca "violência" por "outra causa externa"; "religiosidade" (praticante ou não) dá lugar à religião, porque as duas juntas repetiriam a mesma informação. Campos correlacionados não invalidam a análise primária, porque a simetria de `d` sob a hipótese nula não depende de independência entre campos; só reduzem a informação que cada campo novo acrescenta.

**O que ainda falta**: a revisão por pesquisadores da área, o teste com consulentes reais de compreensão e de tempo de preenchimento (entrevista cognitiva) e as frequências das opções por estrato, que hoje são suposições (seção 9, questão 1).

#### Anexo — questionário, versão `v1-rascunho` (substituída na v0.25)

Fica como registro, e no código, porque os pedidos feitos nela continuam legíveis e verificáveis. Era o rascunho para revisão do pesquisador; o que vale é a versão congelada no pré-registro. Implementado em `catalog.Questionnaires` e publicado, campo a campo, na página do P1 (`/protocolo/P1`). Todo campo pontuável tem também "não sei" (consulente) e "não recebi" (médium); os campos de pareamento são obrigatórios para o consulente.

| Campo | Opções | Na nota? |
|---|---|---|
| Gênero | feminino, masculino, outro | não (o nome entrega; pareamento) |
| Faixa etária no óbito | até 17, 18–39, 40–59, 60–79, 80 ou mais | não (pareamento) |
| Período do falecimento | menos de 1 ano, 1–5 anos, 5–15 anos, mais de 15 anos | não (pareamento) |
| Como foi a morte | súbita, após doença prolongada | sim |
| Causa principal | coração ou circulação, câncer, doença respiratória, acidente, violência, outra doença | sim |
| Onde faleceu | hospital, em casa, na rua ou no trânsito, outro lugar | sim |
| Estado civil | solteiro, casado ou em união, viúvo, separado | sim |
| Tinha filhos | sim, não | sim |
| Ocupação principal | campo, ofício manual ou indústria, comércio, escritório ou serviços, saúde, educação, cuidou da casa, outra | sim |
| Onde viveu a maior parte da vida | cidade grande, cidade média ou pequena, zona rural | sim |
| Temperamento | mais expansivo, mais reservado | sim |
| Estatura | abaixo da média, na média, acima da média | sim |
| Religiosidade | praticante, não praticante, sem religião | sim |
| Passatempo mais marcante | música, trabalhos manuais, esporte, leitura, cozinha, plantas, outro | sim |
| Usava óculos | sim, não | sim |
| Fumava | sim, não | sim |

### E14. Precognição com e sem retorno (P5) — *em concepção*

> **Em concepção (v0.20).** Rascunho do desenho, derivado de `docs/VISION-AXION.md`, onde estão também as referências; elas entram em `REFERENCES.md` quando o P5 sair da concepção. Nada aqui é implementado, e o P5 fica fora do catálogo público até lá (seção 1.2). As decisões pendentes são as questões 14 a 18 (seção 9); a 13, o tipo, está decidida: experimento.

**Objetivo**: saber se a taxa de acerto em escolha forçada depende de a pessoa receber retorno sobre o alvo, num estudo confirmatório em que o alvo não está determinado em lugar nenhum na hora da resposta, e publicar o resultado como detecção ou como limite de exclusão.

**Por que este protocolo**: os protocolos do catálogo perguntam se houve informação; nenhum pergunta de que ela depende. O modelo multifásico de Marwaha e May (2015) propõe que a psi informacional seja precognição da própria experiência futura de retorno; nesse caso, sem retorno não haveria sinal. A evidência é indireta: na meta-análise de Honorton e Ferrari (1989), sete dos oito estudos com participantes selecionados, teste individual e retorno a cada tentativa tiveram resultado significativo, numa comparação entre estudos; e o único teste direto encontrado, o Selfield (Varvoglis e colaboradores, 2019), teve 3 mil tentativas e diferença não significativa. A resposta interessa ao ramo AIR: se o acerto só aparece com retorno, negar a devolutiva ao médium vira controle no P1.

**Ramo, tipo e rótulo**: ramo ESP (precognição); tipo **experimento** (questão 13, decidida na v0.20), porque roda só em estudo pré-registrado, com conta, e só tem resultado no fim do estudo; rótulo provisório **P5-retorno**, nome público **Áxion**. Acaso de 25%, declarado (seção 1.1, regra 3). Na implementação, o tipo do P5 é declarado, e não derivado de `Protocol.Branch`, que daria "teste" a um protocolo de ESP; o rótulo do tipo na página é "Experimento de percepção extrassensorial (ESP)".

**Desenho**:

- **Só como estudo.** O P5 roda só como estudo pré-registrado, com participantes identificados por conta com e-mail confirmado e termo na versão corrente (E10, travas 3 e 6). Não há sessão anônima.
- **Sessão com número fixo de tentativas**, declarado no pré-registro, como no P3.
- **Rodada fixada antes da resposta.** Ao abrir cada tentativa, o sistema fixa a rodada futura do farol que vai defini-la — drand, rede *quicknet*, uma rodada a cada 3 segundos —, e a trilha registra a tentativa com a rodada.
- **Resposta antes da rodada.** A pessoa escolhe uma entre quatro alternativas, e a resposta trava com recibo. Resposta que chegue depois da publicação da rodada é recusada pelo banco, e a tentativa fica anulada. A anulação não depende do desfecho, que a pessoa não tem como conhecer.
- **Condição e alvo saem da semente e da rodada.** A semente do estudo é sorteada com `SecureRandom`, comprometida no pré-registro, guardada em custódia cifrada e revelada no desmascaramento (Constitution P5). Publicada a rodada, a condição — com ou sem retorno, com probabilidade 1/2 — e o alvo são função pública da semente e do valor da rodada, como os sorteios do P1 são função da semente e de entradas registradas. A regra exata fica para a especificação.
- **Na hora da resposta, nada está determinado.** Sem a rodada, nem o servidor conhece o alvo ou a condição; sem a semente, quem conhecer a rodada também não.
- **Sem retorno, o alvo só existe no desmascaramento.** Imposto no banco: o alvo de uma tentativa sem retorno fica vazio até lá.
- **Retorno no fim da sessão.** A pessoa vê, de uma vez, o alvo de cada tentativa com retorno ao lado da própria resposta; das tentativas sem retorno, vê a própria resposta e o aviso de que não haverá retorno. O sistema não calcula total, taxa nem agregado de nenhum tipo.
- **Sessão abandonada** fica registrada e entra no denominador, como no P3. O retorno das tentativas com retorno fica disponível na conta, e a trilha registra se ele foi visto.
- **Desmascaramento automático no N** pré-registrado: a semente é revelada, os alvos das tentativas sem retorno são calculados, e a análise e o relatório rodam. Ninguém escolhe o momento.

**Trava própria do P5** (proposta; questão 14): uma exceção estreita à trava 1 do E10. O participante vê o retorno das próprias tentativas com retorno no fim de cada sessão, enquanto o estudo corre, porque essa é a manipulação do estudo. Nada além disso: nenhum agregado, para ninguém; nunca o alvo de uma tentativa sem retorno; para o administrador, só contagens. A exceção não reabre a parada opcional, porque a análise é por tentativa, com toda tentativa travada no denominador.

**Análise** (rascunho):

- **Unidade: a tentativa, contra o acaso declarado.** A pessoa vê o próprio retorno e pode continuar ou parar a partir dele, e médias por pessoa ficariam enviesadas por essa decisão. A contagem de acertos sobre todas as tentativas travadas não fica: sob a hipótese nula, cada tentativa acerta com probabilidade 0,25, qualquer que seja a decisão de continuar.
- **Duas comparações pré-registradas**: o efeito total, com as duas condições juntas contra o acaso, e a diferença entre as condições, com intervalo que leve em conta a variação entre pessoas.
- **Gráfico de exclusão**: para cada condição e para a diferença, o maior efeito compatível com os dados. "Sem diferença" só vale se o intervalo excluir uma margem declarada no pré-registro.
- **Tamanho**: o efeito médio de Honorton e Ferrari, cerca de 0,02 por tentativa, é pouco menos de 1 ponto percentual acima de 25%. Detectar uma diferença dessa ordem entre as condições, com poder de 80% e α = 0,05 unilateral, pede cerca de 60 mil tentativas (questão 18).

**Matriz de visibilidade do P5** (rascunho):

| Informação ↓ / Papel → | Administrador | Participante |
|---|---|---|
| Identidade das contas | ✓ | só a própria |
| Respostas | ✗ até o desmascaramento | só as próprias |
| Condição de cada tentativa | ✗ até o desmascaramento | só as próprias, no fim da sessão |
| Alvo de tentativa com retorno | ✗ até o desmascaramento | só os próprios, no fim da sessão |
| Alvo de tentativa sem retorno | ✗ até o desmascaramento, quando passa a existir | ✗ nunca |
| Total, taxa ou agregado de acertos | ✗ até o desmascaramento, quando passa a existir | ✗ o sistema não calcula |
| Semente do estudo | ✗ até a revelação | ✗ |
| Rodadas do farol | ✓ públicas | ✓ públicas |

**Dependência nova** (Constitution P8): o cliente do farol. Ele é o ponto do desenho, o único jeito de o alvo não estar determinado nem para quem opera o servidor. A plataforma não passa a depender dele: só o P5 espera pela rodada, e nunca a troca por semente local. Onde se verifica a assinatura é a questão 16.

**O que o resultado do P5 não sustenta**:

- Um nulo vale para escolha forçada on-line, com voluntários não selecionados e retorno no fim da sessão. Não refuta resposta livre, participantes selecionados nem retorno imediato.
- No desmascaramento, o administrador vê o agregado das tentativas sem retorno: o desenho não exclui psi do experimentador.
- Um positivo só com retorno apoia os modelos de retorno, mas não distingue entre eles.
- Nada sobre pessoas falecidas ou sobrevivência. O vínculo com o P1 é de desenho, não de evidência.

**Aceite** (rascunho, para quando o P5 sair da concepção): nenhuma tentativa sem rodada fixada antes da resposta; resposta depois da rodada recusada pelo banco; alvo e condição não obtíveis por nenhuma via antes da rodada; alvo de tentativa sem retorno nunca exibido ao participante nem calculado antes do desmascaramento; nenhum agregado antes do desmascaramento; a semente revelada e as rodadas públicas reproduzem todos os alvos e todas as condições; sessões abandonadas no denominador; e a análise refeita pelo script de referência dá os mesmos números.

**Fora deste épico**: o retorno imediato e as outras varreduras, que serão novos estudos do P5, cada um com o próprio pré-registro; o braço sem devolutiva no P1; e o P6.

## 6. Modelo de domínio (macro)

```
Study 1──* PreregistrationVersion (hash, timestamp proof, frozen_at)
Study 1──* Pair 1──2 Target ──1 Sitter(code)
Study 1──1 AllocationSet (seed_commitment, seed_revealed_at)
Pair 1──2 Session ──1 Medium(code), Experimenter(code)
Session 1──1 Reading 1──* RedactedReading(version) 1──* Item
Consulente 1──1 ScoringPacket ──2 RedactedReading (ordem sorteada)
ScoringPacket 1──* ItemScore, 2 GlobalScore, 1 ForcedChoice
Study 1──0..1 Unblinding ──1 AnalysisRun 1──1 Report
Participant(identity schema) 1──* Consent(version)
AuditEvent (seq, prev_hash, hash, type, payload, at)
Incident ──* Session (affected)

TestSession (protocol, declared_trials, seed_commitment, server_seed_hex,
             participant_nonce, seed_hex, seed_revealed_at, pool_version)
TestSession 1──* Trial (index, response, impression_sha256, locked_at, target_index, hit)
TargetImage (pool_version, index, slug)

Account(identity schema: code, profile fixo, nome e e-mail cifrados, índice cego do e-mail,
        hash da senha, termo aceito, email_confirmed_at)
Account 1──* EmailConfirmation (token_sha256, expires_at, used_at)
AdminNotice (kind, subject_code, at)                      — sem dado pessoal, sem escore
ConsultationRequest(P1) ──1 Account(consulente), answers_commitment, locked_at     [fase B]
StructuredStudy(P1) ──1 Study, questionnaire_version, target_pairs, deadline_days     [fase C]
StructuredPair(P1) ──2 ConsultationRequest, dissolved_at                              [fase C]
ReadingAssignment(P1) ──1 StructuredPair, ──1 Account(médium), attempt, pool, due_at  [fase C]
StructuredReading(P1) ──1 ReadingAssignment, ──1 ConsultationRequest, commitment      [fase C]
StructuredScore(P1) ──1 StructuredPair, comparable_fields, s_aa, s_ab, s_bb, s_ba, d  [fase D]
StructuredReport(P1) ──1 Study, document, results, sha256                            [fase D]
FirstNameRelease(identity) ──1 ReadingAssignment, ──1 ConsultationRequest, revoked_at [fase C]
```

## 7. Requisitos não funcionais

| Categoria | Requisito |
|---|---|
| Segurança | 2FA para pesquisadores; links de uso único para consulentes; HTTPS obrigatório; criptografia em repouso para o esquema identity e para mídia |
| Privacidade | Logs sem dados pessoais; minimização; base legal e consentimento documentados (LGPD) |
| Integridade | Tabela de auditoria sem permissão de UPDATE/DELETE para o usuário da aplicação |
| Reprodutibilidade | Análise determinística; versões de bibliotecas fixadas no pacote |
| Disponibilidade | Não crítica; backup diário criptografado e restauração testada mensalmente |
| Usabilidade | Telas de consulente e de médium simples, acessíveis e usáveis no celular; linguagem acolhedora. Regras na seção 1.4; plano em `docs/UX.md` |
| Manutenibilidade | Monólito modular; fronteiras verificadas em teste; documentação viva em `/docs` |
| Desempenho | Volume pequeno (dezenas a centenas de registros); sem requisito especial |

## 8. Corte do MVP

O MVP é o **P1 de ponta a ponta** (E13, fases A a E, implementadas), sobre E1, E2, E3 e E8. Desde a v0.14 o corte não inclui mais o P2 (E4 a E7).
**Fica para depois**: a fase 2 da ancoragem externa (E8) e o espelho no OSF. A fase 1 da ancoragem entrou na v0.15, e o pacote de verificação do P1 (E9), na v0.22.

**Marco de validação do MVP**: um *dry-run* completo do P1 com participantes fictícios (pessoas reais simulando consulentes e médiuns, com falecidos inventados). O objetivo é exercitar todo o fluxo e tentar quebrar o cegamento de propósito antes do piloto real.

## 9. Questões em aberto

1. **Tamanho da amostra**: precisa de cálculo de poder formal, e agora com uma consequência prática em cima da mesa. A dependência entre os dois consulentes de um par **está decidida** (unidade de análise = par, ver E7), e isso tem custo: as cerca de 70 escolhas independentes necessárias para detectar 65% contra 50% (α = 0,05 unilateral, poder de 80%) passam a significar **70 pares, ou seja 140 consulentes e 140 sessões**, não 70 consulentes. A alternativa é usar as duas escolhas de cada par com um método que corrija o agrupamento (por exemplo GEE ou erro-padrão robusto por cluster), o que recupera parte do poder mas exige uma decisão estatística e uma implementação que a v1 não tem. **Decidido na v0.4**: a medida primária passa a ser a diferença pareada contínua, que mantém o par como unidade e recupera o poder — cerca de 30 pares para d = 0,5, contra 240 pares para 58% de acerto na escolha forçada. O cálculo está implementado (`PowerCheck`) e entra no documento congelado. **O que resta decidir**: qual efeito declarar. Os efeitos por seção de leitura em Beischel et al. (2015) vão de d = 0,49 a 0,75, e a seção escolhida muda o tamanho do estudo.
   **Proposta na v0.25** (artigo técnico, seção 4.5): o *d* daqueles estudos não se transfere, porque é de leituras livres pontuadas por consulentes, com desvio-padrão de outra natureza. O que se transfere é a forma do efeito. Numa simulação do próprio P1 (`paper/poder/SimulacaoPoder.java`, só com o JDK), a leitura acerta a resposta verdadeira de quem tem o nome numa fração θ dos campos e chuta nos outros; o "não sei" e o "não recebi" entram com 8% e 10%. Com a `v2-rascunho`, a proporção de pares positivos de 0,68, equivalente ao índice de proporção da meta-análise de Sarraf et al. (2021), corresponde a θ ≈ 0,07 e *d*<sub>z</sub> ≈ 0,42; a diferença entre alvo e chamariz de 12 a 16 pontos percentuais por item, de Beischel et al. (2015), a θ entre 0,2 e 0,25. A proposta é planejar com metade do efeito da meta-análise, por causa do viés de publicação, da falta de consulente substituto e do formato fechado: θ ≈ 0,04, *d*<sub>z</sub> = 0,25, que pedem **120 pares** (240 consulentes) para poder de 80% com α = 0,05 unilateral. O `PowerCheck` dá 114 para o mesmo *d*<sub>z</sub>, e a simulação confirma o poder com empates e zeros. Com a `v1-rascunho`, o mesmo θ pediria cerca de 250 pares. Um piloto de 30 pares tem poder de cerca de 0,38 para esse efeito e precisa dizer isso no pré-registro. **O que resta decidir**, com quem conduzir o estudo: aceitar o efeito proposto e o N, e trocar as frequências supostas por frequências observadas, por estrato, antes do congelamento.
2. **Escala de itens** — *sem objeto desde a v0.14*: era do P2. No P1 a conferência é por igualdade entre opções, e o "não sei" tira o campo da comparação do par inteiro.
3. **Primeiro nome**: dar ao médium o nome real ou um pseudônimo? (Isso afeta a ética e a validade.) No P1, ver a questão 10.
4. **Recrutamento de médiuns**: o campo de critérios já existe no pré-registro e é obrigatório; falta decidir o conteúdo — quais critérios de inclusão e se haverá triagem prévia. Se houver triagem por desempenho, ela precisa estar descrita ali, porque é exatamente o ponto que a crítica usa para explicar resultados positivos.
5. **CEP**: o estudo terá fins de publicação acadêmica? Se sim, a submissão à Plataforma Brasil vem antes do piloto.
6. **Devolutiva**: o que cada participante recebe ao final, e como comunicar resultados nulos com cuidado.
7. **Reconciliação** com a documentação de pesquisa já existente no Google Drive (*TransferênciaAnômalaInformação*).

8. **Confiança no compromisso** — *resolvido para os testes públicos; em aberto para o P1*. A
   entropia do participante está implementada (E11, E12): a semente tem duas metades, o servidor
   se compromete com a dele antes de ver a do participante, e sem a metade do participante não
   existe tentativa. Para o P3 e o P4, a prova não depende mais de ordem atestada por quem opera
   a plataforma.

   Para o **P1**, em que o compromisso da semente e o do pré-registro são atestados pela própria
   plataforma, a v0.15 implementa a **ancoragem externa** da cabeça da trilha por carimbo RFC 3161
   do FreeTSA (E8, fase 1), que torna reescrita retroativa detectável por terceiros. Fica em
   aberto: (a) se o primeiro envio ao médium deve **esperar** o carimbo do congelamento, o que
   fecha a janela de cerca de um minuto ao custo de parar o estudo quando o carimbador estiver
   fora do ar; (b) a segunda âncora, OpenTimestamps, para não depender de uma autoridade só.

9. **P1 — questionário**: o rascunho do anexo do E13 precisa de revisão do pesquisador. Os
   critérios são: opções mutuamente exclusivas, poucas opções dominantes, nenhum campo dedutível
   do primeiro nome na nota, e linguagem cuidadosa com quem está de luto. **Na v0.25**, a `v2-rascunho`
   aplica esses critérios e mais três (um fato verificável por campo, nada que identifique a
   pessoa sozinho, os temas que a literatura pergunta ao médium), com a regra que os justifica
   escrita no anexo do E13. Continua rascunho: faltam a revisão por pesquisadores da área e a
   entrevista cognitiva com consulentes, e só depois dela a versão perde o sufixo `-rascunho`.
10. **P1 — primeiro nome ou nenhum nome**: dar ao médium só um código, sem o nome, eliminaria o
    vazamento pelo nome, mas afasta o protocolo da prática da literatura. Fica o nome, por ora,
    com o vazamento declarado.
11. **P1 — qualificação de médiuns**: uma rodada prévia com o próprio P1 (E10) antes da entrada
    no estudo confirmatório.
12. **P1 — carga por médium** — *resolvido na v0.13*: o teto de pares por médium é declarado no
    pré-registro, e o relatório mostra a soma de `d` por médium. O que resta decidir é o valor do
    teto de um estudo real, que depende de quantos médiuns se inscreverem.
13. **P5 — experimento ou teste** — *decidido na v0.20: experimento*. Pelo vocabulário da v0.18,
    o tipo saía do ramo, e o P5, de ESP, seria teste; mas ele pede conta, só roda como estudo
    pré-registrado e tem resultado no fim do estudo, que é a forma dos experimentos. Desde a v0.20,
    o tipo segue a forma (seção 1.1). A alternativa descartada era um teste que só rodasse como
    estudo confirmatório (seção 1.1, regra 1).
14. **P5 — exceção à trava 1 do E10**: aprovar a trava própria proposta no E14, o retorno no fim
    da sessão. Retorno imediato pediria outra emenda e fica para um estudo posterior.
15. **P5 — dados abertos e o "nunca"**: com a semente e as rodadas, o pacote de verificação (E9,
    Constitution P7) deixaria quem guardou as próprias respostas descobrir o alvo de uma tentativa
    sem retorno. Opções: publicar as tentativas sem retorno sem nada que as ligue a uma pessoa ou a
    um horário, entregar o pacote completo só a auditores, ou publicar depois de um embargo longo.
16. **P5 — o farol**: a justificativa escrita da dependência (Constitution P8) e onde se verifica
    a assinatura da rodada — no servidor, com uma biblioteca nova, ou só no pacote de verificação,
    com o servidor conferindo a rodada em mais de um endereço.
17. **P5 — estímulos**: o banco de imagens versionado do P4 ou quatro alternativas fixas, como no
    P3.
18. **P5 — tamanho e análise**: o N em tentativas ou em pessoas, as tentativas por pessoa, a
    margem de "sem diferença", o α e a ordem das duas comparações.

## 10. Próximos passos

Os passos iniciais desta seção estão feitos: os documentos estão em `/docs` e no `CLAUDE.md`, o plano técnico existe (`docs/PLAN.md`), E8 e E3 estão implementados, e a matriz de cegamento é uma suíte de testes (`BlindingMatrixTest`) que cresce antes de cada tela. O estado detalhado por épico está no plano técnico. A ordem daqui em diante:

Desde a v0.14, só o P1. O P5 (E14) não entra nesta lista enquanto estiver em concepção: nada dele é implementado antes de as questões 14 a 18 serem decididas e o épico ser aprovado (seção 1.2).

1. **E8**: ~~ancoragem externa por carimbo RFC 3161~~ — fase 1, FreeTSA, na v0.15. Falta decidir a espera pelo carimbo antes do primeiro envio e a fase 2, OpenTimestamps (questão 8).
2. **E9**: ~~pacote de verificação do P1~~ — feito na v0.22, público e sem nome.
3. **Questões 9, 11 e 12**: revisão do questionário, qualificação de médiuns e o valor do teto de um estudo real.
4. ***Dry-run*** do P1 (seção 8).
5. **Linguagem e celular (seção 1.4)**: ~~fase 2~~ — feita na v0.24, menos o áudio, que espera gravação em voz humana. Faltam as fases 0, 3 e 4 de `docs/UX.md`, cada uma numa versão própria desta SPEC.
