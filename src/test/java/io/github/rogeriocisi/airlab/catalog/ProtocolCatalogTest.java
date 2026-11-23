package io.github.rogeriocisi.airlab.catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.rogeriocisi.airlab.support.Text;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Catalog rules of SPEC section 1.1, as tests. Rule 2 in particular — every
 * protocol states
 * what its result does not support — is the kind of promise that quietly
 * disappears from a
 * page unless something fails when it does.
 */
class ProtocolCatalogTest {

    @Test
    @DisplayName("o catalogo tem os quatro protocolos, com codigos e slugs unicos")
    void the_catalog_has_four_protocols() {
        List<Protocol> all = ProtocolCatalog.all();

        assertThat(all).hasSize(4);
        assertThat(all.stream().map(Protocol::code)).containsExactly("P1", "P2", "P3", "P4");
        assertThat(all.stream().map(Protocol::label))
                .containsExactly("P1-automático", "P2-supervisionado", "P3-precognição", "P4-visão-remota");
        assertThat(all.stream().map(Protocol::slug)).doesNotHaveDuplicates();
        assertThat(all.stream().map(Protocol::name)).doesNotHaveDuplicates();
    }

    @Test
    @DisplayName("todo protocolo declara o que o resultado dele nao sustenta")
    void every_protocol_states_its_limits() {
        for (Protocol protocol : ProtocolCatalog.all()) {
            assertThat(protocol.limits())
                    .describedAs("limites de %s", protocol.code())
                    .isNotEmpty();
            protocol.limits().forEach(limit -> {
                assertThat(limit.pt()).describedAs("limite de %s", protocol.code())
                        .isNotBlank().hasSizeGreaterThan(20);
                // SPEC 0.28, secao 1.5, regra 6: o limite declarado existe nos dois idiomas, e a
                // traducao nao e o portugues repetido.
                assertThat(limit.en()).describedAs("limite de %s em ingles", protocol.code())
                        .isNotBlank().hasSizeGreaterThan(20).isNotEqualTo(limit.pt());
            });
        }
    }

