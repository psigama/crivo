package com.psigama.airlab;

import static org.assertj.core.api.Assertions.assertThat;

import com.psigama.airlab.allocation.AllocationPlan;
import com.psigama.airlab.allocation.AllocationService;
import com.psigama.airlab.participant.TargetProfile;
import com.psigama.airlab.study.PreregistrationSnapshot;
import com.psigama.airlab.study.StudySummary;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Blinding matrix as a test suite (SPEC section 4, Constitution P1).
 *
 * <p>These check the structural half of the matrix: a field forbidden to a role is <em>absent
 * from the projection</em> rather than filtered in a view (architectural rule 3). Rows that
 * depend on persisted state are covered by the integration tests of each module as it lands;
 * the ones below hold for every build.
 */
class BlindingMatrixTest {

    private static final String[] IDENTIFYING_NAMES = {
            "name", "fullname", "firstname", "contact", "email", "phone", "notes"};

    @Test
    @DisplayName("a projecao de alvo usada no pareamento nao tem nenhum campo identificavel")
    void target_projection_carries_no_identifiable_field() {
        assertNoFieldsNamedLike(TargetProfile.class, IDENTIFYING_NAMES);
    }

    @Test
    @DisplayName("a visao de estudo do pesquisador nao expoe ligacao leitura -> alvo nem semente")
    void researcher_study_projection_has_no_blinding_sensitive_field() {
        assertNoFieldsNamedLike(StudySummary.class, "seed", "target", "reading", "sitter", "medium");
        assertNoFieldsNamedLike(PreregistrationSnapshot.class, "seed", "allocation", "target");
    }

    @Test
    @DisplayName("nenhum metodo publico do servico de sorteio devolve o plano de alocacao")
    void allocation_service_never_hands_out_the_plan() {
        for (Method method : AllocationService.class.getMethods()) {
            assertThat(method.getReturnType())
                    .describedAs("metodo %s devolveria o plano de alocacao", method.getName())
                    .isNotEqualTo(AllocationPlan.class);
            assertThat(method.getGenericReturnType().getTypeName())
                    .describedAs("metodo %s devolveria o plano de alocacao", method.getName())
                    .doesNotContain(AllocationPlan.class.getName());
        }
    }

    @Test
    @DisplayName("o progresso da coleta nao carrega nenhum campo de desfecho")
    void progress_carries_no_outcome_field() {
        // Epico E10, trava 1: automatizar o estudo poe este numero na frente do pesquisador
        // todos os dias. Se ele vier acompanhado do resultado, a parada opcional deixa de ser
        // uma tentacao ocasional e vira rotina.
        assertNoFieldsNamedLike(com.psigama.airlab.analysis.CollectionProgress.class,
                "hit", "acerto", "score", "nota", "accuracy", "acuracia", "proportion",
                "proporcao", "pvalue", "correct", "result", "effect", "difference", "diferenca");
    }

    @Test
    @DisplayName("o resumo mostrado ao pesquisador conta unidades, nunca desempenho")
    void progress_summary_reports_counts_only() {
        com.psigama.airlab.analysis.CollectionProgress progress =
                new com.psigama.airlab.analysis.CollectionProgress(1L, 18, 30);

        assertThat(progress.remainingUnits()).isEqualTo(12);
        assertThat(progress.targetReached()).isFalse();
        assertThat(progress.summary())
                .contains("18 de 30")
                .contains("Nenhum resultado");
    }

    @Test
    @DisplayName("dados pessoais nao vazam por toString")
    void personal_data_does_not_leak_through_to_string() {
        assertThat(new com.psigama.airlab.participant.PersonalData("Maria", "maria@exemplo")
                .toString())
                .doesNotContain("Maria")
                .doesNotContain("maria@exemplo");
        assertThat(new com.psigama.airlab.participant.TargetRegistration(
                "Antonio",
                com.psigama.airlab.participant.TargetRegistration.Gender.MASCULINE,
                com.psigama.airlab.participant.TargetRegistration.AgeBand.FROM_60_TO_79,
                com.psigama.airlab.participant.TargetRegistration.DeathPeriod.FROM_1_TO_5_YEARS,
                null).toString())
                .doesNotContain("Antonio");
    }

    @Test
    @DisplayName("o progresso de uma sessao de teste nao tem nenhum campo de desempenho")
    void trial_session_progress_has_no_outcome_field() {
        assertNoFieldsNamedLike(com.psigama.airlab.trials.SessionProgress.class,
                "hit", "score", "target", "correct", "acerto", "seedhex", "result");
    }

