# Plano técnico do MVP — Crivo (AIR Lab)

> Derivado de `CONSTITUTION.md` (prevalece) e de `SPEC.md`. Versão 1.17 · novembro/2026 · 1.17: **dois idiomas** (SPEC 0.28, seção 1.5; Constitution v1.7.0; guia operacional em `docs/IDIOMAS.md`) — `support.Language` (pt e en, com o prefixo `/en`), `support.Text` (um texto nos dois idiomas, que não compila com um lado faltando) e `support.Refusal`, a recusa de domínio que leva a razão nas duas línguas; `web.LanguageFilter` faz o `/en` passar por *context path* do request, e com isso `@{...}`, `th:action`, `redirect:` e os links que já levam `${site}`/`${aplicacao}` ficam no idioma da página sem mudança de template, além de encaminhar a primeira visita pelo `Accept-Language` e guardar a escolha no cookie `idioma`; `web.LanguagePages` dá o idioma, os dois endereços da página e a razão de uma recusa; `web.WebI18nConfiguration` resolve `templates/en/<mesmo caminho>` como sobreposição, com volta ao português e aviso quando a tradução não existe; `messages.properties` e `messages_en.properties` guardam o texto compartilhado do cabeçalho e do rodapé; `catalog` passa a depender de `support` e o catálogo, o questionário e os rótulos de enum de tela nascem bilíngues; questionário `v3-rascunho` (mesmas chaves da `v2-rascunho`, rótulos nas duas línguas) e termo `conta-v4`; o anexo canônico do pré-registro publica os dois rótulos; migração `V25` guarda o idioma da conta, para os e-mails que saem fora de uma requisição; `BrasiliaTime` escreve prazos nos dois idiomas, e o relatório canônico continua em português, porque é documento de registro; `airlab site` escreve o site nos dois idiomas, com `js/idioma.js` para a primeira visita; `BilingualRoutesTest` cobre a seção, e `BlindingMatrixTest` passa a conferir os dois idiomas · 1.3: protocolo P4 (SPEC E13), fase A — contas com perfil fixo, e-mail e avisos ao administrador · 1.4: fase B — questionário versionado e pedido de consulta travado com compromisso salgado · 1.5: fase C — módulo `structured`: estudo P4, ciclo automático, pareamento, sorteio do médium, leitura travada e prazos · 1.6: fase D — desmascaramento automático no N, conferência, análise, relatório e devolutiva · 1.7: fase E — contas ligadas, teto por médium e página pública dos estudos. · 1.8: o P1 é descontinuado (SPEC 0.14) e fica no site só como referência; `session`, `redaction`, `scoring` e `unblinding` congelam no estado atual; os próximos cortes são só do P4 · 1.9: ancoragem externa da trilha por carimbo RFC 3161 do FreeTSA (SPEC 0.15, E8, fase 1): módulo `anchor`, migração `V22` e a BouncyCastle · 1.10: os testes de ESP saem das páginas do Crivo e passam a ser apresentados pela PsiGama (SPEC 0.16); `trials`, as rotas e as garantias ficam como estão · 1.11: protocolos renumerados (SPEC 0.17) — P1-automático, P2-supervisionado, P3-precognição, P4-visão-remota; `/protocolos` e `/protocolo/P{X}` (`ProtocolsController`), rotas `/testes/...` com 301 (`LegacyRoutesController`); os testes de ESP voltam ao site do Crivo; questionário em vigor na página do P1 · 1.12: site e aplicação separados (SPEC 0.21, seção 1.3) — as páginas editoriais ficam em `templates/site/`, listadas em `web.SitePages`; a aplicação as serve no perfil `local` e responde com 301 quando `AIRLAB_SITE_URL` está definida; `airlab site` (`web.StaticSite`) gera o site sem banco, e o workflow `pages.yml` o publica no GitHub Pages a cada push na `main`; a página inicial se divide em Participar, Por quê, Garantias e Auditar; as oito etapas vão para a página do P1; o estado da trilha vai para `/verificar` · 1.13: pacote de verificação do P1 (SPEC 0.22, E9) — `VerificationPackageWriter` escreve o pacote no desmascaramento (migração `V24`), com a checagem por lista fechada; `StructuredAnalysis` passa a ser a função única da conferência e da análise; `AuditChain` publica a regra de hash da trilha; `airlab verificar-pacote` (`cli.PackageVerification`, `structured.VerificationPackageCheck`) confere o arquivo sem Spring e sem banco; `/estudos/{código}/pacote.json` · 1.14: linguagem, celular e acessibilidade (SPEC 0.23, seção 1.4; plano em `docs/UX.md`) — `support.BrasiliaTime` escreve prazos e datas de participante por extenso, no horário de Brasília, para `web.Datas` (`prazo`, `momento`, `data`; `utc` fica para a auditoria) e para o e-mail do pedido de leitura; o fragmento `topo` desenha a navegação uma vez (`links`) e a repete no Menu do celular, um `<details>` sem script; o fragmento `social` declara `og:image`, a imagem `static/img/compartilhar.png` entra em `StaticSite.ASSETS` e `/img/**` fica público; o fragmento `apoio` leva o CVV a `/consulente` e à devolutiva; `/consulente` lê o `SignUpGate` para terminar com uma ação; o CSS ganha mínimos de letra (13 px), de campo (16 px e 48 px) e de alvo de toque (44 px) · 1.15: segunda fase da seção 1.4 (SPEC 0.24) — a página inicial abre com três portas; `HomeController` serve `/medium` e `/perguntas`, com o estado da inscrição pelo `SignUpGate`, e `ProtocolsController` serve `/pesquisa` e `/pesquisa/en`, com o acaso e os limites do P1 tirados do catálogo; o fragmento `links` agrupa o menu em submenus com classe própria (`participar`, `pesquisa`, `biblioteca`), e o rodapé vira três grupos; Garantias e a página do P1 ganham a camada simples; `AccountService.CURRENT_TERM_VERSION` passa a `conta-v3`; `AudiencePagesTest` cobre a fase · 1.16: só maiores de 18 anos criam conta (SPEC 0.26) — `AccountSignUp.adultDeclared`, recusado em `DefaultAccountService` sem a declaração e registrado como booleano no `ACCOUNT_CREATED`; caixa `maioridade` em `conta-criar.html`, fora do termo, que continua `conta-v3`; nenhuma migração, porque nada além do booleano na trilha é guardado
> Este documento registra as decisões de implementação do MVP: estrutura de módulos,
> esquemas e migrações, execução por linha de comando e justificativa de cada dependência.

---

## 1. Arquitetura de execução (sem Docker)

