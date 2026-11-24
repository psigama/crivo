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
}
