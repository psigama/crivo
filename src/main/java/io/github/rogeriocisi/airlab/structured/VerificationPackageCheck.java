package io.github.rogeriocisi.airlab.structured;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.rogeriocisi.airlab.allocation.AllocationEngine;
import io.github.rogeriocisi.airlab.audit.AuditChain;
import io.github.rogeriocisi.airlab.audit.ChainVerification;
import io.github.rogeriocisi.airlab.audit.ExportedEvent;
import io.github.rogeriocisi.airlab.catalog.Questionnaire;
import io.github.rogeriocisi.airlab.catalog.Questionnaires;
import io.github.rogeriocisi.airlab.support.CanonicalJson;
import io.github.rogeriocisi.airlab.support.Hashes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;

/**
 * The reference verifier of a P1 verification package (SPEC E9): from the JSON file alone — no
 * database, no server, no key — it redoes what the platform did and says, check by check, whether
 * it agrees.
 *
 * <ol>
 *   <li>the trail in the package recomputes, from its first event;</li>
 *   <li>the preregistration hashes to what was frozen in the trail;</li>
 *   <li>the revealed seed matches the commitment frozen before the first pair;</li>
 *   <li>every send — the medium drawn and the order of the names — comes out of the seed and of the
 *       eligible list, which matches the hash the trail recorded at the time of the send;</li>
 *   <li>every reading matches the commitment the trail recorded when it locked;</li>
 *   <li>the scoring of every pair and the three p-values come out of the published answers, and
 *       match the package, the trail and the report;</li>
 *   <li>the report hashes to what the trail recorded.</li>
 * </ol>
 *
 * <p>What it cannot redo, by design: the commitment of each request, which covers the first name
 * and a salt that the package does not publish (SPEC E9, LGPD). That one stays attested by the
 * platform, and the verifier says so instead of passing it silently.
 */
public final class VerificationPackageCheck {

    private static final ObjectMapper JSON = new ObjectMapper();

    private VerificationPackageCheck() {
    }

    /** One check, as a person reads it. */
    public record Line(boolean ok, String text) {
    }

    /**
     * @param sha256 the SHA-256 of the file as read, to compare with the one on the study page
     */
    public record Result(String sha256, List<Line> lines) {

        public Result {
            lines = List.copyOf(lines);
        }

        public boolean ok() {
            return !lines.isEmpty() && lines.stream().allMatch(Line::ok);
        }
    }

    public static Result check(String json) {
        List<Line> lines = new ArrayList<>();
        String sha256 = Hashes.sha256Hex(json);
        JsonNode root;
        try {
            root = JSON.readTree(json);
        } catch (Exception e) {
            lines.add(new Line(false, "O arquivo nao e um JSON valido."));
            return new Result(sha256, lines);
        }
        if (root == null || !VerificationPackage.FORMAT.equals(root.path("format").asText())) {
            lines.add(new Line(false, "Formato desconhecido: esperado " + VerificationPackage.FORMAT + "."));
            return new Result(sha256, lines);
        }
        try {
            new Run(root, lines).all();
        } catch (RuntimeException e) {
            lines.add(new Line(false, "Pacote incompleto ou malformado: " + e.getClass().getSimpleName()
                    + (e.getMessage() == null ? "" : " (" + e.getMessage() + ")")));
        }
        return new Result(sha256, lines);
    }

    private static final class Run {

        private final JsonNode root;
        private final List<Line> lines;
        private final List<ExportedEvent> trail = new ArrayList<>();
        private final List<JsonNode> payloads = new ArrayList<>();
        private final String studyCode;
        private final String seed;

        Run(JsonNode root, List<Line> lines) {
            this.root = root;
            this.lines = lines;
            this.studyCode = root.path("study").path("code").asText();
            this.seed = root.path("study").path("seed").path("revealed").asText();
        }

        void all() {
            trail();
            preregistration();
            seed();
            Optional<Questionnaire> questionnaire = questionnaire();
            sends();
            readings();
            questionnaire.ifPresent(this::scoringAndAnalysis);
            report();
            requestCommitments();
        }

