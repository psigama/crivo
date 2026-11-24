package com.psigama.airlab.web;

import com.psigama.airlab.notification.Mailer;
import com.psigama.airlab.participant.AccountProfile;
import com.psigama.airlab.participant.AccountRegistration;
import com.psigama.airlab.participant.AccountService;
import com.psigama.airlab.participant.AccountSignUp;
import com.psigama.airlab.support.Language;
import com.psigama.airlab.participant.AccountView;
import com.psigama.airlab.participant.EmailConfirmationResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Account pages of the automated AIR protocol, P1 (SPEC E13, phase A): create an account with a
 * fixed profile, confirm the e-mail, see and close one's own account.
 *
 * <p>The sign-up answer is the same page whether or not the e-mail already had an account, so
 * the form cannot be used to find out who takes part. The e-mail sent says what happened.
 */
@Controller
class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accounts;
    private final Mailer mailer;
    private final SignUpGate gate;

    AccountController(AccountService accounts, Mailer mailer, SignUpGate gate) {
        this.accounts = accounts;
        this.mailer = mailer;
        this.gate = gate;
    }

    /** What the sign-up form posts. Field names in Portuguese, as in the page. */
    static class SignUpForm {
        private String nome = "";
        private String email = "";
        private String senha = "";
        private String confirmacao = "";
        private String perfil = "";
        private String termo = "";
        private boolean aceite;
        private boolean maioridade;

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }
        public String getConfirmacao() { return confirmacao; }
        public void setConfirmacao(String confirmacao) { this.confirmacao = confirmacao; }
        public String getPerfil() { return perfil; }
        public void setPerfil(String perfil) { this.perfil = perfil; }
        public String getTermo() { return termo; }
        public void setTermo(String termo) { this.termo = termo; }
        public boolean isAceite() { return aceite; }
        public void setAceite(boolean aceite) { this.aceite = aceite; }
        public boolean isMaioridade() { return maioridade; }
        public void setMaioridade(boolean maioridade) { this.maioridade = maioridade; }

        AccountProfile profile() {
            return switch (perfil == null ? "" : perfil) {
                case "consulente" -> AccountProfile.SITTER;
                case "medium" -> AccountProfile.MEDIUM;
                default -> null;
            };
        }

        /** Passwords never go back to the page. */
        void clearPasswords() {
            this.senha = "";
            this.confirmacao = "";
        }
    }

    @GetMapping("/conta/criar")
    String signUpForm(Model model) {
        model.addAttribute("form", new SignUpForm());
        return signUpPage(model);
    }

    @PostMapping("/conta/criar")
    String signUp(@ModelAttribute("form") SignUpForm form, Model model) {
        if (!gate.open()) {
            form.clearPasswords();
            return signUpPage(model);
        }
        if (!form.getSenha().equals(form.getConfirmacao())) {
            model.addAttribute("erro", "A confirmação não é igual à senha.");
            form.clearPasswords();
            return signUpPage(model);
        }

        AccountRegistration registration;
        try {
            registration = accounts.register(new AccountSignUp(form.getNome(), form.getEmail(),
                    form.getSenha(), form.profile(), form.getTermo(), form.isAceite(),
                    form.isMaioridade(), LanguagePages.current().code()));
        } catch (IllegalArgumentException e) {
            // The domain writes these messages for the person filling in the form.
            model.addAttribute("erro", LanguagePages.reason(e));
            form.clearPasswords();
            return signUpPage(model);
        }

        sendSignUpMail(form.getEmail().strip(), form.getNome().strip(), registration);
        return "redirect:/conta/verifique-seu-email";
    }

    private String signUpPage(Model model) {
        model.addAttribute("inscricaoAberta", gate.open());
        model.addAttribute("aguardaCep", gate.awaitingEthicsApproval());
        model.addAttribute("contato", gate.contactEmail().orElse(null));
        model.addAttribute("perfilLocal", gate.localProfile());
        model.addAttribute("versaoTermo", AccountService.CURRENT_TERM_VERSION);
        model.addAttribute("senhaMinima", AccountService.MINIMUM_PASSWORD_LENGTH);
        return "conta-criar";
    }

    /**
      * The confirmation message, in the language of the page the form was filled in on (SPEC 0.28,
      * section 1.5, rule 8). This one leaves inside a request, so the language is at hand; the
      * messages the P1 cycle sends later take it from the account.
      */
    private void sendSignUpMail(String email, String fullName, AccountRegistration registration) {
        String firstName = fullName.contains(" ") ? fullName.substring(0, fullName.indexOf(' ')) : fullName;
        Language language = LanguagePages.current();
        String profile = registration.profile().label().in(language).toLowerCase();
        boolean english = language == Language.EN;

        if (registration.confirmationToken() == null) {
            String login = ServletUriComponentsBuilder.fromCurrentContextPath().path("/login").toUriString();
            mailer.send(email,
                    english ? "Crivo: you already have an account" : "Crivo: você já tem uma conta",
                    english ? """
                    Hello.

                    Someone, perhaps you, tried to create an account on Crivo with this e-mail address. It
                    already has an active %s account, so nothing was created.

                    To sign in: %s

                    The profile of an account never changes. If this was not you, ignore this message.
                    """.formatted(profile, login) : """
                    Olá.

                    Alguém, talvez você, tentou criar uma conta no Crivo com este e-mail. Ele já tem
                    uma conta de %s ativa, e por isso nada foi criado.

                    Para entrar: %s

                    O perfil de uma conta não muda. Se não foi você, ignore esta mensagem.
                    """.formatted(profile, login));
            return;
        }

        String link = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/conta/confirmar")
                .queryParam("token", registration.confirmationToken())
                .toUriString();
        String already;
        if (!registration.alreadyExisted()) {
            already = "";
        } else if (english) {
            already = "This e-mail address already had a " + profile + " account waiting for confirmation; "
                    + "the profile chosen the first time is the one that counts, because the profile never "
                    + "changes.\n\n";
        } else {
            already = "Este e-mail já tinha uma conta de " + profile + " aguardando confirmação; vale o perfil "
                    + "escolhido da primeira vez, porque o perfil não muda.\n\n";
        }
        mailer.send(email,
                english ? "Crivo: confirm your e-mail address" : "Crivo: confirme o seu e-mail",
                english ? """
                Hello, %s.

                You created a %s account on Crivo. %sTo activate it, open the link below within %d hours:

                %s

                Without that confirmation the account does not count. If this was not you, ignore this message.

                Crivo — blind protocols for anomalous cognition research.
                """.formatted(firstName, profile, already, AccountService.CONFIRMATION_HOURS, link) : """
                Olá, %s.

                Você criou uma conta de %s no Crivo. %sPara ativá-la, abra o link abaixo em até %d horas:

                %s

                Sem essa confirmação a conta não vale. Se não foi você, ignore esta mensagem.

                Crivo — protocolos cegos para pesquisa de cognição anômala.
                """.formatted(firstName, profile, already, AccountService.CONFIRMATION_HOURS, link));

        if (gate.localProfile()) {
            // Local profile only, where the data are fictitious: the link is in the log even when an
            // SMTP catcher is configured and happens not to be running. Code and link, no address (P6).
            log.info("Perfil local: link de confirmacao da conta {}: {}", registration.code(), link);
        }
    }

    @GetMapping("/conta/verifique-seu-email")
    String checkYourEmail(Model model) {
        model.addAttribute("perfilLocal", mailer.localOutbox());
        model.addAttribute("smtpLocal", gate.localProfile() && !mailer.localOutbox() && mailer.enabled());
        model.addAttribute("horas", AccountService.CONFIRMATION_HOURS);
        return "conta-verifique";
    }

    /**
     * The link in the e-mail opens a page with a button, and the button confirms. A GET that
     * confirmed by itself would be consumed by the link scanners some mail services run.
     */
    @GetMapping("/conta/confirmar")
    String confirmPage(@RequestParam(name = "token", required = false) String token, Model model) {
        model.addAttribute("token", token == null ? "" : token);
        return "conta-confirmar";
    }

    @PostMapping("/conta/confirmar")
    String confirm(@RequestParam(name = "token", required = false) String token, Model model) {
        EmailConfirmationResult result = accounts.confirmEmail(token);
        model.addAttribute("resultado", result.name());
        model.addAttribute("token", "");
        return "conta-confirmar";
    }

    @GetMapping("/conta")
    String myAccount(Principal principal, Model model, HttpServletRequest request) throws ServletException {
        Optional<AccountView> account = accounts.account(principal.getName());
        if (account.isEmpty()) {
            request.logout();
            return "redirect:/login";
        }
        model.addAttribute("conta", account.get());
        return "conta";
    }

    @PostMapping("/conta/excluir")
    String close(Principal principal,
                 @RequestParam(name = "confirmo", defaultValue = "false") boolean confirmed,
                 HttpServletRequest request) throws ServletException {
        if (!confirmed) {
            return "redirect:/conta?confirmeExclusao";
        }
        accounts.close(principal.getName());
        request.logout();
        // The sign-in page, not the home page: the home page belongs to the static site, which
        // cannot show a notice that depends on the request (SPEC 0.21, section 1.3).
        return "redirect:/login?contaEncerrada";
    }
}
