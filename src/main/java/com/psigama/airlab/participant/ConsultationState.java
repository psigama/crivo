package com.psigama.airlab.participant;

import com.psigama.airlab.support.Text;
/**
 * Where a P1 consultation request stands (SPEC E13, sitter panel). Phase B creates requests as
 * {@link #LOCKED}; phase C pairs them, sends them to a medium and records the readings; phase D
 * brings {@link #UNBLINDED}.
 *
 * <p>A state says where the request is, never how it scored: there is no score before
 * unblinding (E10, lock 1).
 */
public enum ConsultationState {

    LOCKED(Text.of("Travado — aguardando par", "Locked — waiting for a pair")),
    PAIRED(Text.of("Par formado — aguardando médium", "Paired — waiting for a medium")),
    SENT_TO_MEDIUM(Text.of("Enviado ao médium", "Sent to the medium")),
    READING_RECEIVED(Text.of("Leitura recebida — aguardando o fim do estudo",
            "Reading received — waiting for the study to end")),
    UNBLINDED(Text.of("Estudo concluído — devolutiva disponível",
            "Study concluded — feedback available")),
    WITHDRAWN(Text.of("Retirado", "Withdrawn"));

    private final Text label;

    ConsultationState(Text label) {
        this.label = label;
    }

    public Text label() {
        return label;
    }

    /**
     * Whether the sitter may withdraw a request in this state on their own, keeping the account
     * (SPEC 0.30, E13, phase B): while the reading has not come in. Once it has, the pair has
     * produced data, and the way out is closing the account, which withdraws from any state —
     * the right to give up at any time stays open either way (Constitution P6).
     *
     * <p>The rule lives here, and not in the service or in a screen, because both the domain and
     * the sitter's page ask the same question of the same state.
     */
    public boolean withdrawableBySitter() {
        return this == LOCKED || this == PAIRED || this == SENT_TO_MEDIUM;
    }
}