    // --- P1, leitura estruturada automatizada (SPEC E13, matriz de visibilidade do P1) ---------

    private static final String[] P1_CONTENT_AND_OUTCOME = {
            "name", "firstname", "answer", "resposta", "salt", "score", "nota", "hit", "acerto",
            "difference", "diferenca", "result", "email"};

    @Test
    @DisplayName("P1: o que o administrador ve tem contagens, codigos e horas, nunca conteudo ou nota")
    void p4_administrator_projections_carry_no_content_or_outcome() {
        assertNoFieldsNamedLike(com.psigama.airlab.participant.AccountCounts.class, P1_CONTENT_AND_OUTCOME);
        assertNoFieldsNamedLike(com.psigama.airlab.participant.ConsultationCounts.class, P1_CONTENT_AND_OUTCOME);
        assertNoFieldsNamedLike(com.psigama.airlab.notification.AdminNotice.class, P1_CONTENT_AND_OUTCOME);
    }

    @Test
    @DisplayName("P1: a situacao do pedido diz onde ele esta, sem primeiro nome, respostas ou nota")
    void p4_request_status_carries_no_content_or_outcome() {
        assertNoFieldsNamedLike(com.psigama.airlab.participant.ConsultationStatus.class, P1_CONTENT_AND_OUTCOME);
        assertNoFieldsNamedLike(com.psigama.airlab.participant.ConsultationLocked.class, P1_CONTENT_AND_OUTCOME);
        assertNoFieldsNamedLike(com.psigama.airlab.participant.AccountConfirmed.class, P1_CONTENT_AND_OUTCOME);
    }

    @Test
    @DisplayName("P1: nenhum estado do pedido carrega desempenho")
    void p4_request_states_carry_no_outcome() {
        for (com.psigama.airlab.participant.ConsultationState state
                : com.psigama.airlab.participant.ConsultationState.values()) {
            // SPEC 0.28: o rotulo existe nos dois idiomas, e nenhum dos dois pode vazar desempenho.
            for (com.psigama.airlab.support.Language language
                    : com.psigama.airlab.support.Language.values()) {
                assertThat(state.label().in(language).toLowerCase(Locale.ROOT))
                        .doesNotContain("acert").doesNotContain("nota").doesNotContain("encaix")
                        .doesNotContain("hit").doesNotContain("score").doesNotContain("match");
            }
        }
    }

    @Test
    @DisplayName("P1: o pedido, a conta e o rascunho nao vazam por toString")
    void p4_content_does_not_leak_through_to_string() {
        java.util.Map<String, String> answers = java.util.Map.of("causa", "cancer");
        assertThat(new com.psigama.airlab.participant.ConsultationDraft("Sebastiao", answers).toString())
                .doesNotContain("Sebastiao").doesNotContain("cancer");
        assertThat(new com.psigama.airlab.participant.OwnConsultation("PED-1", "v1", "Sebastiao",
                answers, "0".repeat(64), java.time.Instant.EPOCH).toString())
                .doesNotContain("Sebastiao").doesNotContain("cancer");
        assertThat(new com.psigama.airlab.participant.AccountSignUp("Maria", "maria@exemplo",
                "segredo-123456", com.psigama.airlab.participant.AccountProfile.SITTER, "conta-v2", true, true)
                .toString())
                .doesNotContain("Maria").doesNotContain("maria@exemplo").doesNotContain("segredo");
    }

    // --- P1, fase C: o medium e o envio (SPEC E13, matriz de visibilidade do P1) ----------------

    /** What the medium must not get: the sitter, the request, anyone's answers, any score. */
    private static final String[] P1_MEDIUM_FORBIDDEN = {
            "sitter", "consulente", "request", "pedido", "answer", "resposta", "salt", "score", "nota",
            "hit", "acerto", "difference", "diferenca", "result", "email", "commitment"};

    @Test
    @DisplayName("P1: o que o medium ve do pedido tem nomes, posicao e prazo; nunca consulente, respostas ou nota")
    void p4_medium_projection_carries_no_sitter_link_answers_or_outcome() {
        assertNoFieldsNamedLike(com.psigama.airlab.structured.MediumAssignment.class, P1_MEDIUM_FORBIDDEN);
        assertNoFieldsNamedLike(com.psigama.airlab.structured.MediumAssignment.Slot.class, P1_MEDIUM_FORBIDDEN);
    }

