package com.psigama.airlab.structured.internal;

import com.psigama.airlab.allocation.AllocationService;
import com.psigama.airlab.allocation.AllocationSnapshot;
import com.psigama.airlab.audit.ActorRole;
import com.psigama.airlab.audit.AuditEventType;
import com.psigama.airlab.audit.AuditTrail;
import com.psigama.airlab.audit.ExportedEvent;
import com.psigama.airlab.catalog.Questionnaire;
import com.psigama.airlab.structured.StructuredAnalysis;
import com.psigama.airlab.structured.StructuredScoring;
import com.psigama.airlab.structured.VerificationPackage;
import com.psigama.airlab.study.PreregistrationSnapshot;
import com.psigama.airlab.study.StudyService;
import com.psigama.airlab.study.StudySummary;
import com.psigama.airlab.support.CanonicalJson;
import com.psigama.airlab.support.Hashes;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.stereotype.Component;

/**
 * Writes the public verification package of a P1 study (SPEC E9), inside the unblinding
 * transaction, right after the report.
 *
 * <p>What goes in: the frozen preregistration, the seed with its commitment and canonical input,
 * every send of every active pair — eligible list, medium and order of the names —, the answers of
 * every sitter of an active pair and of every reading used in the scoring, each reading with its
 * salt, the scores, the three p-values, the report and the trail up to the report. What stays out:
 * the first name and the salt of every request, and anything about accounts. {@link #guard} refuses
 * the package if any key falls outside a closed list or any answer is not an option of the
 * questionnaire, so that no free text can slip in.
 */
@Component
class VerificationPackageWriter {

    /** One locked reading used in the scoring, revealed. */
    record RevealedReading(String code, String assignment, String request, Map<String, String> answers,
                           String salt, String commitment) {
    }

    private final StudyService studies;
    private final AllocationService allocation;
    private final ReadingAssignmentRepository assignments;
    private final StructuredPackageRepository packages;
    private final AuditTrail auditTrail;

    VerificationPackageWriter(StudyService studies, AllocationService allocation,
                              ReadingAssignmentRepository assignments, StructuredPackageRepository packages,
                              AuditTrail auditTrail) {
        this.studies = studies;
        this.allocation = allocation;
        this.assignments = assignments;
        this.packages = packages;
        this.auditTrail = auditTrail;
    }

