package com.psigama.airlab.catalog;

import com.psigama.airlab.support.Text;
import java.util.List;

/**
 * One protocol of the catalog (SPEC, section 1.1).
 *
 * <p>Note what the record forces. {@code chanceNumerator / chanceDenominator} is the declared chance
 * level, because chance is a property of the design and is never estimated from the data.
 * {@code limits} is mandatory and non-empty: catalog rule 2 says every protocol states what its
 * result does <em>not</em> support, and a record that cannot be built without it keeps that from
 * becoming a footnote someone forgets.
 *
 * <p>Since SPEC 0.28 (section 1.5, rule 6) the text of the catalog is a {@link Text}: the two
 * languages of the interface, side by side, so that a protocol cannot reach an English page in
 * Portuguese. What is <em>not</em> translated is what identifies the protocol — {@code code},
 * {@code shortName} and therefore {@link #label()}: those enter commitments, reports and the audit
 * trail, and have to read the same everywhere.
 *
 * @param code              short public code, such as P1, in the URL of its page
 *                          ({@code /protocolo/P1})
 * @param slug              former URL segment, in Portuguese, kept for the permanent redirects from
 *                          {@code /testes/...} (SPEC 0.17) and for naming the test page templates
 * @param name              public name, in both languages
 * @param shortName         short name, in Portuguese, that follows the code in the label; part of a
 *                          public identifier, so it is not translated
 * @param branch            which branch of anomalous cognition it belongs to
 * @param chanceNumerator   numerator of the declared chance level
 * @param chanceDenominator denominator of the declared chance level
 * @param automation        how much of it runs without a person in the loop
 * @param status            whether it is implemented or planned
 * @param summary           one paragraph on what happens in the protocol, in both languages
 * @param limits            what a result of this protocol does not support, in both languages
 * @param epics             epics of the SPEC that specify it
 */
public record Protocol(String code,
        String slug,
        Text name,
        String shortName,
        Branch branch,
        int chanceNumerator,
        int chanceDenominator,
        Automation automation,
        Status status,
        Text summary,
        List<Text> limits,
        String epics) {

    public Protocol {
        if (limits == null || limits.isEmpty()) {
            throw new IllegalArgumentException(
                    "every protocol must state what its result does not support (catalog rule 2)");
        }
        if (chanceNumerator <= 0 || chanceDenominator <= chanceNumerator) {
            throw new IllegalArgumentException("the declared chance level must be between 0 and 1");
        }
        limits = List.copyOf(limits);
    }

    /**
     * Code and short name, as the site shows them: {@code P1-automático}, {@code P4-visão-remota}.
     * A public identifier, the same in both languages.
     */
    public String label() {
        return code + "-" + shortName.replace(' ', '-');
    }

    /** "Experimento" or "Teste" ("Experiment", "Test"), from the branch (SPEC 0.18). */
    public Text kind() {
        return branch.kind();
    }

    /** Chance level as a percentage, for display. */
    public int chancePercent() {
        return chanceNumerator * 100 / chanceDenominator;
    }

    public boolean implemented() {
        return status == Status.IMPLEMENTED;
    }

    /** Reference only: keeps its code and its page, and is not offered for participation. */
    public boolean discontinued() {
        return status == Status.DISCONTINUED;
    }

    /**
     * Whether anyone can take it right now in the browser, anonymously, as a quick test. Only the
     * ESP protocols are like that: the automated AIR protocol runs end to end too, but through an
     * account, because it collects data about a deceased person.
     */
    public boolean quickTest() {
        return branch == Branch.ESP && automation == Automation.FULL;
    }

    /**
     * Branch of anomalous cognition. The distinction is kept because AIR means something narrower
     * than psi in the literature: information about a deceased person, obtained without a sensory
     * channel.
     *
     * <p>The branch also gives the kind of protocol (SPEC 0.18, glossary): the AIR protocols are
     * <em>experiments</em> — sitter and medium, an account, a preregistered study, a result only at
     * the end — and the ESP protocols are <em>tests</em> — quick, anonymous, with the result at once.
     */
    public enum Branch {

        /** Anomalous information reception: mediumship. */
        AIR(Text.of("Recepção anômala de informação (mediunidade)",
                        "Anomalous information reception (mediumship)"),
                Text.of("Experimento", "Experiment"),
                Text.of("Experimento de mediunidade (AIR)", "Mediumship experiment (AIR)")),

        /** Extrasensory perception with a target drawn by the system. */
        ESP(Text.of("Percepção extrassensorial", "Extrasensory perception"),
                Text.of("Teste", "Test"),
                Text.of("Teste de percepção extrassensorial (ESP)", "Extrasensory perception test (ESP)"));

        private final Text label;
        private final Text kind;
        private final Text kindLabel;

        Branch(Text label, Text kind, Text kindLabel) {
            this.label = label;
            this.kind = kind;
            this.kindLabel = kindLabel;
        }

        public Text label() {
            return label;
        }

        /** "Experimento" or "Teste". */
        public Text kind() {
            return kind;
        }

        /** The kind with the branch, as the protocol pages show it. */
        public Text kindLabel() {
            return kindLabel;
        }
    }

    public enum Automation {

        /** Runs end to end with no person in the loop. */
        FULL(Text.of("Integral", "End to end")),

        /** Part of it must stay with people, and the protocol says why. */
        PARTIAL(Text.of("Parcial", "Partial"));

        private final Text label;

        Automation(Text label) {
            this.label = label;
        }

        public Text label() {
            return label;
        }
    }

    public enum Status {

        IMPLEMENTED(Text.of("Disponível", "Available")),

        /** Delivered in phases; part of it works today and the page says which part. */
        IN_PROGRESS(Text.of("Em construção", "Being built")),

        PLANNED(Text.of("Planejado", "Planned")),

        /**
         * Will not be implemented, and stays on the site as reference only: its page describes the
         * design, takes no participants and is not offered for participation. The code is kept:
         * codes are never reused (SPEC 0.17 renumbered them once, before any real data).
         */
        DISCONTINUED(Text.of("Só referência, não implementado", "Reference only, not implemented"));

        private final Text label;

        Status(Text label) {
            this.label = label;
        }

        public Text label() {
            return label;
        }
    }
}
