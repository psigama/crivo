package io.github.rogeriocisi.airlab.web;

import io.github.rogeriocisi.airlab.support.Language;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

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

    /**
     * One editorial page: its address and its template, in Portuguese. The English version of both
     * is the same with the language prefix (SPEC 0.28, section 1.5, rules 1 and 5).
     */
    record Page(String path, String template, String file) {

        /** The address of this page in a language: {@code /garantias} and {@code /en/garantias}. */
        String path(Language language) {
            return language.prefix() + path;
        }

        /** The file the generator writes for a language: {@code garantias.html}, {@code en/garantias.html}. */
        String file(Language language) {
            return language.isDefault() ? file : language.code() + "/" + file;
        }

        /**
         * The template of this page in a language, which for English exists only if it has been
         * translated; when it has not, the Portuguese one answers with a notice (rule 5).
         */
        String template(Language language) {
            return language.isDefault() ? template : language.code() + "/" + template;
        }
    }

    /**
     * The only model variables an editorial page may read. The generator has nothing else to give
     * it, which is what keeps the catalog, the limits and the questionnaire out of the site
     * (SPEC 1.3, rule 3); {@code StaticSiteTest} fails if a template reaches for anything more.
     * {@code paginasDoSite}, the paths of {@link #ALL} separated by spaces, is for the 404 page
     * only: its script never forwards one of them to the application, which would send it back.
     * Since SPEC 0.28 (section 1.5) the language is in there too: {@code idioma}, the two addresses
     * of this page ({@code linkPt} and {@code linkEn}) and whether it is answering untranslated.
     */
    static final Set<String> MODEL = Set.of("site", "aplicacao", "secaoAtual", "logado", "paginasDoSite",
            "idioma", "urlPt", "urlEn", "linkPt", "linkEn", "traducaoPendente");

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
            page("/sobre", "sobre"),
            page("/artigo/", "artigo"));

    private SitePages() {
    }

    static Optional<Page> byPath(String path) {
        return ALL.stream().filter(page -> page.path().equals(path)).findFirst();
    }

    /** Every address of the site, in both languages: what the 404 page must never forward (rule 5). */
    static List<String> everyPath() {
        return Stream.of(Language.values())
                .flatMap(language -> ALL.stream().map(page -> page.path(language)))
                .toList();
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

    /**
     * A path ending in a slash is a folder with its {@code index.html}, as the GitHub Pages serves
     * it; any other is a file named after it, which the Pages serves without the extension.
     */
    private static Page page(String path, String name) {
        String file = path.endsWith("/") ? path.substring(1) + "index.html" : path.substring(1) + ".html";
        return new Page(path, "site/" + name, file);
    }
}