| Item | Decisão |
|---|---|
| Artefato | **Um JAR executável** (`target/airlab.jar`), com Tomcat embutido |
| Execução | Linha de comando, em Windows e Linux: `scripts/start.sh`, `scripts/start.ps1`, `scripts/start.cmd` |
| Build | Maven Wrapper (`mvnw`), `distributionType=only-script` — **nenhum JAR versionado** no repositório |
| Pré-requisito | Um JDK 21 (LTS). Nada mais |
| Banco em desenvolvimento e teste | **PostgreSQL embarcado** pela aplicação (`io.zonky.test:embedded-postgres`), binários reais do PostgreSQL, em `~/.airlab/pgdata` (fora do repositório e fora de pasta sincronizada) |
| Banco em produção | PostgreSQL do sistema operacional, escutando só em `127.0.0.1` |
| Processo em produção | Unidade `systemd` (Linux) ou serviço do Windows / Agendador de Tarefas |
| Borda em produção | Caddy instalado como pacote do sistema, HTTPS automático, proxy para `127.0.0.1:8080` |
| CLI de auditoria | Mesmo JAR: `scripts/airlab.sh verify`, `scripts\airlab.ps1 verify` |
| Site editorial (SPEC 1.3) | Mesmo JAR, sem banco e sem Spring: `scripts/airlab.sh site <pasta> --aplicacao=<endereço>` (ou `airlab.ps1`). `AirLabApplication.main` desvia para `web.StaticSite` antes de subir o contexto, e o gerador renderiza os templates de `templates/site/` com o `SpringTemplateEngine` (o mesmo dialeto da aplicação), copia o CSS, o favicon e o `encaminhar.js` e escreve a página 404 que encaminha para a aplicação. Publicado pelo GitHub Pages (`pages.yml`), a cada push na `main` |

**Por que o PostgreSQL continua obrigatório**: o P4 (trilha append-only) e o P6 (separação de
esquemas com permissões por papel) são impostos *no banco*. H2 ou SQLite não oferecem papéis,
esquemas e triggers equivalentes, então trocar de banco enfraqueceria dois princípios para
ganhar conveniência. A solução adotada mantém o PostgreSQL e remove o Docker: em
desenvolvimento ele é embarcado no processo da aplicação; em produção é um serviço do sistema.

**Paridade Windows/Linux**: nenhum caminho absoluto no código; diretórios vêm de configuração
com padrão relativo ao diretório de trabalho; o documento do pré-registro usa sempre `\n`, para
que o hash não dependa do sistema operacional.

## 2. Estrutura de módulos (Spring Modulith)

Pacote base `io.github.rogeriocisi.airlab`. Cada módulo declara em `package-info.java` de
quem pode depender; `ModularityTest` verifica isso no build.

| Módulo | Épico | Estado |
|---|---|---|
| `audit` | E8 | **Implementado**: trilha append-only encadeada por hash, verificação da cadeia |
| `allocation` | E3 | **Implementado**: sorteio reproduzível, compromisso → revelação, custódia da semente |
| `study` | E1 | **Implementado**: estudo, documento canônico do pré-registro, congelamento, emendas |
| `participant` | E2, E13 | **Implementado**: participantes, consentimento, alvos, pareamento, desistência; **contas do P1** (`AccountService`) com perfil fixo, confirmação de e-mail por link de uso único e exclusão que mantém o código; **pedidos de consulta do P1** (`ConsultationService`), travados na criação, com compromisso salgado (`ConsultationCommitment`), e a **liberação dos primeiros nomes** só ao médium sorteado; **sinalização de contas ligadas** por índice cego do e-mail canônico e do nome normalizado, com revisão humana e sem bloqueio |
| `notification` | E13 | **Implementado**: `Mailer` — caixa de saída em memória nos perfis `local`/`test`, SMTP em produção, desativado sem SMTP —, `AdminNotices`, avisos ao administrador só com tipo, código e hora, e o e-mail ao médium sorteado, sem nomes |
| `structured` | E13 | **Implementado (fases C e D)**: estudo P1 congelado com o questionário no anexo (`StructuredStudyService`), ciclo automático — pareamento, sorteio do médium, envio, prazos e reenvio —, leitura do médium travada com compromisso salgado (`MediumReadingService`, `ReadingCommitment`); no N, desmascaramento automático, conferência (`StructuredScoring`), análise (`StructuredAnalysis`, a mesma função que o conferidor externo roda), relatório (`StructuredReport`) e devolutiva (`FeedbackService`); desde a 1.13, o **pacote de verificação** público (`VerificationPackageWriter`) e o conferidor de referência (`VerificationPackageCheck`) |
| `redaction` | E5 | **Congelado (P2 descontinuado)**: `ClueScrubber` fica como está; nada mais será implementado |
| `analysis` | E7 | Parcial: Wilcoxon pareado exato (medida primária), binomial exato, permutação de Pratt e Birge e a sua versão **dentro dos estratos** (`StratifiedPermutationTest`, P1), `PowerCheck`, política de rótulo e redução às unidades independentes. Persistência e relatório pendentes |
| `session` | E4 | **Congelado (P2 descontinuado)**: só as tabelas da `V6`; não será implementado |
| `scoring` | E6 | **Congelado (P2 descontinuado)**: escala de cinco categorias e `AccessLink` ficam como estão; nada mais será implementado |
| `unblinding` | E7 | **Congelado (P2 descontinuado)**: só as tabelas da `V9`. O desmascaramento do P1 mora em `structured` |
| `cli` | E8, E9 | **Implementado**: `airlab verify` — trilha, pré-registros, sorteios e, desde a 1.9, os carimbos de tempo; desde a 1.13, `airlab verificar-pacote`, que confere um pacote do P1 só pelo arquivo, sem Spring e sem banco |
| `anchor` | E8 | **Implementado (fase 1)**: ancoragem externa da cabeça da trilha por carimbo RFC 3161 do FreeTSA — logo depois de cada marco e a cada 24 horas se a trilha mudou; token conferido antes de guardar (`TimestampProtocol`), certificados fixados pelo SHA-256 publicado (`TrustAnchors`), página pública `/carimbos` com o `.tsr`, e reconferência na CLI |
| `catalog` | — | **Implementado**: catálogo de protocolos (SPEC 1.1) e questionários versionados do P1 (`Questionnaires`), sem estado e sem dependência de outro módulo. `all()` em ordem de código; `air()` (P1, P2) e `esp()` (P3, P4) são as duas seções da página `/protocolos`; `Protocol.label()` dá o rótulo, como `P1-automático` |
| `trials` | E11, E12 | **Implementado**: sessões de autoatendimento dos dois testes de ESP (P3 e P4) — semente comprometida, alvo derivado só no travamento, impressão travada antes das candidatas, sessão abandonada registrada. Rodam em `/protocolo/P3/rodar` e `/protocolo/P4/rodar`; o compromisso usa o nome do enum, não o código, e por isso não mudou com a renumeração |
| `support`, `platform`, `web` | — | Infraestrutura, páginas públicas, criação de conta, entrada e painéis por perfil (consulente, médium, administrador). Desde a 1.12, `web` também tem as páginas editoriais (`SitePages`, `templates/site/`), o gerador do site estático (`StaticSite`) e o estado da trilha em `/verificar` (SPEC 1.3) |

