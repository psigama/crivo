package com.psigama.airlab.catalog;

import com.psigama.airlab.support.Language;
import com.psigama.airlab.support.Refusal;
import com.psigama.airlab.support.Text;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * The closed questionnaire of the automated AIR protocol, P1 (SPEC E13). The sitter and the
 * medium answer the same fields; the system compares the answers field by field.
 *
 * <p>Versioned and immutable: an answer is meaningful only together with the version of the
 * questionnaire it answers, and that version is part of every commitment.
 *
 * <p>Closed fields only. Comparing free text would take a judgement, and P10 forbids handing
 * that judgement to a model; equality between options needs none.
 *
 * <p>Since SPEC 0.28 (section 1.5) the labels exist in both languages of the interface, while the
 * <strong>keys do not change</strong>: an answer is a key, so a request answered in English and one
 * answered in Portuguese are the same data and compare directly. That is why the language is not a
 * variable of the study and there is no stratum for it (rule 10). Because the frozen annex of the
 * preregistration includes the labels, adding a language makes a new version of the questionnaire,
 * never an edit of an old one (rule 9).
 *
 * @param version version tag, recorded with every request and frozen in the preregistration
 * @param fields  fields, in the order they are shown
 */
public record Questionnaire(String version, List<Questionnaire.Field> fields) {

    /** The sitter's "I don't know". Never matches anything. */
    public static final String UNKNOWN = "nao-sei";

    /** The medium's "nothing came". Never matches anything. */
    public static final String NOT_RECEIVED = "nao-recebi";

    private static final Text UNKNOWN_LABEL = Text.of("Não sei", "I don't know");
    private static final Text NOT_RECEIVED_LABEL = Text.of("Não recebi", "Nothing came");

    public Questionnaire {
        if (version == null || version.isBlank()) {
            throw new IllegalArgumentException("a questionnaire needs a version");
        }
        fields = List.copyOf(fields);
        Set<String> keys = new HashSet<>();
        for (Field field : fields) {
            if (!keys.add(field.key())) {
                throw new IllegalArgumentException("duplicate field " + field.key());
            }
        }
        if (fields.stream().noneMatch(field -> field.use() == Use.SCORED)) {
            throw new IllegalArgumentException("a questionnaire without scored fields scores nothing");
        }
    }

    public Optional<Field> field(String key) {
        return fields.stream().filter(field -> field.key().equals(key)).findFirst();
    }

    public List<Field> scoredFields() {
        return fields.stream().filter(field -> field.use() == Use.SCORED).toList();
    }

    /**
     * A draft awaiting the researcher's review (tag ending in {@code -rascunho}). In production a
     * study cannot freeze it (SPEC E13).
     */
    public boolean draft() {
        return version.endsWith("-rascunho");
    }

    /**
     * Checks a sitter's answers: every field answered, every option known, "não sei" only where
     * it is allowed, and nothing extra.
     *
     * @throws Refusal with the reason in both languages, for the person filling in
     */
    public void checkSitterAnswers(Map<String, String> answers) {
        if (answers == null) {
            throw new Refusal("Responda ao questionário.", "Answer the questionnaire.");
        }
        for (String key : answers.keySet()) {
            if (field(key).isEmpty()) {
                throw new Refusal("Campo desconhecido no questionário.",
                        "Unknown field in the questionnaire.");
            }
        }
        for (Field field : fields) {
            String answer = answers.get(field.key());
            if (answer == null || answer.isBlank()) {
                throw new Refusal(
                        "Responda ao campo \"" + field.label().pt() + "\".",
                        "Answer the field \"" + field.label().en() + "\".");
            }
            if (UNKNOWN.equals(answer)) {
                if (!field.unknownAllowed()) {
                    throw new Refusal(
                            "O campo \"" + field.label().pt()
                                    + "\" é usado para formar os pares e precisa de uma resposta.",
                            "The field \"" + field.label().en()
                                    + "\" is used to form the pairs and needs an answer.");
                }
                continue;
            }
            if (field.option(answer).isEmpty()) {
                throw new Refusal(
                        "Opção desconhecida no campo \"" + field.label().pt() + "\".",
                        "Unknown option in the field \"" + field.label().en() + "\".");
            }
        }
    }

