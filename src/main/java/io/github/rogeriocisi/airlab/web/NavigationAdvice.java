package io.github.rogeriocisi.airlab.web;

import io.github.rogeriocisi.airlab.catalog.Protocol;
import io.github.rogeriocisi.airlab.catalog.ProtocolCatalog;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Tells the site header which section the page belongs to, so the main navigation can mark
 * it with {@code aria-current="page"}.
 *
 * <p>Only the top level section is exposed, never the full path: a protocol page under
 * {@code /testes/...} marks "Protocolos", and nothing about the session leaks into the model.
 * The ESP tests are the exception: PsiGama presents them since SPEC 0.16, so their pages are
 * not marked as part of the Crivo protocols section, although they live under the same path.
 */
@ControllerAdvice
class NavigationAdvice {

    /** Section of the ESP test pages: no navigation item has it, so none is marked. */
    private static final String ESP_SECTION = "pes";

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
        if (segments[0].equals("testes") && segments.length > 1
                && ProtocolCatalog.bySlug(segments[1]).filter(Protocol::presentedByPsiGama).isPresent()) {
            return ESP_SECTION;
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