Dependências entre módulos: `audit` não depende de nenhum módulo de domínio; todos os outros
dependem dele. O plano de alocação sai do módulo `allocation` apenas como evento de domínio
(`AllocationDrawn`), nunca por consulta, porque o pesquisador não pode ver a ligação
leitura → alvo antes do desmascaramento (SPEC, seção 4).

## 3. Esquemas e migrações

Flyway, em `src/main/resources/db/migration`. Histórico no esquema `research`.

| Migração | Conteúdo |
|---|---|
| `V1` | Esquemas `research`, `identity`, `audit` |
| `V2` | `audit.event` + triggers que recusam UPDATE, DELETE e TRUNCATE (P4) |
| `V3` | `research.study`, `research.preregistration_version` + trigger de imutabilidade do congelado (P2) |
| `V4` | `identity.participant`, `identity.target_detail`, `identity.consent`, `research.participant_role`, `research.target`, `research.pair` (P6) |
| `V5` | `research.allocation_set` + trigger de imutabilidade do sorteio; semente sob custódia cifrada (P5) |
| `V6` | Sessões, leituras brutas, ocorrências, incidentes (E4) |
| `V7` | Leituras redigidas e itens (E5) |
| `V8` | Pacotes de avaliação, pontuações, escolha forçada (E6) |
| `V9` | Desmascaramento (append-only) e execuções de análise (E7) |
| `V10` | Papel `airlab_app` com privilégio mínimo: na trilha, só `SELECT` e `INSERT` |
| `V11` | Escala de itens de cinco categorias, com justificativa obrigatória imposta por `CHECK` (E6) |
| `V12` | `research.protocol_deviation`, append-only por *trigger* (E1/E7) |
| `V13` | Comentários em português passam a dizer **consulente** no lugar de *sitter* (SPEC 0.5) |
| `V14` | Índice cego do contato em `identity.participant`, para detectar papel duplo sem guardar contato em claro (E10) |
| `V15` | `research.test_session` e `research.test_trial` dos testes de autoatendimento, com `CHECK` que impede alvo antes do travamento e *trigger* que torna a tentativa travada imutável (E11/E12) |
| `V16` | Entropia do participante: `participant_nonce` e `server_seed_hex`, *trigger* que recusa tentativa de sessão sem a metade do participante, e preenchimento das sessões anteriores (`server_seed_hex = seed_hex`, porque antes a semente era toda do servidor) |
| `V17` | Contas do P1 (`identity.account`, `identity.email_confirmation`) e avisos (`research.admin_notice`). *Trigger* recusa troca de perfil, de código, reabertura de conta encerrada, reescrita da data de confirmação e qualquer `DELETE`; `CHECK` impede perfil fora de consulente e médium e exige dados apagados na conta encerrada; índice único parcial: uma conta ativa por e-mail (E13) |
| `V18` | Pedido de consulta do P1: `research.consultation_request` (código, consulente, versão do questionário, atributos de pareamento, compromisso, estado) e `identity.consultation_detail` (primeiro nome, respostas e sal, cifrados). *Triggers*: pedido travado só muda de estado, retirado é final, nada se apaga, e o detalhe só admite o apagamento na desistência; índice único parcial: um pedido ativo por consulente (E13, fase B) |
| `V19` | P1, fase C: `research.structured_study` (versão do questionário, N, prazo; imutável), `research.structured_pair` (dois pedidos travados; só se desfaz), `research.reading_assignment` (envio a um médium sorteado, com a lista de elegíveis e a ordem dos nomes; só se encerra), `research.structured_reading` (leitura cifrada com compromisso; imutável) e `identity.first_name_release` (a quem o nome foi liberado; só se revoga). Chaves estrangeiras para `consultation_request`: não existe par nem envio sobre pedido que não esteja travado (E13, fase C) |
| `V20` | P1, fase D: `research.structured_score` (campos comparáveis, as quatro contagens e o `d` de cada par, com o médium revelado) e `research.structured_report` (números da análise e documento canônico com hash). *Trigger*: as duas recusam linha de estudo que não esteja desmascarado, e nenhuma linha muda nem some — nenhuma nota existe antes do desmascaramento, nem por SQL (E13, fase D) |
| `V21` | P1, fase E: `identity.account_signal` (índice cego do e-mail canônico e do nome normalizado; apagado na desistência) e `identity.account_flag` (duas contas que compartilham um sinal; decidida uma vez, nunca apagada); coluna `max_pairs_per_medium` em `research.structured_study`, nula nos estudos anteriores |
| `V22` | Ancoragem externa (SPEC 0.15, E8): `audit.timestamp_anchor`, no esquema `audit`, onde a aplicação só tem `SELECT` e `INSERT`. *Trigger* recusa `UPDATE`, `DELETE`, `TRUNCATE` e linha cujo hash não seja o do evento daquela posição da trilha; `CHECK` exige token, hora atestada e série no carimbo concedido e motivo na falha |
| `V23` | Acessos do `airlab_app` que faltavam às tabelas e sequências da `V12` e da `V15` |
| `V24` | Pacote de verificação do P1 (SPEC 0.22, E9): `research.structured_package` (documento JSON canônico com hash). A mesma *trigger* da `V20`: nenhum pacote antes do desmascaramento, e nenhuma linha muda nem some |

**Migração aplicada não se edita**: corrigir o vocabulário dos comentários mudaria o checksum
do Flyway e quebraria a validação em qualquer banco já migrado — inclusive o do próprio autor.
Por isso a troca de `sitter` para `consulente` nos comentários veio como `V13`, e não como edição
das migrações anteriores.

**Garantias no banco, não só no código**: os triggers alcançam qualquer papel, inclusive
superusuário, de modo que a imposição vale também no perfil local, onde a aplicação conecta
como proprietária do banco.

## 4. Os dois testes de autoatendimento (E11 e E12)

A plataforma passou a cobrir mais de um protocolo. O catálogo está na SPEC, seção 1.1; aqui
ficam as decisões de implementação. Desde a SPEC 0.17 os dois testes são o P3 e o P4 e voltaram a ser
listados no site do Crivo, em `/protocolos` (decisões no fim da tabela).

