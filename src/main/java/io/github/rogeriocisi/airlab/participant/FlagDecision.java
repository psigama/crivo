package io.github.rogeriocisi.airlab.participant;

import io.github.rogeriocisi.airlab.support.Text;
/** The administrator's review of a linked-accounts flag (SPEC E13, phase E). Made once. */
public enum FlagDecision {

    /** The same person: the two accounts keep apart in every pair, for good. */
    LINKED(Text.of("Mesma pessoa", "The same person")),

    /** No relation: the restriction falls. */
    UNRELATED(Text.of("Sem relação", "Unrelated"));

    private final Text label;

    FlagDecision(Text label) {
        this.label = label;
    }

    public Text label() {
        return label;
    }
}