        private void ok(boolean ok, String text) {
            lines.add(new Line(ok, text));
        }

        private static String sha(String text) {
            return Hashes.sha256Hex(text);
        }

        // --- 1. Trail -----------------------------------------------------------------------------

        private void trail() {
            for (JsonNode event : root.path("trail")) {
                trail.add(new ExportedEvent(event.path("seq").asLong(), text(event, "occurredAt"),
                        text(event, "eventType"), text(event, "actorRole"), text(event, "actorCode"),
                        text(event, "subjectRef"), text(event, "payload"), text(event, "prevHash"),
                        text(event, "hash")));
                try {
                    payloads.add(JSON.readTree(text(event, "payload")));
                } catch (Exception e) {
                    payloads.add(JSON.createObjectNode());
                }
            }
            ChainVerification chain = AuditChain.verify(trail);
            ok(chain.intact() && !trail.isEmpty(), "Trilha do pacote: " + chain.detail());
        }

        private static String text(JsonNode node, String field) {
            JsonNode value = node.get(field);
            return value == null || value.isNull() ? null : value.asText();
        }

        /** Payloads of the trail events of a type that satisfy a condition. */
        private List<JsonNode> events(String type, Predicate<JsonNode> where) {
            List<JsonNode> found = new ArrayList<>();
            for (int i = 0; i < trail.size(); i++) {
                if (type.equals(trail.get(i).eventType()) && where.test(payloads.get(i))) {
                    found.add(payloads.get(i));
                }
            }
            return found;
        }

        private List<JsonNode> events(String type, String field, String value) {
            return events(type, payload -> value != null && value.equals(payload.path(field).asText()));
        }

        // --- 2. Preregistration -------------------------------------------------------------------

        private void preregistration() {
            JsonNode preregistration = root.path("study").path("preregistration");
            String declared = preregistration.path("sha256").asText();
            boolean hashes = sha(preregistration.path("document").asText()).equals(declared);
            boolean frozen = !events("PREREGISTRATION_FROZEN", "documentSha256", declared).isEmpty()
                    || !events("PREREGISTRATION_AMENDED", "documentSha256", declared).isEmpty();
            ok(hashes && frozen, "Pre-registro do estudo " + studyCode + ": "
                    + (hashes ? "hash confere" : "HASH NAO CONFERE") + "; "
                    + (frozen ? "congelado na trilha" : "HASH AUSENTE DA TRILHA"));
        }

        // --- 3. Seed ------------------------------------------------------------------------------

        private void seed() {
            JsonNode seedPart = root.path("study").path("seed");
            String commitment = seedPart.path("commitment").asText();
            String drawInput = seedPart.path("drawInput").asText();
            Map<String, Object> expectedInput = new LinkedHashMap<>();
            expectedInput.put("mode", AllocationEngine.INCREMENTAL_MODE);
            expectedInput.put("studyCode", studyCode);
            boolean input = CanonicalJson.write(expectedInput).equals(drawInput);
            boolean matches = seed.matches("[0-9a-f]{64}")
                    && AllocationEngine.seedCommitment(seed, drawInput).equals(commitment);
            boolean committed = !events("ALLOCATION_SEED_COMMITTED", payload ->
                    commitment.equals(payload.path("commitment").asText())
                            && sha(drawInput).equals(payload.path("drawInputSha256").asText())).isEmpty();
            boolean frozen = !events("PREREGISTRATION_FROZEN", "allocationCommitment", commitment).isEmpty();
            boolean revealed = !events("SEED_REVEALED", payload ->
                    commitment.equals(payload.path("commitment").asText())
                            && sha(seed).equals(payload.path("seedSha256").asText())).isEmpty();
            ok(input && matches && committed && frozen && revealed,
                    "Semente revelada contra o compromisso: "
                            + (matches ? "confere" : "NAO CONFERE")
                            + (input ? "" : "; ENTRADA CANONICA DIFERENTE")
                            + (committed && frozen ? "; compromisso na trilha e no pre-registro"
                            : "; COMPROMISSO AUSENTE DA TRILHA")
                            + (revealed ? "; revelacao na trilha" : "; REVELACAO AUSENTE DA TRILHA"));
        }