| Decisão | Por quê |
|---|---|
| Um módulo só (`trials`) para os dois protocolos | Eles compartilham tudo que importa: sessão com N fixo, semente comprometida, alvo derivado no travamento, revelação no fim. O que difere é um passo a mais na visão remota |
| Alvo derivado, não sorteado e guardado | `alvo(n) = SeedStream(semente, "trial:" + n) mod alternativas`. Nada é gravado antes da resposta: a coluna `target_position` é nula até o travamento, imposto por `CHECK` |
| **Semente em duas metades** | `semente = SHA-256(metade do servidor \|\| metade do participante)`. O servidor publica o hash da metade dele antes de a outra existir; o navegador sorteia a dele com `crypto.getRandomValues`. Nenhum dos lados escolhe o resultado, e a prova deixa de depender de ordem atestada pela plataforma |
| Sem a metade do participante, nada existe | Nem semente, nem alvo, nem linha de tentativa. Imposto por *trigger* (V16), não só no serviço: é essa ordem que sustenta a afirmação do protocolo |
| O compromisso é guardado pelo próprio navegador | `sessionStorage`, no momento em que a tela da entropia chega. No fim, o verificador compara — a conferência que antes exigia a pessoa copiar o hash à mão. Quando não há armazenamento, a página diz que essa parte não foi conferida |
| Semente em custódia cifrada, revelada no fechamento | Mesmo esquema do sorteio do P2. Um dump do banco durante a sessão não entrega o alvo |
| Compromisso liga a semente aos parâmetros | `SHA-256(semente \| protocolo \| tentativas \| alternativas \| versão do banco)`. Sem isso, um compromisso poderia ser "honrado" por uma sessão de outro tamanho |
| Impressão travada antes das candidatas (P4) | A trilha guarda só o hash da impressão. É o que sustenta a afirmação de que o texto não foi reescrito depois de ver as figuras — objeção padrão ao autojulgamento |
| Banco de figuras versionado no repositório, não no banco de dados | A versão usada fica na sessão. Assim a aplicação e o pacote de verificação não podem discordar sobre o que era a figura 7 |
| Sessão anônima, sem conta nem e-mail | Não há nada aqui que justifique pedir dado pessoal, e é isso que permite publicar o conjunto |
| `SessionProgress` sem nenhum campo de desfecho | Trava 1 do E10 aplicada ao autoatendimento. Há teste estrutural: um campo com nome de acerto, escore ou alvo derruba o build |
| Alvo e acerto fora da trilha por tentativa | A trilha é pública. Publicar o acerto de cada tentativa entregaria o placar que a sessão esconde; eles entram no evento de fechamento, com a semente |
| Sessão abandonada é registrada e fica | Desistir de uma sessão ruim não melhora o número agregado |
| **Listados no site do Crivo** (SPEC 0.17) | A página `/protocolos` mostra os quatro protocolos: o P1 e o P2 na seção AIR, o P3 e o P4 na de ESP, com o ramo declarado. Na v0.16 eles tinham saído do site do Crivo para a PsiGama; a 0.17 desfez isso. A PsiGama continua apresentando os testes de ESP e aponta para as páginas daqui |
| Rotas por código, antigas redirecionadas | `/protocolo/P3` e `/protocolo/P4` (página), `/protocolo/P3/rodar` e `/protocolo/P4/rodar` (o teste), com os passos e o resultado sob o mesmo caminho; o código no endereço ignora maiúsculas e redireciona para a forma canônica. `LegacyRoutesController` responde 301 de `/testes`, `/testes/{nome}`, `/rodar` e `/resultado` para as novas, porque a página `/pes` da PsiGama e links já publicados apontavam para elas |
| As páginas dos testes voltam ao catálogo | A página do protocolo leva a "Todos os protocolos", e o resultado a "Outros testes de ESP" (`/protocolos#pes`). `NavigationAdvice` marca "Protocolos" como seção atual em `/protocolos` e em toda página sob `/protocolo/` |

**O que estes dois testes não fazem**: não dizem nada sobre mediunidade, e o pool público é
exploratório — autoatendimento anônimo não controla quem testa, quantas vezes nem em que
condições. Para resultado confirmatório, vale a política de rótulo que já existe: pré-registro
congelado, participantes identificados e N declarado antes da coleta.

## 4.1 Leitura estruturada automatizada (P1, E13) — fase A

