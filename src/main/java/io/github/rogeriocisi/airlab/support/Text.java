package io.github.rogeriocisi.airlab.support;

/**
 * A piece of interface text in the two languages of the system (SPEC 0.28, section 1.5, rule 6).
 *
 * <p>Text that comes out of the code — the protocol catalog, the questionnaire labels — is written
 * here in both languages, side by side, so that a translation cannot be forgotten: the record
 * refuses to exist with one side missing, and a reviewer reads the two versions of a sentence on
 * neighbouring lines. Nothing is translated at render time.
 *
 * <p>Portuguese is the language of origin and English is a translation of it (Constitution,
 * language rule, v1.7.0). When a text is a public identifier rather than a sentence — a protocol
 * code, a protocol label such as {@code P1-automático} — it is <em>not</em> a {@code Text}: it
 * enters commitments, reports and the audit trail, and must read the same everywhere.
 *
 * @param pt the Portuguese text, written and reviewed first
 * @param en the English translation of the very same text
 */
public record Text(String pt, String en) {

    public Text {
        if (pt == null || pt.isBlank()) {
            throw new IllegalArgumentException("a text needs its Portuguese version");
        }
        if (en == null || en.isBlank()) {
            throw new IllegalArgumentException("a text needs its English version: " + pt);
        }
    }

    public static Text of(String pt, String en) {
        return new Text(pt, en);
    }

    /** The text in one of the two languages; a missing language reads as Portuguese. */
    public String in(Language language) {
        return language == Language.EN ? en : pt;
    }

    /**
     * The text by language code, {@code pt} or {@code en}, which is what the templates have at
     * hand. An unknown code reads as Portuguese rather than failing: a page never breaks over a
     * language.
     */
    public String in(String languageCode) {
        return in(Language.byCode(languageCode).orElse(Language.DEFAULT));
    }

    /** The Portuguese version, for the few places that are deliberately one language only. */
    @Override
    public String toString() {
        return pt;
    }
}
