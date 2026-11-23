package io.github.rogeriocisi.airlab.web;

import io.github.rogeriocisi.airlab.support.Language;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * The language of a request, decided before anything else runs (SPEC 0.28, section 1.5).
 *
 * <p>One address, one language (rule 1): the Portuguese pages keep the addresses they have, and the
 * English ones carry the prefix {@code /en}. This filter makes that prefix behave as if it were the
 * <em>context path</em> of the request, and nothing else in the application needs to know about it:
 * the handler mappings, the Spring Security patterns and {@code ServletRequestPathUtils} all work on
 * the path within the application, so {@code /en/protocolos} reaches the same controller as
 * {@code /protocolos}; and {@code @{...}} in a template, a {@code th:action} and a
 * {@code redirect:/...} from a controller all prefix the context path, so every link, form and
 * redirect of an English page stays in English on its own. <strong>No link writes {@code /en} by
 * hand.</strong>
 *
 * <p>It also carries rule 2, the first visit: whoever arrives at a Portuguese address without
 * having chosen a language, with a browser that does not ask for Portuguese, is sent once to the
 * English address. The choice is then remembered in the {@code idioma} cookie — which the selector
 * in the header also sets, through {@code ?idioma=pt} or {@code ?idioma=en} — so the forward never
 * happens twice and never interrupts a form. Only {@code GET} is ever forwarded. The language of
 * the browser is what the person configured: no address is looked up anywhere, nothing is asked of
 * a third party (Constitution P6 and P8).
 *
 * <p>The static site has no server to do this, and does the same in {@code js/idioma.js}. The two
 * never fight: by the time a page of the application is rendered, the cookie is already set, and
 * the script finds it and does nothing.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
class LanguageFilter extends OncePerRequestFilter {

    /** The language the rest of the request can read, set on every request. */
    static final String ATTRIBUTE = LanguageFilter.class.getName() + ".language";

    /** A year: a language preference is not a session. */
    private static final int COOKIE_MAX_AGE = 365 * 24 * 60 * 60;

    /**
     * Paths that are never a page, and so never change language nor get forwarded. The stylesheet,
     * the scripts, the images and the targets are the same bytes in both languages, and
     * {@code /actuator} is what the deployment checks.
     */
    private static final List<String> NOT_A_PAGE =
            List.of("/css/", "/js/", "/img/", "/targets/", "/favicon.svg", "/actuator", "/error");

    /**
     * The error page too. Spring forwards to {@code /error} in a dispatch of its own, which this
     * filter would skip by default, and the page would come out in Portuguese inside an English
     * visit. The address is still the one that was asked for, so the language is still known.
     */
    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String context = request.getContextPath();
        String path = pathWithinApplication(request, context);
        Language language = Language.ofPath(path);

        // "/en" without the slash: the container never redirects it, because /en is not a real
        // context path. Done once, with a permanent redirect, so the shared address is the final one.
        if (language == Language.EN && path.equals(Language.EN.prefix())) {
            response.setStatus(HttpStatus.MOVED_PERMANENTLY.value());
            response.setHeader("Location", context + Language.EN.prefix() + "/" + query(request));
            return;
        }

        Optional<Language> chosen = chosenInQuery(request);
        if (chosen.isPresent() && isPage(path)) {
            // The selector in the header: remember the choice and leave the address clean.
            remember(request, response, context, chosen.get());
            response.setStatus(HttpStatus.FOUND.value());
            response.setHeader("Location", context + chosen.get().prefix() + withoutLanguagePrefix(path)
                    + queryWithoutChoice(request));
            return;
        }

        if (language == Language.PT && isPage(path) && shouldForward(request)) {
            remember(request, response, context, Language.EN);
            response.setHeader("Vary", "Accept-Language");
            response.setStatus(HttpStatus.FOUND.value());
            response.setHeader("Location", context + Language.EN.prefix() + path + query(request));
            return;
        }

        if (isPage(path) && cookie(request).isEmpty()) {
            // Whatever language this page came out in is the one this browser keeps, so a later
            // page of the same flow is never forwarded away from it.
            remember(request, response, context, language);
        }

