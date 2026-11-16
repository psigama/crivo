package io.github.rogeriocisi.airlab.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Tells the site header which section the page belongs to, so the main navigation can mark
 * it with {@code aria-current="page"}, and where each side of the site lives.
 *
 * <p>Only the top level section is exposed, never the full path: the catalog,
 * {@code /protocolos}, and every protocol page under {@code /protocolo/...} mark "Protocolos",
 * and nothing about the session leaks into the model. Since SPEC 0.17 the ESP tests are part of
 * that section again.
 *
 * <p>Since SPEC 0.21 the editorial pages are a static site at their own address (section 1.3).
 * The shared header and footer prefix each link with {@code site} or {@code aplicacao}: here the
 * application is the current host, so {@code aplicacao} is empty, and {@code site} is
 * {@code airlab.site-url} — empty too in the local profile, where this process serves both sides.
 * The generator of the static site gives the same variables the other way round.
 */
@ControllerAdvice
class NavigationAdvice {

    private final String siteUrl;

    NavigationAdvice(@Value("${airlab.site-url:}") String siteUrl) {
        this.siteUrl = SitePages.base(siteUrl, "airlab.site-url (AIRLAB_SITE_URL)");
    }

    @ModelAttribute("secaoAtual")
    String currentSection(HttpServletRequest request) {
        String path = request.getRequestURI();
        String context = request.getContextPath();
        if (context != null && !context.isEmpty() && path.startsWith(context)) {
            path = path.substring(context.length());
        }
        return sectionOf(path);
    }

    /**
     * Whether someone is signed in, so the header offers "Painel" instead of "Entrar". Only a
     * boolean: who it is stays out of the shared layout.
     */
    @ModelAttribute("logado")
    boolean signedIn(HttpServletRequest request) {
        return request.getUserPrincipal() != null;
    }

    /** Where the editorial pages live, prefixed to every link that goes to them. */
    @ModelAttribute("site")
    String site() {
        return siteUrl;
    }

    /** Where the application lives: this very host, so links to it stay relative. */
    @ModelAttribute("aplicacao")
    String application() {
        return "";
    }

    /** The top level section of a path, shared with the generator of the static site. */
    static String sectionOf(String path) {
        if (path.isEmpty() || path.equals("/")) {
            return "inicio";
        }
        String[] segments = path.substring(1).split("/", 3);
        if (segments[0].equals("protocolo")) {
            return "protocolos";
        }
        return segments[0];
    }
}
