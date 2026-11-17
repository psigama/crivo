# paper/ — Artigo técnico do Crivo

| Arquivo | Conteúdo |
|---|---|
| `artigo-tecnico.pt-BR.md` | Artigo de desenho, em português, em Markdown do Pandoc (esboço) |
| `referencias.bib` | As 27 referências citadas no artigo, em BibTeX |

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
- [ ] Efeito esperado e cálculo de poder (seção 4.5)
- [ ] Revisão do questionário, hoje em versão de rascunho
- [ ] Normas de ética em pesquisa aplicáveis (seção 7)
- [ ] Ensaio completo e auditoria externa (seção 8)
- [ ] Afiliação do autor e abstract em inglês
- [ ] Discussão (seção 9), depois do ensaio e da auditoria