        // --- Questionnaire and answers ------------------------------------------------------------

        private Optional<Questionnaire> questionnaire() {
            String version = root.path("study").path("questionnaire").asText();
            Optional<Questionnaire> questionnaire = Questionnaires.byVersion(version);
            if (questionnaire.isEmpty()) {
                ok(false, "Questionario " + version + " desconhecido por esta versao do Crivo.");
                return questionnaire;
            }
            List<String> declared = new ArrayList<>();
            root.path("scoredFields").forEach(field -> declared.add(field.asText()));
            List<String> scored = questionnaire.get().scoredFields().stream().map(Questionnaire.Field::key).toList();
            int bad = 0;
            for (JsonNode request : root.path("requests")) {
                bad += unknownAnswers(questionnaire.get(), request.path("answers"));
            }
            for (JsonNode reading : root.path("readings")) {
                bad += unknownAnswers(questionnaire.get(), reading.path("answers"));
            }
            ok(declared.equals(scored) && bad == 0, "Questionario " + version + ": "
                    + (declared.equals(scored) ? "campos pontuados conferem" : "CAMPOS PONTUADOS DIFERENTES")
                    + (bad == 0 ? "; toda resposta e uma opcao do questionario" : "; " + bad + " RESPOSTA(S) FORA DAS OPCOES"));
            return questionnaire;
        }

        private static int unknownAnswers(Questionnaire questionnaire, JsonNode answers) {
            int bad = 0;
            var fields = answers.fields();
            while (fields.hasNext()) {
                var answer = fields.next();
                String option = answer.getValue().asText();
                boolean known = questionnaire.field(answer.getKey())
                        .map(field -> field.option(option).isPresent() || Questionnaire.UNKNOWN.equals(option)
                                || Questionnaire.NOT_RECEIVED.equals(option))
                        .orElse(false);
                if (!known) {
                    bad++;
                }
            }
            return bad;
        }

        // --- 4. Sends: the medium of each pair and the order of the names -------------------------

        private void sends() {
            int total = 0;
            int good = 0;
            List<String> failures = new ArrayList<>();
            for (JsonNode pair : root.path("pairs")) {
                String pairCode = pair.path("code").asText();
                List<String> requests = strings(pair.path("requests"));
                boolean formed = !events("STRUCTURED_PAIR_FORMED", payload -> pairCode.equals(payload.path("pair").asText())
                        && new HashSet<>(strings(payload.path("requests"))).equals(new HashSet<>(requests))).isEmpty();
                if (!formed) {
                    failures.add(pairCode + " sem formacao na trilha");
                }
                for (JsonNode send : pair.path("sends")) {
                    total++;
                    String code = send.path("code").asText();
                    int attempt = send.path("attempt").asInt();
                    List<String> eligible = strings(send.path("eligible"));
                    List<String> order = strings(send.path("order"));
                    String medium = send.path("medium").asText();
                    boolean recorded = !events("READING_REQUEST_SENT", payload ->
                            code.equals(payload.path("assignment").asText())
                                    && pairCode.equals(payload.path("pair").asText())
                                    && attempt == payload.path("attempt").asInt()
                                    && order.equals(strings(payload.path("order")))
                                    && sha(CanonicalJson.write(eligible)).equals(payload.path("eligibleSha256").asText())
                                    && eligible.size() == payload.path("eligibleCount").asInt()).isEmpty();
                    boolean drawn = !eligible.isEmpty()
                            && medium.equals(AllocationEngine.structuredMedium(seed, pairCode, attempt, eligible))
                            && order.equals(AllocationEngine.structuredNameOrder(seed, pairCode, attempt, requests));
                    if (recorded && drawn && formed) {
                        good++;
                    } else {
                        failures.add(code + (recorded ? "" : " fora da trilha") + (drawn ? "" : " sorteio diferente"));
                    }
                }
            }
            ok(total > 0 && good == total, "Sorteio do medium e ordem dos nomes: " + good + " de " + total
                    + " envio(s) refeitos a partir da semente e da lista de elegiveis"
                    + (failures.isEmpty() ? "" : "; NAO CONFEREM: " + String.join(", ", failures)));
        }

