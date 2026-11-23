package io.github.rogeriocisi.airlab.web;

import io.github.rogeriocisi.airlab.support.Language;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.context.support.ResourceBundleMessageSource;
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
    static final List<String> ASSETS = List.of("css/crivo.css", "favicon.svg", "js/encaminhar.js",
            "js/idioma.js", "img/compartilhar.png");

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
        List<Path> written = new ArrayList<>();
        for (Language language : Language.values()) {
            SpringTemplateEngine engine = engine(language);
            for (SitePages.Page page : SitePages.ALL) {
                String html = render(engine, language, page.template(), page.path(),
                        NavigationAdvice.sectionOf(page.path()), application);
                written.add(save(target, page.file(language), html.getBytes(StandardCharsets.UTF_8)));
            }
        }
        // One 404 for the whole site: the GitHub Pages serves this very file for any address it
        // does not have, in either language, and its script forwards to the application.
        String notFound = render(engine(Language.PT), Language.PT, SitePages.NOT_FOUND_TEMPLATE, "/", "erro",
                application);
        written.add(save(target, "404.html", notFound.getBytes(StandardCharsets.UTF_8)));
        for (String asset : ASSETS) {
            written.add(save(target, asset, resource("static/" + asset)));
        }
        return written;
    }

    /**
     * One engine per language. The English one looks under {@code templates/en/} first and falls
     * back to the Portuguese template when the page has not been translated yet (SPEC 0.28, section
     * 1.5, rule 5) — the same overlay the application uses, built here by hand because this command
     * never starts Spring.
     */
    private static SpringTemplateEngine engine(Language language) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        if (language == Language.EN) {
            ClassLoaderTemplateResolver english = resolver("templates/" + Language.EN.code() + "/");
            english.setCheckExistence(true);
            english.setOrder(1);
            engine.addTemplateResolver(english);
        }
        ClassLoaderTemplateResolver portuguese = resolver("templates/");
        portuguese.setOrder(2);
        engine.addTemplateResolver(portuguese);
        engine.setTemplateEngineMessageSource(messages());
        return engine;
    }

    private static ClassLoaderTemplateResolver resolver(String prefix) {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver(StaticSite.class.getClassLoader());
        resolver.setPrefix(prefix);
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return resolver;
    }

    /** The same message bundle the application uses for the shared header, footer and buttons. */
    private static ResourceBundleMessageSource messages() {
        ResourceBundleMessageSource messages = new ResourceBundleMessageSource();
        messages.setBasename("messages");
        messages.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messages.setFallbackToSystemLocale(false);
        return messages;
    }

    /** Whether a page has its own English template, or will answer in Portuguese with a notice. */
    private static boolean translated(String template) {
        String name = "templates/" + Language.EN.code() + "/" + template + ".html";
        return StaticSite.class.getClassLoader().getResource(name) != null;
    }

    /**
     * One page, with nothing in the model but {@link SitePages#MODEL}. The two addresses of the
     * page are relative, like every other link of the site: the generator knows where the
     * application is, never where the site itself is published.
     */
    private static String render(SpringTemplateEngine engine, Language language, String template, String path,
                                 String section, String application) {
        Context context = new Context(language.locale());
        context.setVariable("site", language.prefix());
        context.setVariable("aplicacao", application + language.prefix());
        context.setVariable("secaoAtual", section);
        context.setVariable("logado", false);
        context.setVariable("idioma", language.code());
        context.setVariable("urlPt", Language.PT.prefix() + path);
        context.setVariable("urlEn", Language.EN.prefix() + path);
        context.setVariable("linkPt", Language.PT.prefix() + path + "?" + Language.CHOICE + "=" + Language.PT.code());
        context.setVariable("linkEn", Language.EN.prefix() + path + "?" + Language.CHOICE + "=" + Language.EN.code());
        context.setVariable("traducaoPendente", language == Language.EN && !translated(template));
        context.setVariable("paginasDoSite", String.join(" ", SitePages.everyPath()));
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
