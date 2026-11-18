# paper/ — Artigo técnico do Crivo

| Arquivo | Conteúdo |
|---|---|
| `artigo-tecnico.pt-BR.md` | Artigo de desenho, em português, em Markdown do Pandoc (esboço) |
| `referencias.bib` | As 34 referências citadas no artigo, em BibTeX |
| `poder/SimulacaoPoder.java` | Simulação de poder do P1 (seção 4.6), só com o JDK: `java paper/poder/SimulacaoPoder.java` |
| `poder/resultados.txt` | A saída da simulação, com a semente fixa |

## Relação com a página publicada

O mesmo texto está publicado em [crivo.psigama.com/artigo/](https://crivo.psigama.com/artigo/), a partir de `src/main/resources/templates/site/artigo.html`. As duas versões andam juntas: quem muda uma muda a outra. Diferenças só de forma:

- as citações aqui são chaves do Pandoc (`[@silva2022]`), e a lista de referências é gerada do `referencias.bib`;
- os trechos **a fazer**, que no site aparecem em caixas, aqui ficam em comentários HTML, fora do PDF, e as pendências antes da submissão ficam nas notas de redação, no topo do arquivo;
- as caixas explicativas ("Em termos simples", o exemplo da conferência) são *divs* do Pandoc com a classe `quadro`.

## Tipo de artigo

**Artigo de desenho (design paper):** apresenta os requisitos, o protocolo, a arquitetura, as garantias e os limites do Crivo. O texto **não faz nenhuma afirmação empírica nem de desempenho**: nenhum estudo com participantes reais foi conduzido.

## Gerar PDF ou HTML

```bash
cd paper
pandoc artigo-tecnico.pt-BR.md --citeproc -o artigo-tecnico.pt-BR.pdf    # requer LaTeX
pandoc artigo-tecnico.pt-BR.md --citeproc -s -o artigo-tecnico.pt-BR.html
```

## Possíveis destinos (avaliar quando o texto amadurecer)

- **Preprint em português:** SciELO Preprints ou OSF Preprints, para registrar a precedência do desenho.
- **Revistas da área (em inglês):** *Journal of Scientific Exploration* e *Explore*, onde está boa parte da literatura citada.
- **Artigo de software (em inglês):** *Journal of Open Source Software* (JOSS).

## Pendências principais

- [ ] Ler o texto integral das referências ainda conferidas só pelo resumo e conferir cada número citado
- [ ] Levantamento sistemático de ferramentas existentes (seção 2.5), com protocolo registrado
- [x] Efeito esperado e cálculo de poder (seção 4.6, com a simulação em `poder/`)
- [x] Revisão do questionário: `v2-rascunho`, com os critérios escritos (seção 4.1; SPEC, anexo do E13)
- [ ] Revisão do questionário por pesquisadores da área e entrevista cognitiva com consulentes
- [ ] Frequências observadas das opções, por estrato, no lugar das supostas na simulação de poder
- [x] Normas de ética em pesquisa aplicáveis (seção 7)
- [ ] Conferir com o comitê a resolução aplicável (466 ou 510) e as normas novas da Lei 14.874/2024
- [ ] Ensaio completo e auditoria externa (seção 8)
- [x] Afiliação do autor e abstract em inglês
- [ ] ORCID e e-mail de correspondência do autor
- [ ] Discussão (seção 9), depois do ensaio e da auditoria
