package io.github.rogeriocisi.airlab.web;

import io.github.rogeriocisi.airlab.audit.AuditTrail;
import io.github.rogeriocisi.airlab.study.StudyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Public pages. They say what the platform is, how to audit it and where the code lives, and
 * show the state of the audit trail, which is public by design (Constitution P7). Nothing here
 * says anything about participants, readings or allocations.
 *
 * <p>Since SPEC 0.21 (section 1.3) the editorial pages ({@link SitePages}) are a static site of
 * their own. This controller still serves them while {@code airlab.site-url} is blank, so the local
 * profile keeps one process for everything; once it is set, each of them answers with a permanent
 * redirect to the same path on the site. The state of the trail comes from the database and stays
 * here, at {@code /verificar}: a static page would show a frozen hash.
 */
@Controller
class HomeController {

    private final AuditTrail auditTrail;
    private final StudyService studies;
    private final Environment environment;
    private final String siteUrl;

    HomeController(AuditTrail auditTrail, StudyService studies, Environment environment,
                   @Value("${airlab.site-url:}") String siteUrl) {
        this.auditTrail = auditTrail;
        this.studies = studies;
        this.environment = environment;
        this.siteUrl = SitePages.base(siteUrl, "airlab.site-url (AIRLAB_SITE_URL)");
    }

    /** The editorial pages. Every path of {@link SitePages#ALL} is here, and a test checks it. */
    @GetMapping({"/", "/participar", "/por-que", "/garantias", "/auditar", "/historia", "/referencias", "/sobre"})
    ModelAndView sitePage(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        SitePages.Page page = SitePages.byPath(path.isEmpty() ? "/" : path)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (siteUrl.isEmpty()) {
            return new ModelAndView(page.template());
        }
        RedirectView redirect = new RedirectView(siteUrl + page.path());
        redirect.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        redirect.setPropagateQueryParams(true);
        return new ModelAndView(redirect);
    }

    /** The state of this installation's audit trail, read from the database on each request. */
    @GetMapping("/verificar")
    String trailState(Model model) {
        model.addAttribute("auditEvents", auditTrail.size());
        model.addAttribute("auditHead", auditTrail.headHash());
        model.addAttribute("studyCount", studies.all().size());
        return "verificar";
    }

    /**
     * Page for the person who will rate the readings. Separate from the rest of the site on
     * purpose: they arrive by an invitation link, usually grieving, and need the procedure and
     * their rights in plain words, not the argument for the platform (Constitution P6). It stays
     * in the application, not on the static site: what participants are told about the procedure
     * and their rights must not change with a push during a study (SPEC 1.3, rule 2).
     */
    @GetMapping("/consulente")
    String participant() {
        return "consulente";
    }

    /**
     * Sign-in page for whoever runs this installation.
     *
     * <p>It tells the visitor what the login is actually for, because today there is no
     * researcher screen behind it: the account exists to protect what comes next, and the
     * public pages are the whole surface. Saying so is better than a bare form that looks
     * like a locked door with a room behind it.
     */
    @GetMapping("/login")
    String login(Model model) {
        model.addAttribute("localProfile", environment.matchesProfiles("local", "test"));
        return "login";
    }
}
