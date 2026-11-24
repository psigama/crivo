package io.github.rogeriocisi.airlab.web;

import io.github.rogeriocisi.airlab.support.Language;
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
 * application is the current host, so {@code aplicacao} is the context path, and {@code site} is
 * {@code airlab.site-url} — empty too in the local profile, where this process serves both sides.
 * The generator of the static site gives the same variables the other way round.
 *
 * <p>Since SPEC 0.28 (section 1.5) those two prefixes also carry the <strong>language</strong>:
 * on an English page {@code aplicacao} is the context path that {@link LanguageFilter} made of
 * {@code /en}, and {@code site} ends in {@code /en}. That is the whole reason no template writes
 * {@code /en} by hand — an internal link that starts with {@code ${aplicacao}}, with {@code ${site}}
 * or with {@code @{...}} is already in the language of the page.
 */
@ControllerAdvice
class NavigationAdvice {

    private final String siteUrl;
    private final String shareImage;

    NavigationAdvice(@Value("${airlab.site-url:}") String siteUrl,
                     @Value("${airlab.base-url:}") String baseUrl) {
        this.siteUrl = SitePages.base(siteUrl, "airlab.site-url (AIRLAB_SITE_URL)");
        // A imagem de compartilhamento precisa de endereço absoluto (SPEC 0.23, seção 1.4, regra 6),
        // e ela está onde estão as páginas editoriais: no site, quando ele mora noutro endereço, e
        // nesta própria instalação, quando não — e aí o endereço absoluto é o `airlab.base-url`,
        // o endereço público desta instalação (SPEC 0.29, seção 1.3). Vazio, nos testes, o caminho
        // fica relativo, como antes.
        String raiz = siteUrl == null || siteUrl.isBlank()
                ? SitePages.base(baseUrl, "airlab.base-url (AIRLAB_BASE_URL)")
                : this.siteUrl;
        this.shareImage = raiz + "/img/compartilhar.png";
    }

    @ModelAttribute("secaoAtual")
    String currentSection(HttpServletRequest request) {
        return sectionOf(LanguagePages.pagePath(request));
    }

    /**
     * Whether someone is signed in, so the header offers "Painel" instead of "Entrar". Only a
     * boolean: who it is stays out of the shared layout.
     */
    @ModelAttribute("logado")
    boolean signedIn(HttpServletRequest request) {
        return request.getUserPrincipal() != null;
    }

    /** The language of this page, {@code pt} or {@code en}, for the text that comes from the code. */
    @ModelAttribute("idioma")
    String language(HttpServletRequest request) {
        return LanguagePages.of(request).code();
    }

    /** Where the editorial pages live, prefixed to every link that goes to them, language included. */
    @ModelAttribute("site")
    String site(HttpServletRequest request) {
        return siteUrl + LanguagePages.of(request).prefix();
    }

    /**
     * Where the application lives: this very host, so links to it stay relative. On an English page
     * this is {@code /en}, the context path of the request.
     */
    @ModelAttribute("aplicacao")
    String application(HttpServletRequest request) {
        String context = request.getContextPath();
        return context == null ? "" : context;
    }

    /**
     * The absolute address of the share image, which every page declares as its {@code og:image}
     * (SPEC 0.23, section 1.4, rule 6). Absolute because Open Graph requires it, and the same in
     * both languages.
     */
    @ModelAttribute("imagemSocial")
    String shareImage() {
        return shareImage;
    }

    /** This same page in Portuguese, clean, for {@code hreflang} and for the language script. */
    @ModelAttribute("urlPt")
    String portuguese(HttpServletRequest request) {
        return LanguagePages.addressIn(request, Language.PT, root(request), false);
    }

    /** This same page in English, clean. */
    @ModelAttribute("urlEn")
    String english(HttpServletRequest request) {
        return LanguagePages.addressIn(request, Language.EN, root(request), false);
    }

    /**
     * The same two addresses for the selector in the header, which also records the choice
     * ({@code ?idioma=pt}, {@code ?idioma=en}) so that the first-visit forward never undoes it.
     */
    @ModelAttribute("linkPt")
    String choosePortuguese(HttpServletRequest request) {
        return LanguagePages.addressIn(request, Language.PT, root(request), true);
    }

    @ModelAttribute("linkEn")
    String chooseEnglish(HttpServletRequest request) {
        return LanguagePages.addressIn(request, Language.EN, root(request), true);
    }

    /**
     * The address of the application without the language prefix, which is the real context path:
     * what the two addresses of one page are built from.
     */
    private static String root(HttpServletRequest request) {
        String context = request.getContextPath() == null ? "" : request.getContextPath();
        if (LanguagePages.of(request) == Language.EN && context.endsWith(Language.EN.prefix())) {
            return context.substring(0, context.length() - Language.EN.prefix().length());
        }
        return context;
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
