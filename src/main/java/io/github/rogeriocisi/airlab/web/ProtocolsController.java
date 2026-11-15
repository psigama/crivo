package io.github.rogeriocisi.airlab.web;

import io.github.rogeriocisi.airlab.catalog.Protocol;
import io.github.rogeriocisi.airlab.catalog.ProtocolCatalog;
import io.github.rogeriocisi.airlab.catalog.Questionnaires;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

/**
 * The protocols section: the catalog and one page per protocol (SPEC, section 1.1, rule 7).
 *
 * <p>Since SPEC 0.17 the catalog lists all four protocols in code order, the AIR ones apart from
 * the ESP tests, and each protocol has its page at {@code /protocolo/P1} to {@code /protocolo/P4}.
 * The old {@code /testes/...} addresses redirect here ({@link LegacyRoutesController}).
 *
 * <p>Each protocol page prints what that protocol's result does not support, straight from the
 * catalog, because catalog rule 2 makes it mandatory text rather than a footnote. The page of
 * the automated protocol also publishes the questionnaire in force, field by field: it is public
 * by design, frozen into every preregistration and part of every commitment.
 */
@Controller
class ProtocolsController {

    @GetMapping("/protocolos")
    String catalog(Model model) {
        model.addAttribute("air", ProtocolCatalog.air());
        model.addAttribute("esp", ProtocolCatalog.esp());
        return "protocolos";
    }

    @GetMapping("/protocolo/{code}")
    String protocol(@PathVariable String code, Model model) {
        Protocol protocol = ProtocolCatalog.byCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!protocol.code().equals(code)) {
            // p1 and P1 are the same page; the address people share is the canonical one.
            return "redirect:/protocolo/" + protocol.code();
        }
        model.addAttribute("protocolo", protocol);
        if (protocol.equals(ProtocolCatalog.structuredReading())) {
            model.addAttribute("questionario", Questionnaires.current());
        }
        return "protocolo";
    }
}
