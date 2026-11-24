package com.psigama.airlab.participant;

import com.psigama.airlab.support.Text;
/**
 * Profile of a P1 account (SPEC E13). Chosen once, at sign-up, and never changed: the database
 * refuses the change too (V17).
 *
 * <p>There is no administrator here on purpose. The administrator comes from the server
 * configuration, and no path of the site can create one.
 */
public enum AccountProfile {

    SITTER(Text.of("Consulente", "Sitter"), "CON", ParticipantRole.SITTER),
    MEDIUM(Text.of("Médium", "Medium"), "MED", ParticipantRole.MEDIUM);

    private final Text label;
    private final String codePrefix;
    private final ParticipantRole role;

    AccountProfile(Text label, String codePrefix, ParticipantRole role) {
        this.label = label;
        this.codePrefix = codePrefix;
        this.role = role;
    }

    /** Name shown to people, in both languages of the interface. */
    public Text label() {
        return label;
    }

    /** Prefix of the pseudonymous code, so a code says which side it is on. */
    public String codePrefix() {
        return codePrefix;
    }

    /** The role this profile holds in a study. */
    public ParticipantRole role() {
        return role;
    }
}
