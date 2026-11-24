package com.psigama.airlab.participant;

import com.psigama.airlab.support.Language;

/**
 * What a person fills in to create a P1 account (SPEC E13).
 *
 * <p>The password arrives in clear only to be hashed; it is never stored, logged or returned.
 *
 * @param fullName              name, stored encrypted
 * @param email                 e-mail, stored encrypted, compared only through its blind index
 * @param password              chosen password, at least
 *                              {@link AccountService#MINIMUM_PASSWORD_LENGTH} characters
 * @param profile               consulente or medium, fixed from here on
 * @param termVersion           version of the consent term the person accepted
 * @param sensitiveDataAccepted explicit acceptance of the sensitive data clause (LGPD)
 * @param adultDeclared         the person declares being 18 or older (SPEC 0.26). A declaration,
 *                              not a birth date: the exact age is not collected (P6)
 * @param language              language the account is being created in, {@code pt} or {@code en}
 *                              (SPEC 0.28, section 1.5, rule 8): what the e-mails sent outside a
 *                              request come out in. A presentation preference, nothing more
 */
public record AccountSignUp(String fullName,
                            String email,
                            String password,
                            AccountProfile profile,
                            String termVersion,
                            boolean sensitiveDataAccepted,
                            boolean adultDeclared,
                            String language) {

    /**
     * The same sign-up in the language of origin, for a caller with no language at hand — a test or
     * a fixture. A screen always passes the language of the page it was filled in on.
     */
    public AccountSignUp(String fullName, String email, String password, AccountProfile profile,
                         String termVersion, boolean sensitiveDataAccepted, boolean adultDeclared) {
        this(fullName, email, password, profile, termVersion, sensitiveDataAccepted, adultDeclared,
                Language.DEFAULT.code());
    }

    @Override
    public String toString() {
        // Never print the personal data or the password, not even by accident in a log (P6).
        return "AccountSignUp[profile=" + profile + ", termVersion=" + termVersion
                + ", language=" + language + "]";
    }
}
