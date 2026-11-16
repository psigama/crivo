package io.github.rogeriocisi.airlab.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.rogeriocisi.airlab.catalog.Questionnaire;
import io.github.rogeriocisi.airlab.catalog.Questionnaires;
import io.github.rogeriocisi.airlab.cli.PackageVerification;
import io.github.rogeriocisi.airlab.participant.AccountProfile;
import io.github.rogeriocisi.airlab.participant.AccountService;
import io.github.rogeriocisi.airlab.participant.ConsultationDraft;
import io.github.rogeriocisi.airlab.participant.ConsultationService;
import io.github.rogeriocisi.airlab.structured.MediumReadingService;
import io.github.rogeriocisi.airlab.structured.P1Fixture;
import io.github.rogeriocisi.airlab.structured.StructuredStudyService;
import io.github.rogeriocisi.airlab.structured.StructuredStudyTerms;
import io.github.rogeriocisi.airlab.structured.StructuredStudyView;
import io.github.rogeriocisi.airlab.structured.VerificationPackage;
import io.github.rogeriocisi.airlab.structured.VerificationPackageCheck;
import io.github.rogeriocisi.airlab.support.Hashes;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/**
 * SPEC E9 for the P1: the public verification package. It does not exist a moment before the
 * unblinding; once written it carries no name; and from the file alone — no database, no server —
 * the reference verifier redoes the trail, the draws, the reading commitments, the scoring and
 * the analysis, and catches a single altered answer.
 *
 * <p>On a database of its own, recreated on every run, for the reason given in
 * {@link P1UnblindingTest}: a study that completes needs to be the only P1 study open.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "airlab.local-database.database-name=airlab_test_pacote",
        "airlab.local-database.recreate-database=true"})
class P1VerificationPackageTest {

    private static final ObjectMapper JSON = new ObjectMapper();

    /** Distinctive names, so that finding one of them in the package can only be a leak. */
    private static final List<String> FIRST_NAMES = List.of("Zuleica", "Wanderleia", "Xisto", "Yolanda");
    private static final List<String> FULL_NAMES = List.of("Quiteria Pacoteira", "Raimunda Pacoteira",
            "Severino Pacoteiro", "Teodoro Pacoteiro", "Ubirajara Pacoteiro");

    @Autowired
    MockMvc mvc;

    @Autowired
    AccountService accounts;

    @Autowired
    ConsultationService consultations;

    @Autowired
    StructuredStudyService studies;

    @Autowired
    MediumReadingService mediumReadings;

    @Autowired
    JdbcTemplate jdbc;

    P1Fixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new P1Fixture(accounts, consultations, studies, jdbc);
    }

    private record Lock(String medium, String assignment, int position, Map<String, String> answers) {
    }

    @Test
    @DisplayName("o pacote so existe depois do desmascaramento, nao tem nome e se confere so pelo arquivo")
    void the_package_exists_only_after_the_unblinding_and_checks_out_from_the_file_alone(@TempDir Path dir)
            throws Exception {
        assertThat(studies.current()).isEmpty();
        fixture.account(AccountProfile.MEDIUM, FULL_NAMES.get(4));
        StructuredStudyView study = studies.open(new StructuredStudyTerms("Estudo do pacote", 2, 14, 0.5,
                "As leituras coincidem mais com o alvo.", "Qualquer medium confirmado.", 2), "pesquisador");
        String code = study.studyCode();

        P1Fixture.Stratum first = new P1Fixture.Stratum("feminino", "60-79", "1-5-anos");
        P1Fixture.Stratum second = new P1Fixture.Stratum("masculino", "40-59", "mais-15-anos");
        Map<String, Integer> variant = new java.util.HashMap<>();
        P1Fixture.Stratum[] strata = {first, first, second, second};
        for (int i = 0; i < 4; i++) {
            String sitter = fixture.account(AccountProfile.SITTER, FULL_NAMES.get(i));
            int v = i % 2 + 1;
            String request = consultations.submit(sitter,
                    new ConsultationDraft(FIRST_NAMES.get(i), P1Fixture.answers(strata[i], v))).code();
            variant.put(request, v);
        }
        studies.runCycle(Instant.now());

        List<Lock> locks = new ArrayList<>();
        for (Map<String, Object> sent : jdbc.queryForList("SELECT code, medium_code, first_request, second_request "
                + "FROM research.reading_assignment WHERE state = 'OPEN' AND pair_code IN "
                + "(SELECT code FROM research.structured_pair WHERE study_id = ?) ORDER BY code", study.studyId())) {
            for (int position = 1; position <= 2; position++) {
                String request = (String) sent.get(position == 1 ? "first_request" : "second_request");
                locks.add(new Lock((String) sent.get("medium_code"), (String) sent.get("code"), position,
                        P1Fixture.mediumAnswers(variant.get(request))));
            }
        }
        assertThat(locks).hasSize(4);
        for (Lock lock : locks.subList(0, 3)) {
            mediumReadings.lock(lock.medium(), lock.assignment(), lock.position(), lock.answers());
        }

        // Before the last reading: no package, not on the page, not by SQL.
        assertThat(studies.verificationPackage(code)).isEmpty();
        mvc.perform(get("/estudos/{code}/pacote.json", code)).andExpect(status().isNotFound());
        assertThatThrownBy(() -> jdbc.update("INSERT INTO research.structured_package (study_id, document, "
                + "document_sha256, created_at) VALUES (?, '{}', ?, now())", study.studyId(), Hashes.sha256Hex("{}")))
                .isInstanceOf(Exception.class);

        Lock last = locks.get(3);
        mediumReadings.lock(last.medium(), last.assignment(), last.position(), last.answers());
        assertThat(studies.runCycle(Instant.now()).unblindedStudies()).isEqualTo(1);

        VerificationPackage pacote = studies.verificationPackage(code).orElseThrow();
        assertThat(Hashes.sha256Hex(pacote.document())).isEqualTo(pacote.sha256());
        assertThat(jdbc.queryForObject("SELECT payload FROM audit.event WHERE event_type = "
                + "'VERIFICATION_PACKAGE_EXPORTED' AND subject_ref = ?", String.class, "study:" + study.studyId()))
                .contains(pacote.sha256());

        // No name of anyone, and no e-mail: the guard and the design, checked from outside.
        String lower = pacote.document().toLowerCase(Locale.ROOT);
        for (String name : FIRST_NAMES) {
            assertThat(lower).doesNotContain(name.toLowerCase(Locale.ROOT));
        }
        for (String name : FULL_NAMES) {
            assertThat(lower).doesNotContain(name.toLowerCase(Locale.ROOT).split(" ")[0]);
        }
        assertThat(pacote.document()).doesNotContain("@");
        JsonNode root = JSON.readTree(pacote.document());
        root.path("requests").forEach(request ->
                assertThat(request.has("salt") || request.has("firstName") || request.has("commitment")).isFalse());

        // The public download is the stored document, byte for byte.
        mvc.perform(get("/estudos/{code}/pacote.json", code))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString(pacote.fileName())))
                .andExpect(content().bytes(pacote.document().getBytes(StandardCharsets.UTF_8)));
        mvc.perform(get("/estudos/{code}", code))
                .andExpect(content().string(containsString(pacote.sha256())))
                .andExpect(content().string(containsString("pacote.json")));

        // From the file alone, everything checks out.
        VerificationPackageCheck.Result result = VerificationPackageCheck.check(pacote.document());
        assertThat(result.lines()).allSatisfy(line -> assertThat(line.ok()).as(line.text()).isTrue());
        assertThat(result.ok()).isTrue();
        assertThat(result.lines()).hasSizeGreaterThanOrEqualTo(9);
        assertThat(result.sha256()).isEqualTo(pacote.sha256());

        // The command line, on a file, with no Spring: 0 when it holds.
        Path file = dir.resolve(pacote.fileName());
        Files.writeString(file, pacote.document(), StandardCharsets.UTF_8);
        assertThat(cli(file)).isZero();

        // One altered answer of one reading: the reading commitment and the scoring both catch it.
        ObjectNode altered = (ObjectNode) JSON.readTree(pacote.document());
        ObjectNode answers = (ObjectNode) altered.path("readings").get(0).path("answers");
        String field = answers.fieldNames().next();
        Questionnaire.Field scored = Questionnaires.byVersion(altered.path("study").path("questionnaire").asText())
                .orElseThrow().field(field).orElseThrow();
        String other = scored.options().stream().map(Questionnaire.Option::key)
                .filter(option -> !option.equals(answers.path(field).asText())).findFirst().orElseThrow();
        answers.put(field, other);
        VerificationPackageCheck.Result tampered = VerificationPackageCheck.check(JSON.writeValueAsString(altered));
        assertThat(tampered.ok()).isFalse();
        assertThat(tampered.lines()).anyMatch(line -> !line.ok() && line.text().startsWith("Leituras"));
        Path tamperedFile = dir.resolve("adulterado.json");
        Files.writeString(tamperedFile, JSON.writeValueAsString(altered), StandardCharsets.UTF_8);
        assertThat(cli(tamperedFile)).isEqualTo(1);

        // A published d changed by hand: the scoring catches it.
        ObjectNode inflated = (ObjectNode) JSON.readTree(pacote.document());
        ObjectNode result0 = (ObjectNode) inflated.path("results").get(0);
        result0.put("d", result0.path("d").asInt() + 1);
        assertThat(VerificationPackageCheck.check(JSON.writeValueAsString(inflated)).lines())
                .anyMatch(line -> !line.ok() && line.text().startsWith("Conferencia"));

        // An event of the trail edited: the chain breaks.
        ObjectNode rewritten = (ObjectNode) JSON.readTree(pacote.document());
        ((ObjectNode) rewritten.path("trail").get(0)).put("occurredAt", "2000-01-01T00:00:00Z");
        assertThat(VerificationPackageCheck.check(JSON.writeValueAsString(rewritten)).lines())
                .anyMatch(line -> !line.ok() && line.text().startsWith("Trilha"));

        // A different eligible list: the draw no longer matches the hash recorded at the send.
        ObjectNode pool = (ObjectNode) JSON.readTree(pacote.document());
        ((com.fasterxml.jackson.databind.node.ArrayNode) pool.path("pairs").get(0).path("sends").get(0)
                .path("eligible")).add("MED-INVENTADO");
        assertThat(VerificationPackageCheck.check(JSON.writeValueAsString(pool)).lines())
                .anyMatch(line -> !line.ok() && line.text().startsWith("Sorteio"));

        // Written once: the database refuses any change.
        assertThatThrownBy(() -> jdbc.update("UPDATE research.structured_package SET document = '{}' "
                + "WHERE study_id = ?", study.studyId())).isInstanceOf(Exception.class);
        assertThatThrownBy(() -> jdbc.update("DELETE FROM research.structured_package WHERE study_id = ?",
                study.studyId())).isInstanceOf(Exception.class);
    }

    private static int cli(Path file) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        return PackageVerification.run(new String[] {PackageVerification.COMMAND, file.toString()},
                new PrintStream(out, true, StandardCharsets.UTF_8), new PrintStream(out, true, StandardCharsets.UTF_8));
    }
}