        private static List<String> strings(JsonNode array) {
            List<String> out = new ArrayList<>();
            array.forEach(item -> out.add(item.asText()));
            return out;
        }

        // --- 5. Readings --------------------------------------------------------------------------

        private void readings() {
            String version = root.path("study").path("questionnaire").asText();
            int total = 0;
            int good = 0;
            for (JsonNode reading : root.path("readings")) {
                total++;
                String code = reading.path("code").asText();
                String commitment = reading.path("commitment").asText();
                Map<String, String> answers = answers(reading.path("answers"));
                boolean recomputes;
                try {
                    recomputes = ReadingCommitment.of(version, code, reading.path("request").asText(), answers,
                            reading.path("salt").asText()).equals(commitment);
                } catch (IllegalArgumentException e) {
                    recomputes = false;
                }
                boolean locked = !events("STRUCTURED_READING_LOCKED", payload ->
                        code.equals(payload.path("reading").asText())
                                && commitment.equals(payload.path("commitment").asText())
                                && reading.path("assignment").asText().equals(payload.path("assignment").asText()))
                        .isEmpty();
                if (recomputes && locked) {
                    good++;
                }
            }
            ok(total > 0 && good == total, "Leituras: " + good + " de " + total
                    + " conferem com o compromisso gravado na trilha quando travaram");
        }

        private static Map<String, String> answers(JsonNode node) {
            Map<String, String> answers = new TreeMap<>();
            var fields = node.fields();
            while (fields.hasNext()) {
                var field = fields.next();
                answers.put(field.getKey(), field.getValue().asText());
            }
            return answers;
        }

        // --- 6. Scoring and analysis --------------------------------------------------------------