| Decisão | Por quê |
|---|---|
| Conta no módulo `participant` | Conta é dado identificável, e só `participant` lê o esquema `identity` (regra arquitetural 2) |
| Perfil fixo, imposto por *trigger* | A forma mais simples da trava 2 do E10: com um e-mail, ninguém fica dos dois lados. Nenhum caminho do site cria administrador; ele vem da configuração |
| Principal da sessão = código pseudônimo | O e-mail serve só para achar a conta pelo índice cego; depois da entrada, nada na sessão o carrega (P6) |
| Conta não confirmada é recusada **depois** da senha | A checagem usa "credencial expirada", que roda depois da senha, e não "conta desativada", que roda antes: uma senha errada não revela se o e-mail está cadastrado |
| Cadastro repetido mostra a mesma página | O formulário não serve para descobrir quem participa; o e-mail enviado diz o que aconteceu |
| Confirmação por botão, não ao abrir o link | Serviços de e-mail abrem links sozinhos para checá-los; um GET que confirmasse seria consumido por eles |
| Token de confirmação só como hash, 48 horas | Mesmo esquema do `AccessLink` do E6 |
| Inscrição fechada sem e-mail, sem contato humano ou, em produção, sem a referência do CEP | Travas 3, 4 e 5 do E10. No perfil local, nada sai da máquina: o link vai para a caixa de saída (só o administrador abre) e para o log, com código e sem endereço |
| Aviso ao administrador só com tipo, código e hora | Painel e e-mail não são canais cegos. O registro não tem coluna para mais nada |
| Notas não existem antes do desmascaramento (fases B a D) | A conferência só roda depois dele; não é questão de esconder, é de não calcular |
| **Fase B**: rascunho só na sessão HTTP | O banco recebe o pedido uma vez, já travado; não existe janela em que um pedido gravado pudesse mudar antes de o compromisso sair |
| Compromisso `SHA-256(JSON canônico \|\| "\|" \|\| sal)` | O sal de 32 bytes, em custódia cifrada, impede o ataque de dicionário sobre respostas de pouca entropia numa trilha pública. A regra é pública (`ConsultationCommitment`) |
| Trilha com código, consulente, versão e compromisso | Nem os atributos de pareamento: só o compromisso precisa ser público antes da leitura |
| Questionário no `catalog`, como constante versionada | Mesma razão do catálogo e do banco de figuras: a versão usada fica no pedido e no compromisso, e a aplicação e o pacote não podem discordar sobre o que era a pergunta 7 |
| Faixas de pareamento iguais às do P2 | O pareamento da fase C é o mesmo código para os dois protocolos |
| Respostas guardadas como linhas `chave=opção` cifradas | Chaves e opções são *slugs*; o formato dispensa parser de JSON e não entra em hash nenhum |
| Formulário fechado em produção sem `AIRLAB_CEP_REFERENCE` | E10, trava 4: o pedido coleta dado sensível sobre a pessoa falecida |
| **Fase C**: módulo próprio, `structured` | Pares, envios e leituras não são dado identificável e não cabem em `participant`; o nome continua lá, e só sai por uma liberação registrada |
| Estudo P1 no módulo `study`, com anexo canônico | O mesmo documento com hash, congelamento e *trigger* do P2; o anexo leva o questionário inteiro, o N, o prazo e as regras de sorteio, e o P2 continua byte a byte igual |
| Pré-registro do P1 sem rascunho gravado | Como o pedido: o administrador revê na sessão e congela num passo. Emenda de P1 é recusada até existir uma que preserve o anexo |
| Compromisso sobre a semente (`AllocationService.commitSeed`) | Os pares se formam durante o estudo; cada sorteio é função pública da semente e de entradas registradas (`AllocationEngine.structuredMedium`, `structuredNameOrder`), em forma canônica |
| Ciclo agendado a cada minuto, não evento síncrono | Um lugar só para pareamento, envio, prazo e cancelamento, idempotente e com o relógio como parâmetro; os testes o chamam com o relógio que precisam. Desligado na CLI e nos testes |
| Nenhum código de médium na trilha antes do desmascaramento | Matriz do P1: ligação pedido → médium ✗ para o administrador, e a trilha é pública. Envio, liberação e leitura vão à trilha só com códigos de envio, par, leitura e hashes |
| Liberação do nome registrada no esquema `identity` | `participant` recusa o nome a quem não tem liberação não revogada; vencimento, cancelamento e retirada revogam |
| O médium responde só aos campos da nota | Os de pareamento não contam e são iguais (ou dedutíveis do nome) dentro do par |
| E-mail ao médium depois do *commit* | Ninguém é avisado de um envio que um *rollback* desfez; o e-mail não leva nome nem código de par |
| **Fase D**: desmascaramento no ciclo, no módulo `structured` | É automático e as entradas estão ali; o módulo `unblinding` continua reservado ao P2, onde o desmascaramento é uma operação explícita |
| Uma transação, em ordem fixa | Coleta encerrada → semente revelada → desmascarado → *flush* → respostas reveladas → compromissos conferidos → notas → análise → relatório → relatado. O *flush* põe o estado no banco antes da primeira nota, que o *trigger* confere |
| Respostas só para pedido em "estudo concluído" | `ConsultationService.revealedAnswers` devolve nada para outro estado, e confere o compromisso de cada pedido onde estão o nome e o sal |
| Permutação dentro dos estratos | Leituras de estratos diferentes não são trocáveis sob a hipótese nula: o nome e o estrato já as separam |
| Relatório com o médium de cada par | Depois do desmascaramento a ligação é pública (matriz do P1), e a tabela por médium mostra se um só carrega o resultado (questão 12) |
| Devolutiva construída a partir das notas | Sem nota, não há o que montar: a regra "nada antes do desmascaramento" não depende de um `if` |
| Banco próprio para o teste de ponta a ponta | O banco de teste compartilhado mantém um estudo P1 aberto com N grande; `airlab.local-database.database-name` e `recreate-database` dão ao `P4UnblindingTest` um banco recriado a cada execução, no mesmo cluster embarcado |
| **Fase E**: sinais calculados na confirmação, só como índice cego | Conta não confirmada pode ser lixo; e nem o e-mail canônico nem o nome ficam em claro. Nenhum IP ou aparelho é coletado (P6) |
| Sinalizar, não bloquear | Um homônimo não pode perder o acesso por coincidência. O efeito é só no sorteio: médium e consulente sinalizados não se encontram, e consulentes sinalizados entre si não formam par, até a decisão "sem relação" |
| Trilha sem os códigos das contas | Uma suspeita não é um fato, e a trilha é pública: vão o código da sinalização, o sinal e a decisão |
| Teto filtra a lista de elegíveis | O sorteio continua uma escolha sobre a lista registrada; quem está no teto simplesmente não está nela |
| Página pública só com o que já é público | Pré-registro e contagens enquanto coleta; o relatório, que só existe depois do desmascaramento, quando termina |
| Nomes únicos nas contas de teste (`TestNames`) | Com o sinal de nome, contas de teste homônimas no banco persistente se sinalizariam todas entre si |

## 5. Decisões metodológicas impostas pelo código

Críticas recorrentes a esta literatura que não se resolvem com disciplina do pesquisador; o
sistema as trata como regra:

| Crítica | Como o sistema responde | Onde |
|---|---|---|
| Resultados positivos vêm de "médiuns selecionados", sem critérios publicados | `mediumInclusionCriteria` é campo obrigatório do pré-registro, entra no documento com hash antes da coleta | `study.PreregistrationPlan` |
| Parada opcional: encerrar a coleta quando os números ficam bons | A análise só recebe o rótulo **confirmatória** se o pré-registro estava congelado e o número de unidades coletadas for exatamente o alvo declarado. Fora disso, sai rotulada como exploratória, com o motivo impresso ao lado | `analysis.ConfirmatoryAnalysisPolicy` |
| Dependência entre os dois consulentes de um par, tratados como observações independentes | A unidade da análise confirmatória é o par: entra só a escolha do consulente **sorteado** para aquele par, antes da primeira sessão. A outra escolha é relatada como secundária | `allocation.AllocationPlan.ConfirmatoryUnit`, `analysis.IndependentUnits` |
| Estudo subdimensionado cujo resultado nulo é apresentado como ausência de efeito | O mínimo de pares para o efeito declarado é calculado e **escrito no documento congelado**, com a frase que diz se o desenho atende ou não | `analysis.PowerCheck` |
| Comprimento da leitura como pista e como confundidor | Tolerância pré-registrada de diferença de itens; a leitura mais longa é cortada por subamostra sorteada da semente | `PreregistrationPlan.maxItemCountDifference`, `AllocationEngine.balancedSubsample` |
| Pistas temporais que permitem ao consulente reconhecer a própria leitura | Itens apresentados em ordem sorteada; slots de sessão contrabalanceados por quadrado latino, sorteados junto com o resto | `AllocationEngine.itemPresentationOrder`, `AllocationPlan.SessionSlot` |
| Ordem de apresentação desequilibrada dentro do par | Um consulente vê a própria leitura primeiro e o outro vê o chamariz; qual dos dois é sorteado | `AllocationEngine` (domínio `presentation:`) |
| Item que se encaixa em outra pessoa próxima, forçado a virar acerto ou erro | Categoria própria na escala, com explicação escrita obrigatória, imposta também por `CHECK` no banco | `scoring.ItemScoreValue`, `scoring.ItemScoreEntry`, migração `V11` |
| Execução que se afasta do plano congelado e não aparece no relatório | Desvio de protocolo é entidade própria, append-only, distinta de emenda | `study.ProtocolDeviation`, migração `V12` |
| Multiplicar probabilidades de itens como se fossem independentes | Análise vedada. A alternativa do campo é o teste de permutação de Pratt e Birge | `analysis.PermutationTest` |
| Painel de progresso que vira análise interina contínua | A projeção de progresso não tem campo de desfecho, e um teste estrutural reprova o build se alguém acrescentar um | `analysis.CollectionProgress` |
| Conluio na inscrição aberta: a mesma pessoa como médium e como consulente | Um papel por pessoa em cada estudo, reconhecida por índice cego do contato; a recusa vai para a trilha | `support.BlindIndex`, migração `V14` |
| Link de avaliação reaproveitado, vazado ou reaberto depois do envio | Token de uso único guardado só como hash, com prazo, comparação em tempo constante e recusa definitiva após o envio | `scoring.AccessLink` |

