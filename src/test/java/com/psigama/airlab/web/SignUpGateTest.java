package com.psigama.airlab.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.psigama.airlab.notification.MailMessage;
import com.psigama.airlab.notification.Mailer;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

/** When P1 sign-up and requests open (SPEC E10, locks 3, 4 and 5; E13). */
class SignUpGateTest {

    private static final String CONTACT = "airlab.contact-email";
    private static final String CEP = "airlab.p4.cep-reference";

    @Test
    @DisplayName("producao com e-mail e contato, mas sem a referencia do CEP: inscricao e pedidos fechados")
    void production_without_the_ethics_approval_keeps_sign_up_closed() {
        SignUpGate gate = new SignUpGate(mailer(true), production().withProperty(CONTACT, "contato@example.org"));

        assertThat(gate.open()).isFalse();
        assertThat(gate.requestsOpen()).isFalse();
        assertThat(gate.awaitingEthicsApproval()).isTrue();
    }

    @Test
    @DisplayName("producao com e-mail, contato e referencia do CEP: inscricao e pedidos abertos")
    void production_with_everything_opens_sign_up_and_requests() {
        SignUpGate gate = new SignUpGate(mailer(true), production()
                .withProperty(CONTACT, "contato@example.org")
                .withProperty(CEP, "CAAE 00000000.0.0000.0000"));

        assertThat(gate.open()).isTrue();
        assertThat(gate.requestsOpen()).isTrue();
        assertThat(gate.awaitingEthicsApproval()).isFalse();
    }

    @Test
    @DisplayName("producao sem contato humano ou sem e-mail: fechada, e a pagina nao culpa o CEP")
    void production_without_contact_or_mail_is_closed_for_another_reason() {
        SignUpGate noContact = new SignUpGate(mailer(true), production().withProperty(CEP, "CAAE 1"));
        SignUpGate noMail = new SignUpGate(mailer(false), production()
                .withProperty(CONTACT, "contato@example.org"));

        assertThat(noContact.open()).isFalse();
        assertThat(noContact.awaitingEthicsApproval()).isFalse();
        assertThat(noMail.open()).isFalse();
        assertThat(noMail.awaitingEthicsApproval()).isFalse();
    }

    @Test
    @DisplayName("referencia do CEP em branco conta como ausente")
    void a_blank_ethics_reference_is_absent() {
        SignUpGate gate = new SignUpGate(mailer(true), production()
                .withProperty(CONTACT, "contato@example.org")
                .withProperty(CEP, "   "));

        assertThat(gate.open()).isFalse();
    }

    @Test
    @DisplayName("perfil local: abre so com o envio de e-mail, sem contato nem CEP")
    void the_local_profile_needs_only_mail() {
        MockEnvironment local = new MockEnvironment();
        local.setActiveProfiles("local");

        assertThat(new SignUpGate(mailer(true), local).open()).isTrue();
        assertThat(new SignUpGate(mailer(true), local).requestsOpen()).isTrue();
        assertThat(new SignUpGate(mailer(true), local).awaitingEthicsApproval()).isFalse();
        assertThat(new SignUpGate(mailer(false), local).open()).isFalse();
    }

    private static MockEnvironment production() {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles("prod");
        return environment;
    }

    private static Mailer mailer(boolean enabled) {
        return new Mailer() {
            @Override
            public boolean enabled() {
                return enabled;
            }

            @Override
            public boolean localOutbox() {
                return false;
            }

            @Override
            public void send(String to, String subject, String body) {
            }

            @Override
            public List<MailMessage> outbox() {
                return List.of();
            }
        };
    }
}
