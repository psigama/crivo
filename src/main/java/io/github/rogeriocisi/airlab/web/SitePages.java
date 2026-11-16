package io.github.rogeriocisi.airlab.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * The editorial pages of the site (SPEC 0.21, section 1.3): hand-written text, with nothing that
 * comes from the code that runs the protocols or from the database.
 *
 * <p>This list is the single source for both sides. The application serves these pages itself
 * while no site address is configured (the local profile, the tests) and answers them with a
 * permanent redirect once one is ({@link HomeController}); the generator writes exactly these
 * pages into the static site ({@link StaticSite}). A new editorial page is a template under
 * {@code templates/site/}, an entry here and the path in {@link HomeController}'s mapping.
 *
 * <p>The protocol pages, {@code /consulente} and everything else stay in the application: they
 * change only with a deploy (Constitution, deployment rules 8 and 9).
 */
final class SitePages {

    /** One editorial page: its address, its template and the file the generator writes. */
    record Page(String path, String template, String file) {
    }

    /**
     * The only model variables an editorial page may read. The generator has nothing else to give
     * it, which is what keeps the catalog, the limits and the questionnaire out of the site
     * (SPEC 1.3, rule 3); {@code StaticSiteTest} fails if a template reaches for anything more.
     * {@code paginasDoSite}, the paths of {@link #ALL} separated by spaces, is for the 404 page
     * only: its script never forwards one of them to the application, which would send it back.
     */
    static final Set<String> MODEL = Set.of("site", "aplicacao", "secaoAtual", "logado", "paginasDoSite");

    /** The page the static site answers with for every address it does not have (rule 5). */
    static final String NOT_FOUND_TEMPLATE = "site/404";

    static final List<Page> ALL = List.of(
            page("/", "index"),
            page("/participar", "participar"),
            page("/por-que", "por-que"),
            page("/garantias", "garantias"),
            page("/auditar", "auditar"),
            page("/historia", "historia"),
            page("/referencias", "referencias"),
            page("/sobre", "sobre"));

    private SitePages() {
    }

    static Optional<Page> byPath(String path) {
        return ALL.stream().filter(page -> page.path().equals(path)).findFirst();
    }

    /**
     * An address from the configuration, ready to be prefixed to a path: blank stays blank (links
     * stay relative), anything else must be an absolute http or https address and loses its
     * trailing slashes. A malformed value stops the start, never a silent default (Constitution,
     * command line rule 3).
     */
    static String base(String address, String name) {
        if (address == null || address.isBlank()) {
            return "";
        }
        String trimmed = address.strip().replaceAll("/+$", "");
        try {
            URI uri = new URI(trimmed);
            boolean web = "https".equals(uri.getScheme()) || "http".equals(uri.getScheme());
            if (!web || uri.getHost() == null || uri.getRawQuery() != null || uri.getRawFragment() != null) {
                throw new IllegalArgumentException(
                        name + " precisa ser um endereço http ou https completo, como https://crivo.psigama.com");
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(
                    name + " precisa ser um endereço http ou https completo, como https://crivo.psigama.com", e);
        }
        return trimmed;
    }

    private static Page page(String path, String name) {
        String file = path.equals("/") ? "index.html" : path.substring(1) + ".html";
        return new Page(path, "site/" + name, file);
    }
}
