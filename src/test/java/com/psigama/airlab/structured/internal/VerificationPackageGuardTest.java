package com.psigama.airlab.structured.internal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.psigama.airlab.catalog.Questionnaire;
import com.psigama.airlab.catalog.Questionnaires;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * SPEC E9: the export is refused when anything outside the closed list appears. The guard is what
 * keeps a future change from slipping a name, an e-mail or free text into the public package.
 */
class VerificationPackageGuardTest {

    private static final Questionnaire QUESTIONNAIRE = Questionnaires.current();
    private static final Questionnaire.Field FIELD = QUESTIONNAIRE.scoredFields().get(0);

    private static Map<String, Object> request(Map<String, Object> extra, String option) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("code", "CON-7Q4K2M9B");
        request.put("stratum", "feminino|60-79|1-5-anos");
        request.put("answers", Map.of(FIELD.key(), option));
        request.putAll(extra);
        Map<String, Object> document = new LinkedHashMap<>();
        document.put("format", "crivo-pacote-p1/1");
        document.put("requests", List.of(request));
        return document;
    }

    @Test
    @DisplayName("um pacote so com chaves da lista e opcoes do questionario passa")
    void a_package_within_the_list_passes() {
        assertThatCode(() -> VerificationPackageWriter.guard(
                request(Map.of(), FIELD.options().get(0).key()), QUESTIONNAIRE)).doesNotThrowAnyException();
        assertThatCode(() -> VerificationPackageWriter.guard(
                request(Map.of(), Questionnaire.UNKNOWN), QUESTIONNAIRE)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("uma chave fora da lista, como o primeiro nome, recusa a exportacao")
    void a_key_outside_the_list_is_refused() {
        assertThatThrownBy(() -> VerificationPackageWriter.guard(
                request(Map.of("firstName", "Aurora"), FIELD.options().get(0).key()), QUESTIONNAIRE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("requests[].firstName")
                .hasMessageNotContaining("Aurora");
        assertThatThrownBy(() -> VerificationPackageWriter.guard(
                request(Map.of("salt", "ab"), FIELD.options().get(0).key()), QUESTIONNAIRE))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("uma resposta que nao e opcao do questionario recusa a exportacao")
    void an_answer_outside_the_options_is_refused() {
        assertThatThrownBy(() -> VerificationPackageWriter.guard(
                request(Map.of(), "minha avo Aurora"), QUESTIONNAIRE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageNotContaining("Aurora");
    }

    @Test
    @DisplayName("texto livre ou e-mail onde se espera um codigo recusa a exportacao")
    void free_text_where_a_code_belongs_is_refused() {
        Map<String, Object> document = request(Map.of(), FIELD.options().get(0).key());
        document.put("trail", List.of(Map.of("actorCode", "aurora@exemplo.test")));
        assertThatThrownBy(() -> VerificationPackageWriter.guard(document, QUESTIONNAIRE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("trail[].actorCode")
                .hasMessageNotContaining("aurora");
    }
}
