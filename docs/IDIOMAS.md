# Idiomas — como o Crivo fala português e inglês

> Derivado de `CONSTITUTION.md` (regra de idioma, v1.7.0) e de `SPEC.md`, seção 1.5, que prevalecem.
> Este documento é operacional: diz como escrever e revisar a tradução, e o que nunca se traduz.

O português é o **idioma de origem**: é nele que o texto é escrito, discutido e revisado. O inglês é
**tradução dele**, e nunca diz nada que o português não diga. Quando os dois divergirem, o português
está certo e o inglês está errado — não o contrário.

## 1. Onde fica cada coisa

| O que | Onde |
|---|---|
| Página em português | `templates/<caminho>.html` |
| A mesma página em inglês | `templates/en/<mesmo caminho>.html` |
| Texto compartilhado (menu, rodapé, seletor, botões) | `messages.properties` (pt) e `messages_en.properties` (en) |
| Texto que vem do código (catálogo, questionário) | `support.Text`, com os dois idiomas lado a lado |
| Datas e prazos por extenso | `support.BrasiliaTime`, por idioma |

Sem o template em inglês, o endereço `/en/...` responde **em português**, com um aviso de uma linha
(seção 1.5, regra 5). Isso é proposital: é melhor do que uma tradução não revisada. Não existe
tradução automática em produção.

## 2. Ao traduzir uma página

1. **Copie o template português** para `templates/en/` com o mesmo nome e traduza só a prosa.
2. **Não mexa em nada de Thymeleaf**: `th:*`, `${...}`, `#{...}`, `~{fragments/...}`, nomes de
   variável, condições, `th:each`. Um `${protocolo.name().in(idioma)}` fica como está.
3. **Não mexa em estrutura nem em estilo**: as mesmas tags, classes, `id`, âncoras (`#conteudo`,
   `#questionario`) e a mesma ordem das seções. A tradução não é redesenho.
4. `<html lang="en">` no lugar de `lang="pt-BR"`.
5. **Traduza os argumentos do fragmento `social(...)`**, que são o título e a descrição da página.
6. **Nunca escreva `/en` num link.** O prefixo de idioma entra sozinho: todo link interno começa com
   `${aplicacao}`, com `${site}` ou é um `@{...}`, e esses três já estão no idioma da página.
7. **Inglês americano** (*randomized*, *behavior*), que é o da literatura da área.
8. Um termo português que precise aparecer no texto em inglês vai em `<em lang="pt-BR">`.

## 3. O que não se traduz

- **Nomes**: Crivo, PsiGama, AIR Lab, Rogerio Cisi.
- **Identificadores públicos**: o código de um protocolo (`P1`) e o rótulo dele (`P1-automático`),
  que entram em compromissos, relatórios e na trilha; a versão de um questionário (`v3-rascunho`);
  o código de um termo (`conta-v4`); chaves de campo e de opção do questionário.
- **Endereços**: caminhos, URLs, `mailto:`.
- **Código**: nomes de classe, de método, de variável de ambiente, comandos da CLI.
- **Citações bibliográficas**: autor, ano, título e veículo ficam como estão.

## 4. Glossário

A coluna da esquerda é o termo do projeto em português; a da direita, o que se escreve em inglês. O
vocabulário continua neutro quanto à origem da informação (Constitution P3): nada de *spirit said*.

| Português | Inglês | Observação |
|---|---|---|
| consulente | sitter | É o termo da literatura (glossário da SPEC); em português usamos consulente |
| médium | medium | |
| leitura | reading | |
| item | item | |
| alvo | target | |
| chamariz | decoy | Nas telas de participante, *person for comparison* |
| nota, pontuação | score | |
| campo | field | |
| questionário fechado | closed questionnaire | |
| acaso (nível de) | chance level | Declarado, nunca estimado |
| pareamento | pairing | |
| estrato | stratum (plural *strata*) | |
| pré-registro | preregistration | |
| congelar | freeze | |
| emenda | amendment | |
| cegamento | blinding | |
| desmascaramento | unblinding | Nas telas de participante, *opening of the results* |
| lacre digital | digital seal | Camada simples de *compromisso criptográfico* |
| compromisso criptográfico | cryptographic commitment | |
| registro público | public record | Camada simples de *trilha de auditoria* |
| trilha de auditoria | audit trail | |
| carimbo de tempo | timestamp | |
| pacote de verificação | verification package | |
| regra de parada | stopping rule | |
| sorteio | draw, randomization | *Sorteio reproduzível*: reproducible draw |
| semente | seed | |
| estudo | study | |
| experimento | experiment | Roda em estudo pré-registrado, com conta |
| teste | test | Roda em sessão anônima, com resultado na hora |
| protocolo | protocol | |
| pedido (de consulta) | request | |
| devolutiva | feedback | |
| painel | dashboard | |
| conta | account | |
| termo de participação | participation agreement | |
| inscrição aberta/fechada | sign-up open/closed | |
| sinalização (de contas ligadas) | flag | |
| desvio de protocolo | protocol deviation | |
| cognição anômala | anomalous cognition | |
| recepção anômala de informação | anomalous information reception | |
| CEP, Comitê de Ética em Pesquisa | research ethics committee | Na primeira menção, *Brazilian research ethics committee (CEP)* |
| LGPD | Brazilian data protection law (LGPD) | Na primeira menção |
| horário de Brasília | Brasília time | |
| CVV | CVV, Brazil's emotional support helpline | Mantém o nome e explica |

## 5. Convenções de número e de formato

- **Separador decimal**: ponto em inglês, vírgula em português. Nos templates, o
  `#numbers.formatDecimal(..., 'COMMA')` do português vira `'POINT'` na versão em inglês. É a única
  expressão Thymeleaf que legitimamente difere entre as duas versões de uma página.
- **Data e hora**: `support.BrasiliaTime` escreve nos dois idiomas, cada um na sua ordem ("27 de
  novembro, às 7h" e "November 27, at 7 a.m."), sempre no horário de Brasília. Nos templates, use
  `@datas.prazo`, `@datas.momento` e `@datas.data`, que já saem no idioma da página; `@datas.utc`
  fica para as páginas de auditoria e de administração, que dizem "UTC".
- **Texto que vem do código** sai com `.in(idioma)`, como em `${protocolo.name.in(idioma)}` ou
  `${campo.label().in(idioma)}`. A variável `idioma` está em toda página, na aplicação e no gerador.
- **Citação bibliográfica** mantém os números como estão, inclusive a vírgula decimal, porque é
  citação.

## 6. Ao acrescentar texto novo

- Texto de interface **no código** nasce com os dois idiomas (`Text.of(pt, en)`), senão não compila.
- Chave nova em `messages.properties` entra também em `messages_en.properties`.
- Página nova nasce em português; a versão em inglês pode vir depois, e até vir o aviso da regra 5
  aparece. O que **não** pode é um link para um endereço que não existe.
- Mudança de texto em português numa página já traduzida: a tradução muda no mesmo commit.
