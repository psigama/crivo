package com.psigama.airlab.notification.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mock.env.MockEnvironment;

/** How the mailer picks its mode: the outbox, SMTP or nothing. */
class DefaultMailerTest {

    @Test
    @DisplayName("host de e-mail em branco, como o SPRING_MAIL_HOST= do .env.example, nao conta como SMTP")
    void a_blank_mail_host_is_not_smtp() {
        assertThat(DefaultMailer.smtpConfigured(new MockEnvironment())).isFalse();
        assertThat(DefaultMailer.smtpConfigured(new MockEnvironment().withProperty("spring.mail.host", ""))).isFalse();
        assertThat(DefaultMailer.smtpConfigured(new MockEnvironment().withProperty("spring.mail.host", "  "))).isFalse();
        assertThat(DefaultMailer.smtpConfigured(new MockEnvironment().withProperty("spring.mail.host", "smtp.example.org")))
                .isTrue();
    }

    @Test
    @DisplayName("producao com host em branco: o envio fica desativado e a inscricao fechada, mesmo com o remetente do Spring")
    void production_with_a_blank_host_keeps_sign_up_closed() {
        // What the first deploy had: SPRING_MAIL_HOST= in the environment, so Spring Boot built a
        // sender for localhost. The mailer must ignore it (SPEC E10, locks 3 and 5).
        MockEnvironment production = new MockEnvironment().withProperty("spring.mail.host", "");

        assertThat(new DefaultMailer(production, springSender(), Clock.systemUTC()).enabled()).isFalse();
    }

    @Test
    @DisplayName("producao com host definido: SMTP")
    void production_with_a_host_uses_smtp() {
        MockEnvironment production = new MockEnvironment().withProperty("spring.mail.host", "smtp.example.org");

        assertThat(new DefaultMailer(production, springSender(), Clock.systemUTC()).enabled()).isTrue();
    }

    /** The sender Spring Boot builds whenever spring.mail.host exists, even blank. */
    private static org.springframework.beans.factory.ObjectProvider<JavaMailSender> springSender() {
        DefaultListableBeanFactory beans = new DefaultListableBeanFactory();
        beans.registerSingleton("mailSender", new JavaMailSenderImpl());
        return beans.getBeanProvider(JavaMailSender.class);
    }

    @Test
    @DisplayName("testes guardam sempre na caixa de saida, mesmo com SMTP no ambiente")
    void tests_always_keep_the_outbox() {
        assertThat(DefaultMailer.modeFor(true, false, true)).isEqualTo(DefaultMailer.Mode.LOCAL);
        assertThat(DefaultMailer.modeFor(true, true, true)).isEqualTo(DefaultMailer.Mode.LOCAL);
    }

    @Test
    @DisplayName("perfil local: caixa de saida por padrao, SMTP so quando configurado de proposito")
    void the_local_profile_opts_into_smtp() {
        assertThat(DefaultMailer.modeFor(false, true, false)).isEqualTo(DefaultMailer.Mode.LOCAL);
        assertThat(DefaultMailer.modeFor(false, true, true)).isEqualTo(DefaultMailer.Mode.SMTP);
    }

    @Test
    @DisplayName("producao: SMTP ou envio desativado, nunca a caixa de saida em memoria")
    void production_is_smtp_or_nothing() {
        assertThat(DefaultMailer.modeFor(false, false, true)).isEqualTo(DefaultMailer.Mode.SMTP);
        assertThat(DefaultMailer.modeFor(false, false, false)).isEqualTo(DefaultMailer.Mode.DISABLED);
    }
}