    /**
     * @param sitterAnswers answers of every sitter of an active pair, by request code
     * @param strata        pairing stratum of each of those requests
     * @param readings      the readings used in the scoring
     * @param mediumByPair  the medium of the completed send of each pair
     */
    java.util.Optional<VerificationPackage> write(StructuredStudyEntity study, StudySummary summary, Questionnaire questionnaire,
                              List<StructuredPairEntity> active, Map<String, Map<String, String>> sitterAnswers,
                              Map<String, String> strata, List<RevealedReading> readings,
                              Map<String, String> mediumByPair, Map<String, String> assignmentByPair,
                              StructuredAnalysis.Outcome outcome, String reportDocument, String reportSha256,
                              String seed, Instant now) {
        long studyId = study.studyId();
        String subject = "study:" + studyId;
        PreregistrationSnapshot preregistration = studies.currentPreregistration(studyId).orElseThrow();
        AllocationSnapshot draw = allocation.snapshot(studyId).orElseThrow();
        long reportPosition = auditTrail.lastPositionOf(AuditEventType.REPORT_GENERATED, subject).orElseThrow();

        Map<String, Object> document = new LinkedHashMap<>();
        document.put("format", VerificationPackage.FORMAT);

        Map<String, Object> studyPart = new LinkedHashMap<>();
        studyPart.put("code", summary.code());
        studyPart.put("questionnaire", study.questionnaireVersion());
        studyPart.put("targetPairs", study.targetPairs());
        studyPart.put("maxPairsPerMedium", study.maxPairsPerMedium());
        studyPart.put("preregistration", Map.of("document", preregistration.document(),
                "sha256", preregistration.sha256()));
        studyPart.put("seed", Map.of("commitment", draw.commitment(), "drawInput", draw.canonicalDrawInput(),
                "revealed", seed));
        document.put("study", studyPart);
        document.put("scoredFields", questionnaire.scoredFields().stream().map(Questionnaire.Field::key).toList());

        List<Object> requests = new ArrayList<>();
        new TreeMap<>(sitterAnswers).forEach((code, answers) -> {
            Map<String, Object> request = new LinkedHashMap<>();
            request.put("code", code);
            request.put("stratum", strata.get(code));
            request.put("answers", new TreeMap<>(answers));
            requests.add(request);
        });
        document.put("requests", requests);

        List<Object> pairs = new ArrayList<>();
        for (StructuredPairEntity pair : active) {
            List<Object> sends = new ArrayList<>();
            for (ReadingAssignmentEntity sent : assignments.findByPairCodeOrderByAttemptAsc(pair.code())) {
                Map<String, Object> send = new LinkedHashMap<>();
                send.put("code", sent.code());
                send.put("attempt", sent.attempt());
                send.put("eligible", sent.eligible());
                send.put("medium", sent.mediumCode());
                send.put("order", List.of(sent.requestAt(1), sent.requestAt(2)));
                send.put("state", sent.state().name());
                sends.add(send);
            }
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("code", pair.code());
            entry.put("requests", pair.requests());
            entry.put("sends", sends);
            pairs.add(entry);
        }
        document.put("pairs", pairs);

        List<Object> readingPart = new ArrayList<>();
        readings.stream().sorted(Comparator.comparing(RevealedReading::code)).forEach(reading -> {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("code", reading.code());
            entry.put("assignment", reading.assignment());
            entry.put("request", reading.request());
            entry.put("answers", new TreeMap<>(reading.answers()));
            entry.put("salt", reading.salt());
            entry.put("commitment", reading.commitment());
            readingPart.add(entry);
        });
        document.put("readings", readingPart);

        List<Object> results = new ArrayList<>();
        for (StructuredAnalysis.PairOutcome pair : outcome.pairs()) {
            StructuredScoring.PairScore score = pair.score();
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("pair", pair.pairCode());
            entry.put("assignment", assignmentByPair.get(pair.pairCode()));
            entry.put("medium", mediumByPair.get(pair.pairCode()));
            entry.put("comparableFields", score.comparableFields());
            entry.put("sAA", score.targetMatchesA());
            entry.put("sAB", score.decoyMatchesA());
            entry.put("sBB", score.targetMatchesB());
            entry.put("sBA", score.decoyMatchesB());
            entry.put("d", score.d());
            results.add(entry);
        }
        document.put("results", results);

        Map<String, Object> analysis = new LinkedHashMap<>();
        analysis.put("primaryMethod", outcome.primary().method());
        analysis.put("primaryP", outcome.primaryP());
        analysis.put("signP", outcome.signPlain());
        analysis.put("permutationP", outcome.permutationP());
        document.put("analysis", analysis);
        document.put("report", Map.of("document", reportDocument, "sha256", reportSha256));

        List<Object> trail = new ArrayList<>();
        for (ExportedEvent event : auditTrail.upTo(reportPosition)) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("seq", event.seq());
            entry.put("occurredAt", event.occurredAt());
            entry.put("eventType", event.eventType());
            entry.put("actorRole", event.actorRole());
            entry.put("actorCode", event.actorCode());
            entry.put("subjectRef", event.subjectRef());
            entry.put("payload", event.payload());
            entry.put("prevHash", event.prevHash());
            entry.put("hash", event.hash());
            trail.add(entry);
        }
        document.put("trail", trail);

