package com.psigama.airlab.notification.internal;

import com.psigama.airlab.notification.Mailer;
import com.psigama.airlab.participant.AccountService;
import com.psigama.airlab.structured.ReadingRequestSent;
import com.psigama.airlab.structured.StructuredStudyReported;
import com.psigama.airlab.support.BrasiliaTime;
import com.psigama.airlab.support.Language;
import java.time.Instant;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Tells the drawn medium that a request arrived, and — phase D — the sitters and mediums of a
 * concluded study that the feedback is ready (SPEC E13: "o médium recebe e-mail quando chega um
 * pedido"). Sent only after the cycle commits, so nobody is told about a request that a rollback
 * undid.
 *
 * <p>No first name in the message, and no pair code: e-mail is not a blind channel, and the names
 * are one sign-in away, on the panel. The deadline is written out in Brasília time (SPEC 0.23,
 * section 1.4, rule 3): the medium reads it on a phone, not beside the audit trail.
 *
 * <p>These messages leave outside any request — the cycle runs on the server's clock — so there is
 * no page to take a language from: each one comes out in the language of the account that receives
 * it (SPEC 0.28, section 1.5, rule 8). The two versions of every message sit side by side below,
 * for the same reason the catalog keeps them that way: so that a change to one is a visible change
 * to the other.
 */
@Component
class MediumRequestMail {

    private final Mailer mailer;
    private final AccountService accounts;
    private final String baseUrl;

    MediumRequestMail(Mailer mailer, AccountService accounts, Environment environment) {
        this.mailer = mailer;
        this.accounts = accounts;
        this.baseUrl = environment.getProperty("airlab.base-url", "http://localhost:8080").replaceAll("/+$", "");
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    void on(ReadingRequestSent event) {
        if (!mailer.enabled()) {
            return;
        }
        send(event.mediumCode(), language -> requestSubject(language),
                language -> requestBody(language, event.dueAt()));
    }

    /**
     * Tells the sitters and mediums of a concluded study that the feedback is ready (SPEC E13,
     * phase D). No score in the message: the feedback is on the panel, with the words around it.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    void on(StructuredStudyReported event) {
        if (!mailer.enabled()) {
            return;
        }
        for (String sitter : event.sitterCodes()) {
            send(sitter, this::sitterFeedbackSubject, this::sitterFeedbackBody);
        }
        for (String medium : event.mediumCodes()) {
            send(medium, this::mediumFeedbackSubject, this::mediumFeedbackBody);
        }
    }

    /** One message, in the language of the account that receives it. */
    private void send(String accountCode, Subject subject, Body body) {
        Language language = accounts.mailLanguage(accountCode).orElse(Language.DEFAULT);
        accounts.mailAddress(accountCode).ifPresent(address ->
                mailer.send(address, subject.in(language), body.in(language)));
    }

    private interface Subject {
        String in(Language language);
    }

    private interface Body {
        String in(Language language);
    }

    private static String requestSubject(Language language) {
        return language == Language.EN
                ? "Crivo: a reading request for you"
                : "Crivo: um pedido de leitura para você";
    }

    private String requestBody(Language language, Instant dueAt) {
        if (language == Language.EN) {
            return """
                    Hello.

                    There is a reading request for you on Crivo: two first names and the blank questionnaire.

                    Send both readings by %s (Brasília time).

                    Sign in to your dashboard to see the names and answer:
                    %s/painel/medium

                    For safety, this e-mail does not carry the names: they appear only on your dashboard,
                    after you sign in.
                    """.formatted(BrasiliaTime.deadline(dueAt, language), panel(language));
        }
        return """
                Olá.

                Há um pedido de leitura para você no Crivo: dois primeiros nomes e o questionário em branco.

                Envie as duas leituras até %s (horário de Brasília).

                Entre no seu painel para ver os nomes e responder:
                %s/painel/medium

                Por segurança, este e-mail não traz os nomes: eles só aparecem no seu painel, depois que
                você entra.
                """.formatted(BrasiliaTime.deadline(dueAt, language), panel(language));
    }

    /**
     * The address of the panels in a language. The prefix comes from {@link Language}, never
     * written by hand: an e-mail is built outside a request, so there is no context path to carry
     * it (SPEC 0.28, section 1.5, rule 1).
     */
    private String panel(Language language) {
        return baseUrl + language.prefix();
    }

    private String sitterFeedbackSubject(Language language) {
        return language == Language.EN
                ? "Crivo: the study is over and your feedback is ready"
                : "Crivo: o estudo terminou e a devolutiva está disponível";
    }

    private String sitterFeedbackBody(Language language) {
        if (language == Language.EN) {
            return """
                    Hello.

                    The study your request was part of has been concluded. The feedback — the medium's answers
                    beside yours, and the result of the whole study — is on your dashboard:
                    %s/painel/consulente/devolutiva

                    One individual result says nothing about the person who died: what the study measures is
                    the whole set of pairs together.
                    """.formatted(panel(language));
        }
        return """
                Olá.

                O estudo de que o seu pedido fazia parte foi concluído. A devolutiva — as respostas do
                médium ao lado das suas e o resultado do estudo inteiro — está no seu painel:
                %s/painel/consulente/devolutiva

                Um resultado individual não diz nada sobre a pessoa que faleceu: o que o estudo avalia é
                o conjunto de todos os pares.
                """.formatted(panel(language));
    }

    private String mediumFeedbackSubject(Language language) {
        return language == Language.EN
                ? "Crivo: the study is over and the scores of your readings are ready"
                : "Crivo: o estudo terminou e as notas das suas leituras estão disponíveis";
    }

    private String mediumFeedbackBody(Language language) {
        if (language == Language.EN) {
            return """
                    Hello.

                    The study your readings were part of has been concluded. The score of each reading and the
                    result of the whole study are on your dashboard:
                    %s/painel/medium
                    """.formatted(panel(language));
        }
        return """
                Olá.

                O estudo de que as suas leituras faziam parte foi concluído. As notas de cada leitura e o
                resultado do estudo inteiro estão no seu painel:
                %s/painel/medium
                """.formatted(panel(language));
    }
}
