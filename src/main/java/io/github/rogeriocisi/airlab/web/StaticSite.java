package io.github.rogeriocisi.airlab.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * {@code airlab site <pasta> --aplicacao=<endereço>}: writes the static site with the editorial
 * pages (SPEC 0.21, section 1.3), which the workflow {@code pages.yml} publishes on the GitHub Pages
 * at every push to {@code main}.
 *
 * <p>It runs before, and instead of, the Spring context ({@code AirLabApplication#main}): no
 * database, no web server, no configuration. The templates are the very ones the application serves
 * in the local profile, rendered by the same Thymeleaf dialect, with SpEL. Each page gets only
 * {@link SitePages#MODEL} — the two addresses, its section and "not signed in" — so it has no way
 * to reach the protocol catalog or the database (rule 3). Links to the site stay relative, and
 * links to the application carry its address.
 *
 * <p>Besides the pages it writes the stylesheet, the favicon, the share image, and the
 * {@code 404.html} with its script, which takes any address the site does not have to the
 * application (rule 5). The share image is there because the application's pages point their
 * {@code og:image} at the site (SPEC 0.23, section 1.4, rule 6). The output depends only on the
 * templates and the address, so two runs over the same commit are identical.
 */
public final class StaticSite {

    /** Files under {@code static/} that the site needs, at the same path. */
    static final List<String> ASSETS =
            List.of("css/crivo.css", "favicon.svg", "js/encaminhar.js", "img/compartilhar.png");

    private static final String USAGE =
            "Uso: airlab site <pasta> --aplicacao=<endereço da aplicação, como https://app.crivo.psigama.com>";

    private StaticSite() {
    }

    /**
     * The command line entry point. Options starting with {@code --} other than {@code --aplicacao}
     * are ignored: the CLI scripts pass Spring options to every command, and this one never starts
     * Spring.
     *
     * @return the exit code: 0 when the site was written, 1 otherwise
     */
    public static int run(String[] args, PrintStream out, PrintStream err) {
        List<String> positional = new ArrayList<>();
        String application = null;
        for (String arg : args) {
            if (arg.startsWith("--aplicacao=")) {
                application = arg.substring("--aplicacao=".length());
            } else if (!arg.startsWith("--")) {
                positional.add(arg);
            }
        }
        if (positional.size() != 2 || !positional.get(0).equals("site")
                || application == null || application.isBlank()) {
            err.println(USAGE);
            return 1;
        }
        Path target = Path.of(positional.get(1)).toAbsolutePath().normalize();
        try {
            List<Path> written = write(target, SitePages.base(application, "--aplicacao"));
            out.println("Site gerado em " + target + ": " + written.size() + " arquivos.");
            return 0;
        } catch (IllegalArgumentException | IOException e) {
            err.println("Site não gerado: " + e.getMessage());
            return 1;
        }
    }

    /**
     * Writes the site into {@code target}, which must be missing or empty: the command never
     * deletes anything, and a leftover page from an earlier run would be published as if current.
     *
     * @param application the application's address, as returned by {@link SitePages#base}
     * @return the files written, relative to {@code target}
     */
    static List<Path> write(Path target, String application) throws IOException {
        if (application.isEmpty()) {
            throw new IllegalArgumentException("o endereço da aplicação é obrigatório");
        }
        if (Files.exists(target)) {
            try (Stream<Path> entries = Files.list(target)) {
                if (entries.findAny().isPresent()) {
                    throw new IOException("a pasta " + target + " não está vazia; apague-a ou escolha outra");
                }
            }
        }
        SpringTemplateEngine engine = engine();
        List<Path> written = new ArrayList<>();
        for (SitePages.Page page : SitePages.ALL) {
            String html = render(engine, page.template(), NavigationAdvice.sectionOf(page.path()), application);
            written.add(save(target, page.file(), html.getBytes(StandardCharsets.UTF_8)));
        }
        String notFound = render(engine, SitePages.NOT_FOUND_TEMPLATE, "erro", application);
        written.add(save(target, "404.html", notFound.getBytes(StandardCharsets.UTF_8)));
        for (String asset : ASSETS) {
            written.add(save(target, asset, resource("static/" + asset)));
        }
        return written;
    }

    private static SpringTemplateEngine engine() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver(StaticSite.class.getClassLoader());
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);
        return engine;
    }

    /** One page, with nothing in the model but {@link SitePages#MODEL}. */
    private static String render(SpringTemplateEngine engine, String template, String section, String application) {
        Context context = new Context(Locale.forLanguageTag("pt-BR"));
        context.setVariable("site", "");
        context.setVariable("aplicacao", application);
        context.setVariable("secaoAtual", section);
        context.setVariable("logado", false);
        context.setVariable("paginasDoSite", String.join(" ",
                SitePages.ALL.stream().map(SitePages.Page::path).toList()));
        return engine.process(template, context);
    }

    private static byte[] resource(String name) throws IOException {
        try (InputStream in = StaticSite.class.getClassLoader().getResourceAsStream(name)) {
            if (in == null) {
                throw new IOException("recurso ausente no artefato: " + name);
            }
            return in.readAllBytes();
        }
    }

    private static Path save(Path target, String file, byte[] content) throws IOException {
        Path destination = target.resolve(file);
        Files.createDirectories(destination.getParent());
        Files.write(destination, content);
        return target.relativize(destination);
    }
}