O sistema não pretende impedir que uma coleta seja interrompida — isso está fora do alcance de
software. O que ele faz é recusar o rótulo de confirmatória nesse caso, o que é a parte que
importa para quem lê o relatório.

### Medida primária e poder

Com o par como unidade, as ~70 observações do cálculo original passariam a significar 70
pares — 140 consulentes e 140 sessões. Pior: a meta-análise de Sarraf, Woodley of Menie e
Tressoldi (2021) estima o efeito do campo em **6 a 14 pontos acima do acaso**, e a 58% de
acerto aquelas 70 unidades dariam cerca de **32% de poder**.

Mínimos para α = 0,05 unilateral e poder de 0,80, conforme `PowerCheck` (com valores de
referência nos testes):

| Medida primária | Efeito declarado | Pares necessários |
|---|---|---|
| Escolha forçada | 56% de acerto | 428 |
| Escolha forçada | 58% | 240 |
| Escolha forçada | 60% | 153 |
| Escolha forçada | 65% | 67 |
| Diferença pareada | d = 0,49 | 30 |
| Diferença pareada | d = 0,50 | 29 |
| Diferença pareada | d = 0,57 | 22 |
| Diferença pareada | d = 0,75 | 13 |

Daí a decisão da v0.4 da SPEC: **a medida primária passa a ser a diferença pareada
alvo − chamariz**, testada com Wilcoxon exato, o que mantém o par como unidade independente e
recupera o poder. A escolha forçada continua coletada e relatada como secundária. Beischel e
Schwartz já esperavam que ela fosse o indicador menos sensível, e os números deles
confirmaram (notas globais p = 0,007; escolha forçada p = 0,01).

O que resta decidir é qual efeito declarar: os efeitos por seção de leitura em Beischel et al.
(2015) vão de d = 0,49 a 0,75.

## 6. Decisões criptográficas

| Decisão | Por quê |
|---|---|
| Hash da trilha sobre JSON canônico, **sem o número de sequência** | O auditor precisa recomputar os hashes a partir do pacote exportado, sem saber como as linhas foram numeradas |
| Semente de 32 bytes do `SecureRandom` | P5, imprevisibilidade |
| Expansão da semente por **SHA-256 em modo contador** (`SeedStream`), não por `SecureRandom` | Nenhum algoritmo de `SecureRandom` garante o mesmo resultado em outra JVM ou outro sistema; o sorteio tem de ser reproduzível por terceiros |
| Um domínio de expansão por decisão (`medium:`, `reading-order:`, `experimenter:`, `presentation:`) | Acrescentar uma decisão no futuro não desloca as decisões anteriores |
| Semente cifrada em custódia (AES-256-GCM) até o desmascaramento | Um dump do banco durante o estudo não revela a semente, e a semente sobrevive a um reinício |
| Campos identificáveis cifrados com AES-256-GCM, envelope `v1:` | LGPD (P6) e espaço para rotação de chave |

## 7. Dependências novas e sua justificativa (P8)

| Dependência | Por quê | Custo de manutenção |
|---|---|---|
| `spring-boot-starter-web`, `-thymeleaf`, `-data-jpa`, `-security`, `-validation`, `-actuator` | Stack de referência da Constitution | Baixo, versões alinhadas pelo parent |
| `spring-modulith-starter-core` / `-test` | Fronteiras de módulo verificadas em teste (regra arquitetural 1) | Baixo |
| `flyway-core`, `flyway-database-postgresql` | Migrações auditáveis | Baixo |
| `postgresql` (driver) | Banco de referência | Baixo |
| `io.zonky.test:embedded-postgres` + binários `linux-amd64` e `windows-amd64` (PostgreSQL 18.6, a versão de produção) | **Substitui o Testcontainers e o Docker Compose**: PostgreSQL real em desenvolvimento e teste sem container, nos dois sistemas operacionais | Médio: é preciso acompanhar a versão dos binários do PostgreSQL. Aceito porque remove a dependência de Docker, exigida pela regra arquitetural 8. **Os binários 17.x falham no `initdb` em Windows** (`coluna "attnum" não existe`); a versão está fixada em 16.9.0 e só deve subir depois de testada nos dois sistemas |
| `org.bouncycastle:bcpkix-jdk18on` 1.87 (traz `bcprov` e `bcutil`) | Carimbo de tempo RFC 3161 (SPEC 0.15, E8): montar o pedido e, sobretudo, **conferir o token** — estrutura CMS assinada, ASN.1, `messageImprint`, *nonce* e certificado do signatário. O JDK não tem API para nenhuma das três coisas, e reescrever um verificador de CMS seria o oposto de "maduro e chato" (P8). É a biblioteca de referência do ecossistema Java para isso, mantida há mais de 20 anos | Baixo: uma dependência, versão fixada em `bouncycastle.version`. Usada só no módulo `anchor`; a criptografia do resto do projeto continua JCA puro |
| `spring-boot-starter-mail` (Jakarta Mail) | Confirmação de conta e avisos do P1 por SMTP externo (SPEC E13, E10 trava 3). É o cliente de e-mail do próprio Spring, sem serviço adicional: em desenvolvimento nada é enviado, em produção basta um SMTP | Baixo, versão alinhada pelo parent |
| Nenhuma dependência para o site estático (SPEC 1.3) | O gerador usa o Thymeleaf que já vem com o starter e o `PathMatchingResourcePatternResolver` do Spring; a publicação usa só as ações oficiais do GitHub Pages, no workflow | Nenhum na aplicação |
| Nenhuma biblioteca de estatística | A análise primária é um teste binomial exato, escrito em aritmética racional (~40 linhas) e validado contra valores de referência | Nenhum |
| Nenhuma biblioteca de JSON para hashing | A forma canônica é escrita no projeto (`CanonicalJson`), porque o hash não pode mudar quando uma biblioteca muda de versão | Nenhum |

