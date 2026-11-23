package io.github.rogeriocisi.airlab.structured;

import io.github.rogeriocisi.airlab.support.Text;
/**
 * Where a request to a medium stands. Says where it is, never how it scored: there is no score
 * before the unblinding (E10, lock 1).
 */
public enum AssignmentState {

    OPEN(Text.of("Aguardando as leituras", "Waiting for the readings")),
    COMPLETED(Text.of("Leituras enviadas", "Readings sent")),
    EXPIRED(Text.of("Prazo vencido — reenviado a outro médium",
            "Deadline passed — sent to another medium")),
    CANCELLED(Text.of("Cancelado", "Cancelled"));

    private final Text label;

    AssignmentState(Text label) {
        this.label = label;
    }

    public Text label() {
        return label;
    }
}