    /**
     * Checks a medium's answers (SPEC E13, phase C): every scored field answered with an option
     * or "não recebi", and nothing else. The pairing fields are not asked of the medium — they do
     * not count, and within a pair they are equal or given away by the first name — and "não sei"
     * is the sitter's answer, not the medium's.
     *
     * @throws Refusal with the reason in both languages, for the person filling in
     */
    public void checkMediumAnswers(Map<String, String> answers) {
        if (answers == null) {
            throw new Refusal("Responda ao questionário.", "Answer the questionnaire.");
        }
        for (String key : answers.keySet()) {
            if (field(key).filter(Field::scored).isEmpty()) {
                throw new Refusal("Campo desconhecido no questionário.",
                        "Unknown field in the questionnaire.");
            }
        }
        for (Field field : scoredFields()) {
            String answer = answers.get(field.key());
            if (answer == null || answer.isBlank()) {
                throw new Refusal(
                        "Responda ao campo \"" + field.label().pt()
                                + "\": escolha uma opção ou \"" + NOT_RECEIVED_LABEL.pt() + "\".",
                        "Answer the field \"" + field.label().en()
                                + "\": pick an option or \"" + NOT_RECEIVED_LABEL.en() + "\".");
            }
            if (!NOT_RECEIVED.equals(answer) && field.option(answer).isEmpty()) {
                throw new Refusal(
                        "Opção desconhecida no campo \"" + field.label().pt() + "\".",
                        "Unknown option in the field \"" + field.label().en() + "\".");
            }
        }
    }

    /**
     * Label of an answer, for showing it back, in both languages; "Não sei" and "Não recebi"
     * included. The answer itself is a key and does not depend on the language (rule 10).
     */
    public Text labelOf(String fieldKey, String answer) {
        if (UNKNOWN.equals(answer)) {
            return UNKNOWN_LABEL;
        }
        if (NOT_RECEIVED.equals(answer)) {
            return NOT_RECEIVED_LABEL;
        }
        return field(fieldKey).flatMap(field -> field.option(answer)).map(Option::label)
                .orElseGet(() -> Text.of(answer, answer));
    }

    /** The same label in one language, where the caller already knows which one it is in. */
    public String labelOf(String fieldKey, String answer, Language language) {
        return labelOf(fieldKey, answer).in(language);
    }

    /** What a field is for. */
    public enum Use {
        /** Counts in the comparison between reading and target. */
        SCORED,
        /**
         * Used to form the pairs and shown in the feedback, but out of the score: it is equal
         * within a pair by construction, or the first name gives it away.
         */
        PAIRING
    }

    /**
     * One field.
     *
     * @param key             stable key, part of the canonical answers, the same in both languages
     * @param label           question, in both languages
     * @param use             scored or pairing
     * @param outOfScoreReason why a pairing field does not count, shown on the page; null for scored
     * @param options         closed options
     */
    public record Field(String key, Text label, Use use, Text outOfScoreReason, List<Option> options) {

        public Field {
            options = List.copyOf(options);
            if (options.size() < 2) {
                throw new IllegalArgumentException("field " + key + " needs at least two options");
            }
            Set<String> keys = new HashSet<>();
            for (Option option : options) {
                if (!keys.add(option.key()) || UNKNOWN.equals(option.key())
                        || NOT_RECEIVED.equals(option.key())) {
                    throw new IllegalArgumentException("bad option " + option.key() + " in " + key);
                }
            }
            if ((use == Use.PAIRING) == (outOfScoreReason == null)) {
                throw new IllegalArgumentException(
                        "field " + key + ": a pairing field states why it is out of the score, a scored one does not");
            }
        }

        /** The sitter may answer "não sei" only where the field is not needed for pairing. */
        public boolean unknownAllowed() {
            return use == Use.SCORED;
        }

        public boolean scored() {
            return use == Use.SCORED;
        }

        public Optional<Option> option(String key) {
            return options.stream().filter(option -> option.key().equals(key)).findFirst();
        }
    }

    /** One closed option: a key that never changes and a label in both languages. */
    public record Option(String key, Text label) {
    }
}