Jackson entra apenas como já parte do starter web, e só é usado para **ler** a entrada do
sorteio na CLI, nunca para calcular hash.

## 8. Testes

| Teste | Princípio | Tipo |
|---|---|---|
| `AuditTrailIntegrationTest` | P4 | PostgreSQL real: cadeia verificável, UPDATE/DELETE recusados, linha forjada detectada |
| `PreregistrationIntegrationTest` | P2, P5 | PostgreSQL real: congelamento, imutabilidade, emenda, sorteio único, revelação da semente |
| `AllocationEngineTest` | P5 | Unitário: reprodutibilidade, independência da ordem de entrada, compromisso ligado às alocações |
| `BinomialTestTest` | P9 | Unitário: valores de referência exatos (conferem com `binom.test` do R) |
| `ConfirmatoryAnalysisPolicyTest` | P2 | Unitário: parada antes e depois do alvo, pré-registro não congelado e unidade dependente viram exploratória |
| `IndependentUnitsTest` | P9 | Unitário: uma escolha por par, a do consulente sorteado; dado faltante interrompe em vez de encolher a amostra |
| `ClueScrubberTest` | P1 | Unitário: primeiro nome, pistas de sessão, datas, item com mais de uma afirmação |
| `BlindingMatrixTest` | P1 | Estrutural: campo proibido ausente da projeção, não filtrado na view |
| `PairedPrimaryMeasureTest` | P9 | Wilcoxon exato contra valores de referência; par duplicado recusado |
| `PowerCheckTest` | P9 | A tabela de poder da seção 4, como teste |
| `PermutationTestTest` | P9 | Permutação exata; reproduz o resultado nulo publicado por O'Keeffe e Wiseman |
| `ItemScoreEntryTest` | P1, E6 | Justificativa obrigatória nos valores interpretativos; só o encaixe óbvio conta |
| `ModularityTest` | Regra 1 e 2 | Fronteiras de módulo |
| `AccountIntegrationTest` | P6, E13 | PostgreSQL real: confirmação de uso único e com prazo, perfil fixo e `DELETE` recusados pelo banco, um e-mail por conta, nada pessoal na trilha, exclusão que mantém o código |
| `QuestionnaireTest` | E13 | Unitário: campos de pareamento iguais aos do P1 e fora da nota, "não sei" só onde o pareamento não precisa, resposta incompleta ou desconhecida recusada |
| `ConsultationIntegrationTest` | P1, P4, P6, E13 | PostgreSQL real: pedido nasce travado, compromisso recalculável e sensível a qualquer resposta, trilha só com o compromisso, detalhe cifrado, banco recusa alterar e apagar, um pedido ativo por consulente, desistência apaga o detalhe |
| `ConsultationPagesTest` | P1, E13 | Segurança real: preencher, rever, travar e ver; o pedido não aparece para outro consulente nem para o administrador; o médium não alcança as telas |
| `StructuredPairingTest` | P5, E13 | Unitário: mesmo estrato, ordem de travamento, nunca dois pedidos do mesmo consulente, mesmo resultado em qualquer ordem de entrada, limite de N |
| `StructuredStudyIntegrationTest` | P1, P2, P4, P5, E13 | PostgreSQL real: anexo e compromisso da semente no documento, um estudo por vez, nome só para o médium sorteado, trilha sem código de médium, sorteio refeito a partir da semente e das entradas registradas, leitura com compromisso recalculável e imutável, prazo vencido vai a outro médium, retirada desfaz o par, banco recusa reescritas |
| `MediumReadingPagesTest` | P1, E13 | Segurança real: ver os nomes, responder, rever e travar; outro médium, o consulente e o administrador não veem os nomes |
| `StructuredStudyPagesTest` | P2, E13 | Preencher, rever com o poder declarado e congelar; questionário em rascunho não congela onde rascunho não vale |
| `StructuredScoringTest` | P10, E13 | Unitário: campos comparáveis, "não sei" fora para os dois, "não recebi" nunca acerta, respostas iguais empatam |
| `StratifiedPermutationTestTest` | P5, E13 | Unitário: só permuta dentro do estrato, um estrato equivale ao teste comum, amostragem reproduzível pela semente |
| `P1UnblindingTest` | P1, P4, P5, P7, E13 | Ponta a ponta, em banco próprio: antes da última leitura não há nota nem relatório, nem por SQL; no N o estudo se desmascara sozinho; os `d`, os três *p* e a integridade são os esperados; o hash do relatório está na trilha; a devolutiva de cada um mostra só o que é dele |
| `AccountSignalsTest` | P6, E13 | Unitário: e-mail canônico (caixa, "+", pontos do Gmail) e nome normalizado; uma palavra só não vira sinal |
| `LinkedAccountsIntegrationTest` | P4, P6, E13 | PostgreSQL real: homônimos sinalizados sem bloqueio, trilha sem as contas, decisão única, apelidos do Gmail, desistência apaga os sinais, banco recusa reescrever ou apagar |
| `LinkedAccountsPagesTest` | E13 | O administrador vê códigos e sinal, sem nome, e decide uma vez; só ele alcança a tela |
| `AccountPagesTest` | P1, E13 | Segurança real: do cadastro ao painel, cada perfil só alcança o próprio painel, senha errada não revela conta não confirmada, cadastro repetido não revela nada |
| `TargetDerivationTest` | P5 | Unitário: alvo reproduzível pela semente, dentro do intervalo, candidatas distintas e banco pequeno recusado |
| `ProtocolCatalogTest` | SPEC 1.1 | Unitário: todo protocolo declara o que não sustenta, e protocolo sem limites não compila em tempo de execução; a numeração da SPEC 0.17, com rótulos, seções AIR e ESP e os códigos das tentativas iguais aos do catálogo |
| `TrialSessionIntegrationTest` | E10 trava 1, E11, E12 | PostgreSQL real: semente só no fim, alvo ausente do banco antes do travamento, impressão não reescrita, tentativa travada imutável, fechamento idempotente, **nada acontece antes da entropia do participante — recusado no serviço e pelo *trigger*** |
| `TargetDerivationTest` (semente conjunta) | E11 | Unitário: vetor de teste das duas metades, e a ordem das metades faz parte da regra |
| `TrialRunControllerTest` | E11, E12 | Páginas: passo da entropia antes de qualquer tentativa, reenvio retoma o teste, coluna de figuras só onde há figuras; **`/protocolos` lista os quatro com links para `/protocolo/P1` a `/protocolo/P4`, as rotas `/testes/...` redirecionam com 301 e as páginas dos testes ficam na seção Protocolos** (SPEC 1.1, regra 7) |
| `ProtocolQuestionnaireTest` | E13 | Página pública do P1: o questionário em vigor campo a campo, com opções, o que entra na nota e por que; só na página do P1; o painel do administrador leva a ele |
| `TimestampProtocolTest` | E8 | Unitário, contra uma autoridade de teste com assinatura real (`TestAuthority`): resposta concedida confere; outro hash, outro *nonce*, outra autoridade, recusa e resposta ilegível são recusados; token guardado só confere com o próprio hash, e um byte adulterado o derruba |
| `TrustAnchorsTest` | E8 | Os certificados do FreeTSA no repositório conferem com os SHA-256 publicados e formam cadeia; arquivo fora da fixação, outra raiz e certificado sem o uso crítico de carimbo são recusados |
| `AnchorIntegrationTest` | P4, P7, E8 | PostgreSQL real, em banco próprio: marco carimbado na verificação seguinte sobre o hash da cabeça e registrado na trilha; mudança comum espera o intervalo; trilha parada não é recarimbada; falha fica fora da trilha e espera para repetir; resposta de outra autoridade ou recusa nunca vira carimbo; banco recusa reescrever, apagar e hash que não é o do evento; a CLI pega token que carimba outro valor; `/carimbos` e o `.tsr` são públicos |
| `FreeTsaLiveTest` | E8 | Ida e volta real ao FreeTSA com os certificados fixados. Só roda com `AIRLAB_LIVE_TSA=true`, porque o build não pode depender de terceiro no ar |

