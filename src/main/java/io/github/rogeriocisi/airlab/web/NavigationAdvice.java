package io.github.rogeriocisi.airlab.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Tells the site header which section the page belongs to, so the main navigation can mark
 * it with {@code aria-current="page"}.
 *
 * <p>Only the top level section is exposed, never the full path: the catalog,
 * {@code /protocolos}, and every protocol page under {@code /protocolo/...} mark "Protocolos",
 * and nothing about the session leaks into the model. Since SPEC 0.17 the ESP tests are part of
 * that section again.
 */
@ControllerAdvice
class NavigationAdvice {

    @ModelAttribute("secaoAtual")
    String currentSection(HttpServletRequest request) {
        String path = request.getRequestURI();
        String context = request.getContextPath();
        if (context != null && !context.isEmpty() && path.startsWith(context)) {
            path = path.substring(context.length());
        }
        if (path.isEmpty() || path.equals("/")) {
            return "inicio";
        }
        String[] segments = path.substring(1).split("/", 3);
        if (segments[0].equals("protocolo")) {
            return "protocolos";
        }
        return segments[0];
    }

    /**
     * Whether someone is signed in, so the header offers "Painel" instead of "Entrar". Only a
     * boolean: who it is stays out of the shared layout.
     */
    @ModelAttribute("logado")
    boolean signedIn(HttpServletRequest request) {
        return request.getUserPrincipal() != null;
    }
}