        private void scoringAndAnalysis(Questionnaire questionnaire) {
            Map<String, Map<String, String>> sitters = new TreeMap<>();
            Map<String, String> strata = new TreeMap<>();
            for (JsonNode request : root.path("requests")) {
                sitters.put(request.path("code").asText(), answers(request.path("answers")));
                strata.put(request.path("code").asText(), request.path("stratum").asText());
            }
            Map<String, JsonNode> results = new LinkedHashMap<>();
            root.path("results").forEach(result -> results.put(result.path("pair").asText(), result));

            // The reading of each request is the one of the send the result names, which must be a
            // completed send of that pair, to the medium the result names.
            Map<String, Map<String, String>> readingFor = new TreeMap<>();
            List<StructuredAnalysis.Pair> pairs = new ArrayList<>();
            List<String> failures = new ArrayList<>();
            for (JsonNode pair : root.path("pairs")) {
                String pairCode = pair.path("code").asText();
                List<String> requests = strings(pair.path("requests"));
                pairs.add(new StructuredAnalysis.Pair(pairCode, requests.get(0), requests.get(1)));
                JsonNode result = results.get(pairCode);
                if (result == null) {
                    failures.add(pairCode + " sem resultado");
                    continue;
                }
                String assignment = result.path("assignment").asText();
                boolean completed = false;
                for (JsonNode send : pair.path("sends")) {
                    if (assignment.equals(send.path("code").asText())) {
                        completed = "COMPLETED".equals(send.path("state").asText())
                                && result.path("medium").asText().equals(send.path("medium").asText());
                    }
                }
                if (!completed) {
                    failures.add(pairCode + " sem envio concluido para o medium indicado");
                }
                for (JsonNode reading : root.path("readings")) {
                    if (assignment.equals(reading.path("assignment").asText())) {
                        readingFor.put(reading.path("request").asText(), answers(reading.path("answers")));
                    }
                }
            }
            StructuredAnalysis.Outcome outcome = StructuredAnalysis.run(questionnaire, pairs, sitters, readingFor,
                    strata, seed);

            int good = 0;
            for (StructuredAnalysis.PairOutcome pair : outcome.pairs()) {
                JsonNode result = results.get(pair.pairCode());
                StructuredScoring.PairScore score = pair.score();
                boolean same = result != null
                        && strings(result.path("comparableFields")).equals(score.comparableFields())
                        && result.path("sAA").asInt() == score.targetMatchesA()
                        && result.path("sAB").asInt() == score.decoyMatchesA()
                        && result.path("sBB").asInt() == score.targetMatchesB()
                        && result.path("sBA").asInt() == score.decoyMatchesB()
                        && result.path("d").asInt() == score.d();
                if (same) {
                    good++;
                } else if (result != null) {
                    failures.add(pair.pairCode() + " com d refeito " + score.d() + " e publicado "
                            + result.path("d").asInt());
                }
            }
            boolean allResults = results.size() == outcome.pairs().size();
            ok(failures.isEmpty() && allResults && good == outcome.pairs().size() && good > 0,
                    "Conferencia: " + good + " de " + outcome.pairs().size()
                            + " par(es) com as mesmas contagens e o mesmo d, refeitos a partir das respostas"
                            + (failures.isEmpty() ? "" : "; NAO CONFEREM: " + String.join(", ", failures)));

            JsonNode analysis = root.path("analysis");
            Map<String, String> redone = new LinkedHashMap<>();
            redone.put("primaryP", outcome.primaryP());
            redone.put("signP", outcome.signPlain());
            redone.put("permutationP", outcome.permutationP());
            boolean inPackage = redone.entrySet().stream()
                    .allMatch(entry -> entry.getValue().equals(analysis.path(entry.getKey()).asText()));
            boolean inTrail = !events("ANALYSIS_RUN", payload -> studyCode.equals(payload.path("study").asText())
                    && redone.entrySet().stream()
                    .allMatch(entry -> entry.getValue().equals(payload.path(entry.getKey()).asText()))).isEmpty();
            String report = root.path("report").path("document").asText();
            boolean inReport = redone.values().stream().allMatch(report::contains);
            ok(inPackage && inTrail && inReport, "Analise refeita: p primario " + outcome.primaryP()
                    + ", sinal " + outcome.signPlain() + ", permutacao " + outcome.permutationP() + "; "
                    + (inPackage ? "confere com o pacote" : "DIFERENTE DO PACOTE") + ", "
                    + (inTrail ? "com a trilha" : "DIFERENTE DA TRILHA") + " e "
                    + (inReport ? "com o relatorio" : "DIFERENTE DO RELATORIO"));
        }

        // --- 7. Report ----------------------------------------------------------------------------

        private void report() {
            JsonNode report = root.path("report");
            String declared = report.path("sha256").asText();
            boolean hashes = sha(report.path("document").asText()).equals(declared);
            boolean recorded = !events("REPORT_GENERATED", payload -> studyCode.equals(payload.path("study").asText())
                    && declared.equals(payload.path("documentSha256").asText())).isEmpty();
            ok(hashes && recorded, "Relatorio: " + (hashes ? "hash confere" : "HASH NAO CONFERE") + "; "
                    + (recorded ? "hash gravado na trilha" : "HASH AUSENTE DA TRILHA"));
        }

        // --- What the package cannot redo ---------------------------------------------------------

        private void requestCommitments() {
            String attested = events("UNBLINDING_PERFORMED", "study", studyCode).stream()
                    .map(payload -> payload.path("sitterCommitmentsVerified").asText())
                    .filter(Objects::nonNull).findFirst().orElse("?");
            Set<String> codes = new HashSet<>();
            root.path("requests").forEach(request -> codes.add(request.path("code").asText()));
            String complete = codes.size() + "/" + codes.size();
            ok(complete.equals(attested), "Compromissos dos pedidos: " + attested
                    + " conferidos pela plataforma no desmascaramento. Nao se refazem por fora: cobrem o"
                    + " primeiro nome e um sal que o pacote nao publica (LGPD, SPEC E9).");
        }
    }
}