        try {
            guard(document, questionnaire);
        } catch (IllegalStateException refused) {
            // Nothing is published, and the unblinding goes on: the report stands without the
            // package. The trail records the refusal with the path that failed, never the value.
            Map<String, Object> withheld = new LinkedHashMap<>();
            withheld.put("study", summary.code());
            withheld.put("reason", refused.getMessage());
            auditTrail.append(AuditEventType.VERIFICATION_PACKAGE_WITHHELD, ActorRole.SYSTEM, null, subject, withheld);
            return java.util.Optional.empty();
        }
        String json = CanonicalJson.write(document);
        String sha256 = Hashes.sha256Hex(json);
        packages.save(new StructuredPackageEntity(studyId, json, sha256, now));

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("study", summary.code());
        payload.put("documentSha256", sha256);
        payload.put("trailUpTo", reportPosition);
        auditTrail.append(AuditEventType.VERIFICATION_PACKAGE_EXPORTED, ActorRole.SYSTEM, null, subject, payload);
        return java.util.Optional.of(new VerificationPackage(summary.code(), json, sha256, now));
    }

    // --- The guard (SPEC E9: the export is refused if anything outside the closed list appears) ----

    /** Every key path the package may have; {@code []} is any item of a list. */
    static final java.util.Set<String> ALLOWED_PATHS = java.util.Set.of(
            "format",
            "study", "study.code", "study.questionnaire", "study.targetPairs", "study.maxPairsPerMedium",
            "study.preregistration", "study.preregistration.document", "study.preregistration.sha256",
            "study.seed", "study.seed.commitment", "study.seed.drawInput", "study.seed.revealed",
            "scoredFields", "scoredFields[]",
            "requests", "requests[]", "requests[].code", "requests[].stratum", "requests[].answers",
            "pairs", "pairs[]", "pairs[].code", "pairs[].requests", "pairs[].requests[]", "pairs[].sends",
            "pairs[].sends[]", "pairs[].sends[].code", "pairs[].sends[].attempt", "pairs[].sends[].eligible",
            "pairs[].sends[].eligible[]", "pairs[].sends[].medium", "pairs[].sends[].order",
            "pairs[].sends[].order[]", "pairs[].sends[].state",
            "readings", "readings[]", "readings[].code", "readings[].assignment", "readings[].request",
            "readings[].answers", "readings[].salt", "readings[].commitment",
            "results", "results[]", "results[].pair", "results[].assignment", "results[].medium",
            "results[].comparableFields", "results[].comparableFields[]", "results[].sAA", "results[].sAB",
            "results[].sBB", "results[].sBA", "results[].d",
            "analysis", "analysis.primaryMethod", "analysis.primaryP", "analysis.signP", "analysis.permutationP",
            "report", "report.document", "report.sha256",
            "trail", "trail[]", "trail[].seq", "trail[].occurredAt", "trail[].eventType", "trail[].actorRole",
            "trail[].actorCode", "trail[].subjectRef", "trail[].payload", "trail[].prevHash", "trail[].hash");

    /**
     * Where text written by a person may appear: the preregistration and the report, both already
     * public on the study page, the method name, and the trail payloads, which never carry personal
     * data (Constitution P6) and are already public as a chain. Everywhere else, a string must look
     * like a code, a hash, an option, an instant or a stratum.
     */
    private static final java.util.Set<String> TEXT_PATHS = java.util.Set.of(
            "study.preregistration.document", "study.seed.drawInput", "analysis.primaryMethod",
            "report.document", "trail[].payload");

    private static final java.util.regex.Pattern PLAIN = java.util.regex.Pattern.compile("[A-Za-z0-9|:._+\\-/]*");

    /**
     * Refuses the package — with the path, never the value — when a key is outside the closed list,
     * an answer is not an option of the questionnaire, or a string that should be a code is not.
     */
    static void guard(Map<String, Object> document, Questionnaire questionnaire) {
        walk("", document, questionnaire);
    }

    private static void walk(String path, Object value, Questionnaire questionnaire) {
        if (path.endsWith(".answers")) {
            checkAnswers(path, value, questionnaire);
            return;
        }
        if (value instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String child = path.isEmpty() ? String.valueOf(entry.getKey()) : path + "." + entry.getKey();
                if (!ALLOWED_PATHS.contains(child)) {
                    throw new IllegalStateException("Pacote de verificacao recusado: chave fora da lista, " + child);
                }
                walk(child, entry.getValue(), questionnaire);
            }
        } else if (value instanceof List<?> list) {
            for (Object item : list) {
                walk(path + "[]", item, questionnaire);
            }
        } else if (value instanceof String text && !TEXT_PATHS.contains(path) && !PLAIN.matcher(text).matches()) {
            throw new IllegalStateException("Pacote de verificacao recusado: valor fora do formato em " + path);
        }
    }

    private static void checkAnswers(String path, Object value, Questionnaire questionnaire) {
        if (!(value instanceof Map<?, ?> answers)) {
            throw new IllegalStateException("Pacote de verificacao recusado: respostas fora do formato em " + path);
        }
        for (Map.Entry<?, ?> answer : answers.entrySet()) {
            Questionnaire.Field field = questionnaire.field(String.valueOf(answer.getKey())).orElseThrow(() ->
                    new IllegalStateException("Pacote de verificacao recusado: campo fora do questionario em " + path));
            String option = String.valueOf(answer.getValue());
            boolean known = field.option(option).isPresent()
                    || Questionnaire.UNKNOWN.equals(option) || Questionnaire.NOT_RECEIVED.equals(option);
            if (!known) {
                throw new IllegalStateException("Pacote de verificacao recusado: resposta fora das opcoes em "
                        + path + "." + field.key());
            }
        }
    }
}
