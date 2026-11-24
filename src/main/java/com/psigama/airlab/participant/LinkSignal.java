package com.psigama.airlab.participant;

import com.psigama.airlab.support.Text;
/**
 * What two accounts were found to share (SPEC E13, phase E). Both are compared through a blind
 * index; neither the address nor the name is stored in clear for this.
 */
public enum LinkSignal {

    /** The same mailbox behind two addresses: case, a "+tag", or dots in a Gmail address. */
    EMAIL_CANONICAL(Text.of("Mesmo e-mail, com outra grafia",
            "The same e-mail address, spelled differently")),

    /** The same full name, ignoring case, accents and spacing; two words at least. */
    FULL_NAME(Text.of("Mesmo nome completo", "The same full name"));

    private final Text label;

    LinkSignal(Text label) {
        this.label = label;
    }

    public Text label() {
        return label;
    }
}
