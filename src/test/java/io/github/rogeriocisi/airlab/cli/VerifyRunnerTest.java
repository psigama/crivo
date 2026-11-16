package io.github.rogeriocisi.airlab.cli;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.rogeriocisi.airlab.allocation.AllocationEngine;
import io.github.rogeriocisi.airlab.allocation.AllocationSnapshot;
import io.github.rogeriocisi.airlab.support.CanonicalJson;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@code airlab verify} on a P1 study: there is no plan to replay, so the command checks the
 * revealed seed against the frozen commitment instead of failing on a draw input it cannot read.
 */
class VerifyRunnerTest {

    private static final String SEED = "5361199bf02ef5ef3d7843618830ff50d4d0f98317b523c465b3a40b3c04b91c";
    private static final ObjectMapper JSON = new ObjectMapper();

    @Test
    @DisplayName("P1: a semente revelada confere com o compromisso publicado no congelamento")
    void incrementalSeedMatchesItsCommitment() throws Exception {
        String input = p1Input("STD-MWPBTZ12");
        AllocationSnapshot snapshot = snapshot(AllocationEngine.seedCommitment(SEED, input), input, SEED);

        assertThat(VerifyRunner.incremental(snapshot, JSON)).isTrue();
        assertThat(VerifyRunner.incrementalCommitmentHolds(snapshot)).isTrue();
    }

    @Test
    @DisplayName("P1: uma semente trocada num único dígito não confere com o compromisso")
    void alteredSeedDoesNotMatch() throws Exception {
        String input = p1Input("STD-MWPBTZ12");
        String altered = (SEED.charAt(0) == '0' ? "1" : "0") + SEED.substring(1);
        AllocationSnapshot snapshot = snapshot(AllocationEngine.seedCommitment(SEED, input), input, altered);

        assertThat(VerifyRunner.incremental(snapshot, JSON)).isTrue();
        assertThat(VerifyRunner.incrementalCommitmentHolds(snapshot)).isFalse();
    }

    @Test
    @DisplayName("P1: o compromisso cobre o código do estudo, e o de outro estudo não confere")
    void commitmentOfAnotherStudyDoesNotMatch() throws Exception {
        String commitment = AllocationEngine.seedCommitment(SEED, p1Input("STD-OUTRO"));
        AllocationSnapshot snapshot = snapshot(commitment, p1Input("STD-MWPBTZ12"), SEED);

        assertThat(VerifyRunner.incrementalCommitmentHolds(snapshot)).isFalse();
    }

    @Test
    @DisplayName("Um sorteio com plano (P2) não é tratado como incremental")
    void planDrawIsNotIncremental() throws Exception {
        AllocationSnapshot snapshot = snapshot("c".repeat(64), "{\"pairs\":[],\"mediums\":[]}", SEED);

        assertThat(VerifyRunner.incremental(snapshot, JSON)).isFalse();
    }

    /** The same canonical input that {@code DefaultAllocationService.commitSeed} stores. */
    private static String p1Input(String studyCode) {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("mode", AllocationEngine.INCREMENTAL_MODE);
        input.put("studyCode", studyCode);
        return CanonicalJson.write(input);
    }

    private static AllocationSnapshot snapshot(String commitment, String input, String seed) {
        return new AllocationSnapshot(1L, commitment, input, "[]", Instant.parse("2026-11-15T23:49:00Z"),
                Optional.of(seed), Optional.of(Instant.parse("2026-11-15T23:50:29Z")));
    }
}