Os testes de integração usam o mesmo PostgreSQL embarcado, então `./mvnw test` funciona em
Windows e Linux sem Docker e sem banco instalado.

## 9. Armadilhas conhecidas

1. **Binários do PostgreSQL embarcado**: fixados em 18.6.0, a mesma versão principal da produção
   (Constitution, regra de implantação 4), conferida pelo `EmbeddedPostgresVersionTest`. Os 17.x
   quebravam o `initdb` no Windows; os 18.x não. Ao mudar de versão principal, um cluster antigo em
   `~/.airlab/pgdata` ou na pasta temporária dos testes não abre: a aplicação recusa com uma mensagem
   que manda apagar o diretório (os dados ali são fictícios) ou apontar outro.
2. **Diretório do cluster**: nunca aponte `airlab.local-database.data-directory` para pasta
   sincronizada (Google Drive, OneDrive) nem para disco de rede — o `initdb` falha ou o cluster
   corrompe. O padrão é `~/.airlab/pgdata`, e os testes usam o diretório temporário do sistema.
3. **Precisão de instante na trilha**: o `occurredAt` é truncado a microssegundos antes de
   entrar no hash, porque é essa a precisão que o PostgreSQL grava. Sem isso, a releitura
   recomputa outro hash e a cadeia parece quebrada.
4. **Tamanho do JAR**: ~150 MB, porque carrega os binários do PostgreSQL das duas plataformas.
   Aceitável para implantação por `scp`; se incomodar, separe um perfil Maven que exclua os
   binários no artefato de produção.

5. **`target/` dentro de pasta sincronizada**: o JAR tem ~150 MB e o `target/` fica no Google
   Drive junto com o repositório. O Drive segura o arquivo enquanto sincroniza, e o
   `spring-boot:repackage` falha com `Unable to rename ... airlab.jar`. E não é só o JAR: já
   aconteceu de `mvn clean` não conseguir apagar `target/classes` e de uma compilação enxergar
   classes principais ausentes que estavam lá. Sintoma a reconhecer: erro de I/O ou símbolo não
   encontrado que **some ao construir noutro lugar**.

   Contorno imediato: apagar `target/airlab.jar` e repetir. Contorno confiável, e o único que
   resolveu os três casos:

   ```
   ./mvnw -Dmaven.build.directory=C:/temp/airlab-target test
   ```

   Correção de verdade: manter o repositório num disco local. O `target/` é gerado, não deveria
   estar sincronizado, e um JAR de 150 MB subindo para a nuvem a cada build é desperdício além
   de risco.
6. **Parar a aplicação pelo gerenciador de tarefas deixa o cluster travado**: o
   `epg-lock` em `~/.airlab/pgdata` fica preso pela JVM, e a próxima execução falha com
   `could not lock`. Encerre com `Ctrl+C` no terminal do `start.sh`, que roda o desligamento
   limpo. Se já aconteceu, encerre o processo `java -jar target/airlab.jar` e o `postgres.exe`
   correspondente — e **só esses**: é fácil derrubar junto o servidor de linguagem Java do IDE,
   que também é `java.exe`.

7. **Um cluster por JVM, não por contexto Spring**: a suíte carrega vários contextos no mesmo
   processo, e cada um tentando subir o próprio PostgreSQL embarcado na mesma porta falha com
   `could not lock`. Por isso `LocalDatabaseConfiguration` compartilha a instância no processo e
   a encerra por *shutdown hook*, não por fechamento de contexto. Ao criar um teste que force
   contexto novo (por exemplo com `@MockitoBean`), nada precisa ser feito — mas não volte a
   registrar `destroyMethod` no bean.

8. **O GitHub Pages não faz redirecionamento nem manda cabeçalhos** (SPEC 1.3): nada de 301 e
   nada de CSP por cabeçalho. Por isso as páginas do site levam a CSP numa `<meta>`, e o
   `404.html` encaminha para a aplicação por um script em arquivo à parte, que a CSP
   `default-src 'self'` permite e um script embutido não. O `frame-ancestors` não vale em
   `<meta>`, o que é aceitável em páginas sem formulário. Uma página do site **não pode** usar
   dado do modelo além de `site`, `aplicacao`, `secaoAtual` e `logado` (e a 404, `paginasDoSite`), porque o gerador não
   tem mais nada para entregar: o `StaticSiteTest` falha se uma tentar. E o script da 404 nunca
   encaminha um caminho que é página do site: com `AIRLAB_SITE_URL`, a aplicação o mandaria de
   volta com 301, e as duas pontas ficariam mandando a pessoa uma para a outra.


## 10. Próximos cortes

Só o P1, desde a SPEC 0.14. O P2 está descontinuado e fica no site apenas como referência; os
módulos dele não recebem mais código.

1. **E8, ancoragem externa**: ~~fase 1, FreeTSA~~ — feita na 1.9. Falta decidir se o primeiro
   envio ao médium espera o carimbo do congelamento (SPEC, questão 8a) e a fase 2, OpenTimestamps,
   para não depender de uma autoridade só (questão 8b).
2. **E9, pacote de verificação do P1**: ~~envios, listas de elegíveis, leituras e a conferência
   refeita por fora~~ — feito na 1.13, público e sem nome (SPEC 0.22).
3. **Questões em aberto do P1** (SPEC, seção 9): 9 (revisão do questionário), 11 (qualificação de
   médiuns) e 12 (valor do teto num estudo real).
4. ***Dry-run*** do P1 com participantes fictícios.
