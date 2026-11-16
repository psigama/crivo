-- V23: acessos do airlab_app que faltavam (Constitution P4, V10).
--
-- A V10 deu ao papel da aplicacao acesso as tabelas que existiam ate ali, e cada migracao
-- seguinte passou a conceder o das suas. Duas ficaram de fora, e nenhum teste via isso, porque
-- nos perfis local e de teste a aplicacao conecta como dona do banco. Em producao ela conecta
-- como airlab_app, e:
--   * a V12 criou research.protocol_deviation sem conceder nada: registrar um desvio falharia;
--   * a V15 concedeu as tabelas dos testes de ESP, mas nao as sequencias do BIGSERIAL: o
--     INSERT de uma sessao ou tentativa falharia com "permission denied for sequence".
-- O AppRoleGrantsTest passa a conferir isso para toda tabela e sequencia dos tres esquemas.

-- Desvio de protocolo e append-only (V12): leitura e insercao, nunca alteracao.
GRANT SELECT, INSERT ON research.protocol_deviation TO airlab_app;

GRANT USAGE, SELECT ON SEQUENCE research.protocol_deviation_id_seq,
                                research.test_session_id_seq,
                                research.test_trial_id_seq TO airlab_app;
