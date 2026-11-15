package io.github.rogeriocisi.airlab.web;

import io.github.rogeriocisi.airlab.catalog.Protocol;
import io.github.rogeriocisi.airlab.catalog.ProtocolCatalog;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

/**
 * The addresses used until SPEC 0.16, {@code /testes/...}, answer with a permanent redirect to
 * the new ones (SPEC 0.17, catalog rule 7). The PsiGama ESP page and links already shared
 * pointed at them, and a protocol page that moved must not become a dead link.
 *
 * <p>Only GET requests are redirected. The forms of a test post to the new addresses, and a stale
 * form from an old page would have nothing safe to resume.
 */
@Controller
class LegacyRoutesController {

    @GetMapping("/testes")
    RedirectView catalog() {
        return permanent("/protocolos");
    }

    @GetMapping("/testes/{slug}")
    RedirectView protocol(@PathVariable String slug) {
        return permanent("/protocolo/" + bySlug(slug).code());
    }

    @GetMapping("/testes/{slug}/rodar")
    RedirectView run(@PathVariable String slug) {
        return permanent("/protocolo/" + bySlug(slug).code() + "/rodar");
    }

    @GetMapping("/testes/{slug}/resultado")
    RedirectView result(@PathVariable String slug) {
        return permanent("/protocolo/" + bySlug(slug).code() + "/resultado");
    }

    private static Protocol bySlug(String slug) {
        return ProtocolCatalog.bySlug(slug).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private static RedirectView permanent(String url) {
        RedirectView view = new RedirectView(url);
        view.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return view;
    }
}