    @Test
    @DisplayName("P1: o progresso do estudo e so contagem, sem medium, par ou conteudo")
    void p4_study_progress_is_counts_only() {
        for (Class<?> type : new Class<?>[]{com.psigama.airlab.structured.StructuredProgress.class,
                com.psigama.airlab.structured.CycleReport.class}) {
            Arrays.stream(type.getRecordComponents()).forEach(component -> assertThat(component.getType())
                    .describedAs("%s.%s", type.getSimpleName(), component.getName())
                    .isIn(int.class, long.class));
        }
        assertNoFieldsNamedLike(com.psigama.airlab.structured.StructuredStudyView.class, P1_CONTENT_AND_OUTCOME);
        // N and the cap per medium are declared numbers; what must be absent is any code of a
        // pair, a send, a medium or a reading.
        assertNoFieldsNamedLike(com.psigama.airlab.structured.StructuredStudyView.class,
                "mediumcode", "paircode", "assignment", "reading", "leitura");
    }

    @Test
    @DisplayName("P1: a API do estudo nao devolve a ligacao par -> medium, nem leitura, a quem a usa")
    void p4_study_service_returns_no_pair_medium_link_or_reading() {
        for (Method method : com.psigama.airlab.structured.StructuredStudyService.class.getMethods()) {
            assertThat(method.getReturnType())
                    .describedAs("StructuredStudyService.%s", method.getName())
                    .isIn(void.class, boolean.class, java.util.Optional.class, java.util.List.class,
                            com.psigama.airlab.structured.StructuredStudyView.class,
                            com.psigama.airlab.structured.StructuredProgress.class,
                            com.psigama.airlab.structured.CycleReport.class);
        }
        // The report is the one place with scores and the pair-to-medium link, and it comes only
        // wrapped in an Optional that stays empty until the unblinding (integration test).
        assertThat(java.util.Arrays.stream(com.psigama.airlab.structured.StructuredStudyService.class.getMethods())
                .filter(method -> method.getName().equals("report"))
                // Explicit Class<?>: the Eclipse compiler (ECJ) cannot match Class<Optional> against
                // the captured wildcard that javac accepts.
                .<Class<?>>map(Method::getReturnType)).containsOnly(java.util.Optional.class);
    }

    // --- P1, fase D: a devolutiva (SPEC E13, matriz do P1 depois do desmascaramento) -----------

    @Test
    @DisplayName("P1: a devolutiva do consulente traz a leitura do proprio alvo, nunca as respostas do outro nem o medium")
    void p4_sitter_feedback_has_no_partner_answers_or_medium() {
        assertNoFieldsNamedLike(com.psigama.airlab.structured.TargetFeedback.class,
                "medium", "partner", "other", "sitter", "decoyanswer", "salt", "email", "name");
    }

    @Test
    @DisplayName("P1: a devolutiva do medium e contagem, nunca as respostas dos consulentes")
    void p4_medium_feedback_has_counts_only() {
        assertNoFieldsNamedLike(com.psigama.airlab.structured.ReadingFeedback.class,
                "answer", "resposta", "sitter", "consulente", "request", "pedido", "salt", "email");
    }

    @Test
    @DisplayName("P1: as respostas reveladas nao levam o primeiro nome nem o sal, e o aviso do fim nao leva nota")
    void p4_revealed_answers_and_report_event_carry_no_name_salt_or_score() {
        assertNoFieldsNamedLike(com.psigama.airlab.participant.RevealedAnswers.class, "name", "salt", "email");
        assertNoFieldsNamedLike(com.psigama.airlab.structured.StructuredStudyReported.class,
                P1_CONTENT_AND_OUTCOME);
    }

    @Test
    @DisplayName("P1: devolutivas e respostas reveladas nao vazam por toString")
    void p4_feedback_does_not_leak_through_to_string() {
        java.util.Map<String, String> answers = java.util.Map.of("causa", "cancer");
        assertThat(new com.psigama.airlab.structured.TargetFeedback("STD-1", "v1", answers,
                java.util.List.of("causa"), 1, 0).toString()).doesNotContain("cancer");
        assertThat(new com.psigama.airlab.structured.ReadingFeedback("STD-1", "ENV-1", 1, "Sebastiao",
                1, 1, 0, 1).toString()).doesNotContain("Sebastiao");
        assertThat(new com.psigama.airlab.participant.RevealedAnswers("PED-1", "v1", answers, true)
                .toString()).doesNotContain("cancer");
    }

