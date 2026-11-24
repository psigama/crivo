package com.psigama.airlab.web;

import com.psigama.airlab.structured.StructuredStudyService;
import com.psigama.airlab.structured.StructuredStudyView;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * The public page of the P1 studies (SPEC E13, phase E): what was frozen, with its hash; counts
 * while a study collects; the whole report once it is unblinded. Only what is public by
 * construction (P2, P7) — the report does not exist before the unblinding, so there is nothing to
 * hide early.
 */
@Controller
class PublicStudiesController {

    private final StructuredStudyService studies;

    PublicStudiesController(StructuredStudyService studies) {
        this.studies = studies;
    }

    @GetMapping("/estudos")
    String list(Model model) {
        model.addAttribute("aberto", studies.current().orElse(null));
        model.addAttribute("concluidos", studies.concluded());
        return "estudos";
    }

    @GetMapping("/estudos/{code}")
    String study(@PathVariable String code, Model model) {
        Optional<StructuredStudyView> study = studies.byCode(code);
        if (study.isEmpty()) {
            return "redirect:/estudos";
        }
        boolean open = studies.current().map(current -> current.studyCode().equals(code)).orElse(false);
        model.addAttribute("estudo", study.get());
        model.addAttribute("progresso", open ? studies.progress() : null);
        model.addAttribute("relatorio", studies.report(code).orElse(null));
        model.addAttribute("pacote", studies.verificationPackage(code).orElse(null));
        return "estudo-publico";
    }

    /**
     * The verification package (SPEC E9), byte for byte as stored, so its SHA-256 is the one in the
     * trail. 404 for a study still collecting: the package does not exist before the unblinding.
     */
    @GetMapping("/estudos/{code}/pacote.json")
    ResponseEntity<byte[]> verificationPackage(@PathVariable String code) {
        return studies.verificationPackage(code)
                .map(pacote -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                ContentDisposition.attachment().filename(pacote.fileName()).build().toString())
                        .body(pacote.document().getBytes(StandardCharsets.UTF_8)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
