-- V25: o idioma da conta (SPEC 0.28, secao 1.5, regra 8).
--
-- A conta guarda em que idioma a pessoa a criou, para que os e-mails que saem fora de uma
-- requisicao -- o pedido de leitura ao medium, o aviso de prazo, a devolutiva -- saiam no idioma em
-- que ela leu o termo. Dentro de uma requisicao o idioma vem da propria pagina, e esta coluna nao e
-- consultada.
--
-- Fica na linha da conta, em identity.account (V17), porque e dela que se trata; so o modulo
-- participant le este esquema (regra arquitetural 2), e e ele que entrega o idioma ao modulo de
-- notificacao, junto com o endereco. Nao e dado pessoal novo: e preferencia de apresentacao, nao
-- entra na trilha e nao e variavel do estudo (regra 10 -- o idioma nao toca cegamento, pareamento
-- nem analise). Nao e cifrada, pelo mesmo motivo: nao identifica ninguem.
--
-- Os dois valores possiveis sao os dois idiomas da interface; o padrao e o portugues, idioma de
-- origem, que e o que toda conta criada antes desta migracao usou.

ALTER TABLE identity.account
    ADD COLUMN language varchar(2) NOT NULL DEFAULT 'pt';

ALTER TABLE identity.account
    ADD CONSTRAINT account_language_check CHECK (language IN ('pt', 'en'));

COMMENT ON COLUMN identity.account.language IS
    'Idioma da interface escolhido na criacao da conta (pt ou en); preferencia de apresentacao, '
    'nao dado pessoal e nao variavel do estudo (SPEC 0.28, secao 1.5).';
