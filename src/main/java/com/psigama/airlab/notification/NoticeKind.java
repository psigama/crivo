package com.psigama.airlab.notification;

import com.psigama.airlab.support.Text;
/**
 * Completed steps the administrator is told about (SPEC E13, "Avisos ao administrador").
 * Phase A emits {@link #ACCOUNT_CONFIRMED}, phase B {@link #REQUEST_LOCKED}, and phase C the
 * sending, the readings and the deadlines; the rest arrive with phase D and are listed now so the
 * panel and the SPEC say the same thing.
 */
public enum NoticeKind {

    ACCOUNT_CONFIRMED(Text.of("Conta confirmada", "Account confirmed")),
    REQUEST_LOCKED(Text.of("Pedido de consulta travado", "Consultation request locked")),
    PAIR_SENT_TO_MEDIUM(Text.of("Par formado e enviado ao médium",
            "Pair formed and sent to the medium")),
    READING_RECEIVED(Text.of("Leitura recebida", "Reading received")),
    READING_OVERDUE(Text.of("Prazo de leitura vencido; pedido reenviado",
            "Reading deadline passed; request sent again")),
    ACCOUNTS_FLAGGED(Text.of("Contas ligadas sinalizadas para revisão",
            "Linked accounts flagged for review")),
    STUDY_UNBLINDED(Text.of("Número de pares atingido; estudo desmascarado",
            "Number of pairs reached; study unblinded")),
    REPORT_READY(Text.of("Relatório pronto", "Report ready"));

    private final Text label;

    NoticeKind(Text label) {
        this.label = label;
    }

    public Text label() {
        return label;
    }
}
