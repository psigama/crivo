package com.psigama.airlab.web;

import com.psigama.airlab.support.Language;
import com.psigama.airlab.support.Refusal;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;

/**
 * What the web layer needs to know about the language of a page (SPEC 0.28, section 1.5): which
 * language this request is in, which address the same page has in the other language, and whether
 * this page has actually been translated.
 *
 * <p>The language itself is decided once, by {@link LanguageFilter}, from the address. Everything
 * here reads that decision; nothing here makes it again.
 */
final class LanguagePages {

    /** Where an English template lives: the same path, under {@code templates/en/}. */
    private static final String ENGLISH_TEMPLATES = "templates/" + Language.EN.code() + "/";

    private LanguagePages() {
    }

    /** The language of this request, as its address declared it. */
    static Language of(HttpServletRequest request) {
        Object attribute = request.getAttribute(LanguageFilter.ATTRIBUTE);
        if (attribute instanceof Language language) {
            return language;
        }
        // No filter in front (a stand-alone MockMvc slice, for instance): the path still says it.
        return Language.ofPath(pathWithinApplication(request));
    }

    /**
     * The path of the page, without the context path and without the language prefix: the same
     * value for the two addresses of one page, which is what the selector and the {@code hreflang}
     * links are built from.
     */
    static String pagePath(HttpServletRequest request) {
        String path = pathWithinApplication(request);
        if (Language.ofPath(path) == Language.EN) {
            String rest = path.substring(Language.EN.prefix().length());
            return rest.isEmpty() ? "/" : rest;
        }
        return path;
    }

    /**
     * The address of this page in one language. With {@code choice}, the explicit choice goes in
     * the query, so that the selector both switches the page and records the decision (rule 3);
     * without it, the address is the clean one, which is what {@code hreflang} and the language
     * script use. The query string of the current page is kept either way: a flag such as
     * {@code ?travado} belongs to the page, not to the language.
     */
    static String addressIn(HttpServletRequest request, Language language, String application, boolean choice) {
        StringBuilder address = new StringBuilder(application)
                .append(language.prefix())
                .append(pagePath(request));
        String kept = withoutChoice(request.getQueryString());
        if (!choice) {
            return kept.isEmpty() ? address.toString() : address.append('?').append(kept).toString();
        }
        address.append('?').append(kept.isEmpty() ? "" : kept + "&")
                .append(Language.CHOICE).append('=').append(language.code());
        return address.toString();
    }

    /**
     * The language of the request being handled, for the code that formats text outside a
     * controller signature. Spring sets it from the {@code LocaleResolver} on every request; with
     * no request in sight, it is the language of origin.
     */
    static Language current() {
        return Language.EN.locale().getLanguage().equals(LocaleContextHolder.getLocale().getLanguage())
                ? Language.EN
                : Language.PT;
    }

    /**
     * Why an input was refused, in the language of the page. A {@link Refusal} carries the reason
     * in both languages (SPEC 0.28, section 1.5, rule 6); anything else has only the one message it
     * was built with.
     */
    static String reason(RuntimeException e) {
        return e instanceof Refusal refusal ? refusal.text().in(current()) : e.getMessage();
    }

    /** Whether the view has an English template of its own, or would fall back to Portuguese. */
    static boolean translated(String viewName) {
        if (viewName == null || viewName.isBlank()) {
            return false;
        }
        return new ClassPathResource(ENGLISH_TEMPLATES + viewName + ".html").exists();
    }

    private static String pathWithinApplication(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String context = request.getContextPath();
        String path = context != null && !context.isEmpty() && uri.startsWith(context)
                ? uri.substring(context.length())
                : uri;
        return path.isEmpty() ? "/" : path;
    }

    private static String withoutChoice(String query) {
        if (query == null || query.isEmpty()) {
            return "";
        }
        StringBuilder kept = new StringBuilder();
        for (String pair : query.split("&")) {
            String name = pair.split("=", 2)[0];
            if (name.equals(Language.CHOICE) || name.isEmpty()) {
                continue;
            }
            kept.append(kept.isEmpty() ? "" : "&").append(pair);
        }
        return kept.toString();
    }
}