    @Test
    @DisplayName("P1: o pedido visto pelo pareamento e os eventos do envio nao carregam nome, resposta ou nota")
    void p4_pairing_view_and_events_carry_no_content() {
        assertNoFieldsNamedLike(com.psigama.airlab.participant.ConsultationRef.class, P1_CONTENT_AND_OUTCOME);
        assertNoFieldsNamedLike(com.psigama.airlab.structured.ReadingRequestSent.class, P1_CONTENT_AND_OUTCOME);
        assertNoFieldsNamedLike(com.psigama.airlab.structured.PairReadingsReceived.class, P1_CONTENT_AND_OUTCOME);
        assertNoFieldsNamedLike(com.psigama.airlab.structured.ReadingRequestExpired.class, P1_CONTENT_AND_OUTCOME);
    }

    @Test
    @DisplayName("P1: o consulente nao recebe a leitura nem o medium antes do desmascaramento")
    void p4_sitter_projections_carry_no_reading_or_medium() {
        for (Class<?> type : new Class<?>[]{com.psigama.airlab.participant.ConsultationStatus.class,
                com.psigama.airlab.participant.OwnConsultation.class}) {
            assertNoFieldsNamedLike(type, "reading", "leitura", "medium", "score", "nota", "hit", "acerto");
        }
    }

    @Test
    @DisplayName("P1: nenhum estado de envio ao medium carrega desempenho")
    void p4_assignment_states_carry_no_outcome() {
        for (com.psigama.airlab.structured.AssignmentState state
                : com.psigama.airlab.structured.AssignmentState.values()) {
            // SPEC 0.28: o rotulo existe nos dois idiomas, e nenhum dos dois pode vazar desempenho.
            for (com.psigama.airlab.support.Language language
                    : com.psigama.airlab.support.Language.values()) {
                assertThat(state.label().in(language).toLowerCase(Locale.ROOT))
                        .doesNotContain("acert").doesNotContain("nota").doesNotContain("encaix")
                        .doesNotContain("hit").doesNotContain("score").doesNotContain("match");
            }
        }
    }

    @Test
    @DisplayName("P1: o envio e a leitura do medium nao vazam por toString")
    void p4_medium_content_does_not_leak_through_to_string() {
        var slot = new com.psigama.airlab.structured.MediumAssignment.Slot(1, "Sebastiao", false, null);
        var assignment = new com.psigama.airlab.structured.MediumAssignment("ENV-1",
                com.psigama.airlab.structured.AssignmentState.OPEN, "v1", java.time.Instant.EPOCH,
                java.time.Instant.EPOCH.plusSeconds(60), java.util.List.of(slot));
        assertThat(assignment.toString()).doesNotContain("Sebastiao");
        assertThat(slot.toString()).doesNotContain("Sebastiao");
        assertThat(new com.psigama.airlab.structured.MediumReadingDraft("ENV-1", 1,
                java.util.Map.of("causa", "cancer")).toString()).doesNotContain("cancer");
    }

    // --- P1, fase E: sinalizacoes e pagina publica ----------------------------------------------

    @Test
    @DisplayName("P1: a sinalizacao traz codigos, perfis e o tipo de sinal, nunca nome, e-mail ou impressao")
    void p4_flags_carry_no_name_email_or_fingerprint() {
        assertNoFieldsNamedLike(com.psigama.airlab.participant.AccountFlag.class,
                "name", "email", "fingerprint", "address");
        assertNoFieldsNamedLike(com.psigama.airlab.participant.AccountsFlagged.class,
                "account", "name", "email");
    }

    // --- E8, ancoragem externa (SPEC 0.15): o que se publica sobre os carimbos ---------------------

    @Test
    @DisplayName("E8: carimbos publicados levam posicao, hash e hora; nunca dado pessoal, conteudo ou nota")
    void anchor_projections_carry_no_personal_content_or_outcome() {
        for (Class<?> type : new Class<?>[]{com.psigama.airlab.anchor.AnchorView.class,
                com.psigama.airlab.anchor.AnchorCheck.class,
                com.psigama.airlab.anchor.AnchorStatus.class}) {
            assertNoFieldsNamedLike(type, IDENTIFYING_NAMES);
            assertNoFieldsNamedLike(type, "sitter", "medium", "answer", "resposta", "reading", "score", "nota",
                    "seed", "salt", "target");
        }
    }

    private static void assertNoFieldsNamedLike(Class<?> type, String... forbidden) {
        Arrays.stream(type.getRecordComponents()).forEach(component -> {
            String name = component.getName().toLowerCase(Locale.ROOT);
            for (String word : forbidden) {
                assertThat(name)
                        .describedAs("%s.%s nao deveria existir nesta projecao",
                                type.getSimpleName(), component.getName())
                        .doesNotContain(word);
            }
        });
    }
}
