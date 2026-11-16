-- V24: pacote de verificacao do P1 (SPEC 0.22, E9).
--
--   research.structured_package   o pacote publico de um estudo P1: documento JSON canonico, com
--                                 hash, escrito uma vez, no desmascaramento, logo depois do relatorio.
--
-- O que o banco garante, como no relatorio (V20): nenhum pacote existe antes do desmascaramento, e
-- nenhuma linha muda nem some depois de escrita. O hash do documento vai a trilha
-- (VERIFICATION_PACKAGE_EXPORTED).

CREATE TABLE research.structured_package (
    study_id         BIGINT       PRIMARY KEY REFERENCES research.structured_study (study_id),
    document         TEXT         NOT NULL,
    document_sha256  CHAR(64)     NOT NULL,
    created_at       TIMESTAMPTZ  NOT NULL,
    CONSTRAINT ck_structured_package_sha CHECK (document_sha256 ~ '^[0-9a-f]{64}$')
);

COMMENT ON TABLE research.structured_package IS
  'Pacote de verificacao publico do estudo P1 (E9): sem primeiro nome e sem sal do pedido. So existe depois do desmascaramento.';

CREATE TRIGGER trg_structured_package_unblinded_only
    BEFORE INSERT OR UPDATE OR DELETE ON research.structured_package
    FOR EACH ROW EXECUTE FUNCTION research.structured_unblinded_only();

GRANT SELECT, INSERT ON research.structured_package TO airlab_app;
