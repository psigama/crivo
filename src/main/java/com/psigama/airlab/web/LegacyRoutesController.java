package com.psigama.airlab.web;

import com.psigama.airlab.catalog.Protocol;
import com.psigama.airlab.catalog.ProtocolCatalog;
import com.psigama.airlab.support.Language;
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
 * <p>Since SPEC 0.28 (section 1.5, rule 1) the English page of the researchers' overview moved
 * from {@code /pesquisa/en} to {@code /en/pesquisa}, where every English address is, and the old
 * one — published in the footer and quoted outside the project — redirects here too.
 *
 * <p>Only GET requests are redirected. The forms of a test post to the new addresses, and a stale
 * form from an old page would have nothing safe to resume.
 */
@Controller
class LegacyRoutesController {

    /** The one English page there was until SPEC 0.27, now one address among the others. */
    @GetMapping("/pesquisa/en")
    RedirectView research() {
        return permanent(Language.EN.prefix() + "/pesquisa");
    }

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
