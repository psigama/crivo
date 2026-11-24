package com.psigama.airlab.support;

/**
 * An input the domain refuses, with the reason in both languages of the interface (SPEC 0.28,
 * section 1.5, rule 6).
 *
 * <p>It is an {@link IllegalArgumentException}, so every caller that already treats a refusal as
 * one keeps working, and {@link #getMessage()} is the Portuguese text — the language of origin,
 * which is what a log, a test and a stack trace get. A page that knows which language it is in asks
 * for {@link #text()} instead and shows the right side of it.
 *
 * <p>This is how a validation message crosses from the domain to the screen without the domain
 * having to know the language of a request: the reason travels in both languages, and the web layer
 * picks one at the end. Nothing here is stored, hashed or audited.
 */
public class Refusal extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    private final Text text;

    public Refusal(Text text) {
        super(text.pt());
        this.text = text;
    }

    public Refusal(String pt, String en) {
        this(Text.of(pt, en));
    }

    /** The reason, in both languages. */
    public Text text() {
        return text;
    }
}
