package io.github.rogeriocisi.airlab.support;

import java.util.Locale;
import java.util.Optional;

/**
 * The two languages of the interface (SPEC 0.28, section 1.5). Portuguese is the language the text
 * is written and reviewed in, and English is a translation of it (Constitution, language rule,
 * v1.7.0).
 *
 * <p>Only two, and an enum rather than a {@link Locale}: every page, message and label exists in
 * exactly these two, and a third would be a decision of the specification, not of a request header.
 * A language unknown to the system is never guessed at — it falls back to Portuguese.
 *
 * <p>The language is a presentation layer. It never reaches blinding, pairing, the audit trail, the
 * analysis or the verification package (section 1.5, rules 10 and 12).
 */
public enum Language {

    /** Portuguese, the language of origin, at the addresses without a prefix. */
    PT("pt", "pt-BR", "pt_BR", "Português", "", Locale.forLanguageTag("pt-BR")),

    /** English, at the addresses prefixed with {@code /en}. */
    EN("en", "en", "en_US", "English", "/en", Locale.ENGLISH);

    /** The language of origin, and the fallback whenever nothing else is known. */
    public static final Language DEFAULT = PT;

    /** Name of the cookie that remembers an explicit choice, and of the query parameter that makes it. */
    public static final String CHOICE = "idioma";

    private final String code;
    private final String tag;
    private final String ogLocale;
    private final String ownName;
    private final String prefix;
    private final Locale locale;

    Language(String code, String tag, String ogLocale, String ownName, String prefix, Locale locale) {
        this.code = code;
        this.tag = tag;
        this.ogLocale = ogLocale;
        this.ownName = ownName;
        this.prefix = prefix;
        this.locale = locale;
    }

    /** {@code pt} or {@code en}: what the cookie, the query parameter and the templates use. */
    public String code() {
        return code;
    }

    /** The {@code lang} and {@code hreflang} attribute of the pages: {@code pt-BR} or {@code en}. */
    public String tag() {
        return tag;
    }

    /** The {@code og:locale} of the pages. */
    public String ogLocale() {
        return ogLocale;
    }

    /** The name of the language in itself, for the selector in the header. */
    public String ownName() {
        return ownName;
    }

    /**
     * The path prefix of this language: empty for Portuguese, {@code /en} for English. Prefixed
     * once, by {@code LanguageFilter} and by the site generator; never written by hand in a link.
     */
    public String prefix() {
        return prefix;
    }

    /** The locale for formatting and for the message bundle. */
    public Locale locale() {
        return locale;
    }

    public Language other() {
        return this == PT ? EN : PT;
    }

    public boolean isDefault() {
        return this == DEFAULT;
    }

    /** By code, {@code pt} or {@code en}; anything else is nothing, never a guess. */
    public static Optional<Language> byCode(String code) {
        if (code == null) {
            return Optional.empty();
        }
        String wanted = code.strip().toLowerCase(Locale.ROOT);
        for (Language language : values()) {
            if (language.code.equals(wanted)) {
                return Optional.of(language);
            }
        }
        return Optional.empty();
    }

    /**
     * The language a path belongs to, by its prefix: {@code /en} and {@code /en/...} are English,
     * everything else is Portuguese. {@code /enfim} is <em>not</em> English, so a Portuguese word
     * starting with "en" never changes language.
     *
     * @param path the path without the context path, starting with a slash
     */
    public static Language ofPath(String path) {
        if (path == null) {
            return DEFAULT;
        }
        if (path.equals(EN.prefix) || path.startsWith(EN.prefix + "/")) {
            return EN;
        }
        return DEFAULT;
    }

    /**
     * What an {@code Accept-Language} header asks for, by the first language it lists that this
     * system has: anything Portuguese is Portuguese, anything English is English, and a header that
     * asks for neither — or none at all — gets nothing, so that the caller decides (section 1.5,
     * rule 2: the forward happens only towards English).
     *
     * <p>Only the language subtag is read: {@code pt-PT} and {@code pt-BR} are both Portuguese, and
     * the region is never used to pick a language. The quality values are respected in the order
     * the header gives them, which is how a browser writes its preference.
     */
    public static Optional<Language> preferred(Iterable<Locale.LanguageRange> ranges) {
        if (ranges == null) {
            return Optional.empty();
        }
        for (Locale.LanguageRange range : ranges) {
            if (range.getWeight() <= 0) {
                continue;
            }
            String subtag = range.getRange().split("-", 2)[0];
            if (subtag.equals("pt")) {
                return Optional.of(PT);
            }
            if (subtag.equals("en")) {
                return Optional.of(EN);
            }
        }
        return Optional.empty();
    }
}