    @Test
    @DisplayName("um protocolo sem limites declarados nao pode ser construido")
    void a_protocol_without_limits_cannot_exist() {
        assertThatThrownBy(() -> new Protocol("P9", "x", Text.of("X", "X"), "x", Protocol.Branch.ESP, 1, 4,
                Protocol.Automation.FULL, Protocol.Status.PLANNED, Text.of("resumo", "summary"),
                List.of(), "E9"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not support");
    }

    @Test
    @DisplayName("os testes de ESP avisam que nao dizem nada sobre mediunidade")
    void the_esp_protocols_say_they_are_not_about_mediumship() {
        for (Protocol protocol : List.of(ProtocolCatalog.precognitiveForcedChoice(),
                ProtocolCatalog.remoteViewing())) {
            assertThat(limitsOf(protocol))
                    .describedAs("limites de %s", protocol.code())
                    .contains("mediunidade");
        }
    }

    @Test
    @DisplayName("a leitura pareada avisa que nao distingue sobrevivencia de telepatia")
    void the_paired_reading_states_its_central_limit() {
        assertThat(limitsOf(ProtocolCatalog.pairedReading())).contains("telepatia");
    }

    @Test
    @DisplayName("o acaso declarado e uma probabilidade, e o percentual confere")
    void chance_is_a_declared_probability() {
        assertThat(ProtocolCatalog.pairedReading().chancePercent()).isEqualTo(50);
        assertThat(ProtocolCatalog.precognitiveForcedChoice().chancePercent()).isEqualTo(25);
        assertThat(ProtocolCatalog.remoteViewing().chancePercent()).isEqualTo(25);
        assertThat(ProtocolCatalog.structuredReading().chancePercent()).isEqualTo(50);

        assertThatThrownBy(() -> new Protocol("P9", "x", Text.of("X", "X"), "x", Protocol.Branch.ESP, 4, 4,
                Protocol.Automation.FULL, Protocol.Status.PLANNED, Text.of("resumo", "summary"),
                List.of(Text.of("um limite declarado com tamanho suficiente", "a declared limit, long enough")),
                "E9"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("chance");
    }

    @Test
    @DisplayName("SPEC 0.17: P1 automatico e P2 supervisionado no ramo AIR; P3 e P4 no ramo ESP")
    void the_renumbered_catalog() {
        assertThat(ProtocolCatalog.structuredReading().code()).isEqualTo("P1");
        assertThat(ProtocolCatalog.pairedReading().code()).isEqualTo("P2");
        assertThat(ProtocolCatalog.precognitiveForcedChoice().code()).isEqualTo("P3");
        assertThat(ProtocolCatalog.remoteViewing().code()).isEqualTo("P4");
        assertThat(ProtocolCatalog.air().stream().map(Protocol::code)).containsExactly("P1", "P2");
        assertThat(ProtocolCatalog.esp().stream().map(Protocol::code)).containsExactly("P3", "P4");
        assertThat(ProtocolCatalog.air()).allMatch(p -> p.branch() == Protocol.Branch.AIR);
        assertThat(ProtocolCatalog.esp()).allMatch(p -> p.branch() == Protocol.Branch.ESP);
        assertThat(ProtocolCatalog.air().size() + ProtocolCatalog.esp().size())
                .isEqualTo(ProtocolCatalog.all().size());
    }

    @Test
    @DisplayName("SPEC 0.18: os protocolos AIR sao experimentos; os de ESP, testes")
    void experiments_and_tests() {
        assertThat(ProtocolCatalog.air()).allMatch(p -> p.kind().pt().equals("Experimento"));
        assertThat(ProtocolCatalog.esp()).allMatch(p -> p.kind().pt().equals("Teste"));
        assertThat(ProtocolCatalog.structuredReading().branch().kindLabel().pt())
                .isEqualTo("Experimento de mediunidade (AIR)");
        assertThat(ProtocolCatalog.remoteViewing().branch().kindLabel().pt())
                .isEqualTo("Teste de percepção extrassensorial (ESP)");
        // SPEC 0.28, secao 1.5, regra 6: o mesmo texto em ingles, e nenhum deles e o portugues.
        assertThat(ProtocolCatalog.air()).allMatch(p -> p.kind().en().equals("Experiment"));
        assertThat(ProtocolCatalog.esp()).allMatch(p -> p.kind().en().equals("Test"));
        assertThat(ProtocolCatalog.structuredReading().branch().kindLabel().en())
                .isEqualTo("Mediumship experiment (AIR)");
    }

    @Test
    @DisplayName("os codigos das tentativas seguem o catalogo: P3 e P4")
    void the_trial_codes_follow_the_catalog() {
        assertThat(io.github.rogeriocisi.airlab.trials.TrialProtocol.PRECOGNITIVE_FORCED_CHOICE.catalogCode())
                .isEqualTo(ProtocolCatalog.precognitiveForcedChoice().code());
        assertThat(io.github.rogeriocisi.airlab.trials.TrialProtocol.REMOTE_VIEWING_RANKED.catalogCode())
                .isEqualTo(ProtocolCatalog.remoteViewing().code());
    }

    @Test
    @DisplayName("os testes de ESP estao no catalogo, com codigo, slug e limites")
    void the_esp_tests_stay_in_the_catalog() {
        assertThat(ProtocolCatalog.bySlug("escolha-forcada")).contains(ProtocolCatalog.precognitiveForcedChoice());
        assertThat(ProtocolCatalog.bySlug("visao-remota")).contains(ProtocolCatalog.remoteViewing());
        assertThat(ProtocolCatalog.esp()).allMatch(Protocol::implemented).allMatch(Protocol::quickTest);
    }

    @Test
    @DisplayName("a leitura pareada (P2) esta descontinuada: listada so como referencia")
    void the_paired_reading_is_discontinued_and_not_offered() {
        Protocol paired = ProtocolCatalog.pairedReading();
        assertThat(paired.discontinued()).isTrue();
        assertThat(paired.status().label().pt()).isEqualTo("Só referência, não implementado");
        assertThat(paired.status().label().en()).isEqualTo("Reference only, not implemented");
        assertThat(ProtocolCatalog.byCode("P2")).contains(paired);
        assertThat(paired.quickTest()).isFalse();
        assertThat(ProtocolCatalog.esp()).doesNotContain(paired);
    }

    @Test
    @DisplayName("o AIR automatizado declara a falta de proxy, o vazamento pelo nome e a telepatia")
    void the_structured_reading_states_its_limits() {
        String limits = limitsOf(ProtocolCatalog.structuredReading());
        assertThat(limits).contains("telepatia").contains("proxy").contains("primeiro nome")
                .contains("conluio");
        assertThat(ProtocolCatalog.structuredReading().automation()).isEqualTo(Protocol.Automation.FULL);
    }

    @Test
    @DisplayName("so os testes de ESP sao testes rapidos anonimos; o P1 exige conta")
    void only_esp_tests_are_quick_tests() {
        assertThat(ProtocolCatalog.all().stream().filter(Protocol::quickTest).map(Protocol::code))
                .containsExactly("P3", "P4");
    }

    @Test
    @DisplayName("busca por slug e por codigo")
    void lookup_works() {
        assertThat(ProtocolCatalog.bySlug("escolha-forcada")).contains(
                ProtocolCatalog.precognitiveForcedChoice());
        assertThat(ProtocolCatalog.byCode("P4")).contains(ProtocolCatalog.remoteViewing());
        assertThat(ProtocolCatalog.byCode("p1")).contains(ProtocolCatalog.structuredReading());
        assertThat(ProtocolCatalog.bySlug("nao-existe")).isEmpty();
    }

    /** The declared limits of a protocol, in Portuguese, joined and lowercased. */
    private static String limitsOf(Protocol protocol) {
        return protocol.limits().stream().map(Text::pt)
                .reduce("", (all, limit) -> all + " " + limit).toLowerCase();
    }
}