        request.setAttribute(ATTRIBUTE, language);
        if (language == Language.EN) {
            chain.doFilter(new EnglishRequest(request, context), response);
            return;
        }
        chain.doFilter(request, response);
    }

    /** The path of the request without the real context path, always starting with a slash. */
    private static String pathWithinApplication(HttpServletRequest request, String context) {
        String uri = request.getRequestURI();
        String path = context != null && !context.isEmpty() && uri.startsWith(context)
                ? uri.substring(context.length())
                : uri;
        return path.isEmpty() ? "/" : path;
    }

    private static String withoutLanguagePrefix(String path) {
        if (Language.ofPath(path) == Language.EN) {
            String rest = path.substring(Language.EN.prefix().length());
            return rest.isEmpty() ? "/" : rest;
        }
        return path;
    }

    private static boolean isPage(String path) {
        String withoutPrefix = withoutLanguagePrefix(path);
        return NOT_A_PAGE.stream().noneMatch(withoutPrefix::startsWith);
    }

    /** {@code ?idioma=pt} or {@code ?idioma=en}: an explicit choice, from the selector. */
    private static Optional<Language> chosenInQuery(HttpServletRequest request) {
        if (!HttpMethod.GET.matches(request.getMethod())) {
            return Optional.empty();
        }
        return Language.byCode(request.getParameter(Language.CHOICE));
    }

    /**
     * Whether this request is the first visit of a browser that does not ask for Portuguese. Only a
     * {@code GET}, only without a choice already made, and only when the header names a language
     * this system has: a header that asks for neither Portuguese nor English changes nothing.
     */
    private static boolean shouldForward(HttpServletRequest request) {
        if (!HttpMethod.GET.matches(request.getMethod()) || cookie(request).isPresent()) {
            return false;
        }
        return acceptedLanguage(request).filter(Language.EN::equals).isPresent();
    }

    private static Optional<Language> acceptedLanguage(HttpServletRequest request) {
        String header = request.getHeader("Accept-Language");
        if (header == null || header.isBlank()) {
            return Optional.empty();
        }
        try {
            return Language.preferred(Locale.LanguageRange.parse(header));
        } catch (IllegalArgumentException e) {
            // A malformed header is no reason to fail a page.
            return Optional.empty();
        }
    }

    private static Optional<Language> cookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }
        for (Cookie cookie : cookies) {
            if (Language.CHOICE.equals(cookie.getName())) {
                return Language.byCode(cookie.getValue());
            }
        }
        return Optional.empty();
    }

    /**
     * Writes the preference cookie. Readable by scripts on purpose: on the static site the same
     * cookie is read and written by {@code js/idioma.js}, and it holds nothing but {@code pt} or
     * {@code en}.
     */
    private static void remember(HttpServletRequest request, HttpServletResponse response, String context,
                                 Language language) {
        Cookie cookie = new Cookie(Language.CHOICE, language.code());
        cookie.setPath(context == null || context.isEmpty() ? "/" : context);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setHttpOnly(false);
        cookie.setSecure(request.isSecure());
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }

    private static String query(HttpServletRequest request) {
        String query = request.getQueryString();
        return query == null || query.isEmpty() ? "" : "?" + query;
    }

    /** The query string without the {@code idioma} parameter, so the address left over is clean. */
    private static String queryWithoutChoice(HttpServletRequest request) {
        String query = request.getQueryString();
        if (query == null || query.isEmpty()) {
            return "";
        }
        StringBuilder kept = new StringBuilder();
        for (String pair : query.split("&")) {
            String name = pair.split("=", 2)[0];
            if (name.equals(Language.CHOICE) || name.isEmpty()) {
                continue;
            }
            kept.append(kept.isEmpty() ? "?" : "&").append(pair);
        }
        return kept.toString();
    }

    /**
     * An English request seen by the rest of the application as if {@code /en} were its context
     * path. Nothing else of the request changes.
     */
    private static final class EnglishRequest extends HttpServletRequestWrapper {

        private final String context;

        private EnglishRequest(HttpServletRequest request, String realContext) {
            super(request);
            this.context = (realContext == null ? "" : realContext) + Language.EN.prefix();
        }

        @Override
        public String getContextPath() {
            return context;
        }

        @Override
        public String getServletPath() {
            String uri = super.getRequestURI();
            String path = uri.startsWith(context) ? uri.substring(context.length()) : uri;
            return path.isEmpty() ? "/" : path;
        }

        @Override
        public String getPathInfo() {
            return null;
        }
    }
}
