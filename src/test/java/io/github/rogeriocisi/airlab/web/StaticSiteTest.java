package io.github.rogeriocisi.airlab.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.rogeriocisi.airlab.support.Language;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * The static site with the editorial pages (SPEC 0.21, section 1.3), as the generator writes it:
 * no Spring context and no database, exactly as in the {@code pages.yml} workflow.
 */
class StaticSiteTest {

    private static final String APP = "https://app.exemplo.test";
    private static final Pattern ROOT_RELATIVE_LINK = Pattern.compile("(?:href|src)=\"(/[^\"]*)\"");
    private static final Pattern EXPRESSION = Pattern.compile("([$*#@])\\{\\s*([^}]*)}");

    @TempDir
    Path dir;

    private Path site() throws IOException {
        Path site = dir.resolve("site");
        StaticSite.write(site, APP);
        return site;
    }

    private static String read(Path file) throws IOException {
        return Files.readString(file, StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("gera as paginas do site nos dois idiomas, a 404, o CSS, o favicon e os scripts, e nada mais")
    void writes_the_editorial_pages_and_nothing_else() throws IOException {
        List<Path> written = StaticSite.write(dir.resolve("site"), APP);

        List<String> expected = new ArrayList<>();
        // SPEC 0.28, secao 1.5, regra 1: cada pagina duas vezes, a inglesa sob en/.
        for (Language language : Language.values()) {
            SitePages.ALL.forEach(page -> expected.add(page.file(language)));
        }
        expected.add("404.html");
        expected.addAll(StaticSite.ASSETS);
        assertThat(written).extracting(path -> path.toString().replace('\\', '/'))
                .containsExactlyInAnyOrderElementsOf(expected);
        assertThat(expected).doesNotContain("protocolos.html", "consulente.html", "verificar.html");
    }

    @Test
    @DisplayName("os links para a aplicacao levam o endereco dela; os do site ficam relativos")
    void links_to_the_application_carry_its_address() throws IOException {
        Path site = site();
        // Nos dois idiomas: numa pagina em ingles, o link para outra pagina do site leva /en.
        Set<String> sitePaths = SitePages.everyPath().stream().collect(Collectors.toSet());

        for (Path page : htmlFiles(site)) {
            String html = read(page);
            Matcher link = ROOT_RELATIVE_LINK.matcher(html);
            while (link.find()) {
                // O seletor de idioma leva ?idioma=pt|en para a mesma pagina (SPEC 1.5, regra 3).
                String target = link.group(1).replaceAll("[?#].*$", "");
                boolean asset = StaticSite.ASSETS.stream().anyMatch(name -> target.equals("/" + name));
                assertThat(sitePaths.contains(target) || asset)
                        .as("%s aponta para %s, que não é página do site: link para a aplicação sem o endereço dela",
                                page.getFileName(), link.group(1))
                        .isTrue();
            }
            assertThat(html).as("%s leva a CSP numa meta", page.getFileName())
                    .contains("<meta http-equiv=\"Content-Security-Policy\" content=\"default-src 'self'\">");
        }

        String home = read(site.resolve("index.html"));
        assertThat(home)
                .contains("href=\"" + APP + "/protocolos\"")
                .contains("href=\"" + APP + "/protocolo/P1\"")
                .contains("href=\"" + APP + "/login\"")
                .contains("href=\"/participar\"")
                .contains("id=\"o-que-nao-e\"");
        assertThat(read(site.resolve("auditar.html"))).contains("href=\"" + APP + "/verificar\"");
    }

    @Test
    @DisplayName("a navegacao marca a pagina atual tambem no site, e o submenu Biblioteca")
    void the_navigation_marks_the_current_page() throws IOException {
        Path site = site();
        assertThat(read(site.resolve("historia.html")))
                .contains("href=\"/historia\" aria-current=\"page\"")
                .contains("class=\"submenu biblioteca atual\"");
        assertThat(read(site.resolve("garantias.html")))
                .contains("href=\"/garantias\" aria-current=\"page\"")
                .contains("class=\"submenu pesquisa atual\"")
                .doesNotContain("class=\"submenu biblioteca atual\"")
                .doesNotContain("href=\"/historia\" aria-current=\"page\"");
    }

    @Test
    @DisplayName("o artigo fica em /artigo/, numa pasta com index.html, e entra no submenu Biblioteca")
    void the_article_is_a_folder_page() throws IOException {
        Path site = site();
        assertThat(SitePages.byPath("/artigo/")).map(SitePages.Page::file).contains("artigo/index.html");
        assertThat(read(site.resolve("artigo/index.html")))
                .contains("href=\"/artigo/\" aria-current=\"page\"")
                .contains("class=\"submenu biblioteca atual\"")
                .contains("<h2>Referências</h2>");
        assertThat(read(site.resolve("index.html"))).contains("href=\"/artigo/\"");
    }

    @Test
    @DisplayName("a pagina 404 entrega ao script o endereco da aplicacao")
    void the_not_found_page_forwards_to_the_application() throws IOException {
        Path site = site();
        String notFound = read(site.resolve("404.html"));
        assertThat(notFound)
                .contains("data-aplicacao=\"" + APP + "\"")
                .contains("<script src=\"/js/encaminhar.js\"></script>")
                .contains("id=\"destino\"");
        assertThat(read(site.resolve("js/encaminhar.js"))).contains("window.location.replace(destino)");
        assertThat(Files.readAllBytes(site.resolve("css/crivo.css"))).isNotEmpty();
    }

    @Test
    @DisplayName("uma pagina do site so le do modelo o que o gerador entrega (SPEC 1.3, regra 3)")
    void editorial_templates_read_only_what_the_generator_gives() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<Resource> templates = new ArrayList<>(List.of(resolver.getResources("classpath*:templates/site/*.html")));
        // A traducao de uma pagina do site nao pode ler mais do que o original (SPEC 1.5, regra 5).
        templates.addAll(List.of(resolver.getResources(
                "classpath*:templates/" + Language.EN.code() + "/site/*.html")));
        assertThat(templates).isNotEmpty();

        Set<String> roots = new TreeSet<>();
        for (Resource template : templates) {
            String html = template.getContentAsString(StandardCharsets.UTF_8);
            Matcher expression = EXPRESSION.matcher(html);
            while (expression.find()) {
                assertThat(expression.group(1))
                        .as("%s: só expressões ${...}; mensagens, seleções e links @{...} precisam de contexto web",
                                template.getFilename())
                        .isEqualTo("$");
                String body = expression.group(2).strip();
                if (!body.startsWith("#")) {
                    roots.add(body.split("[^A-Za-z0-9_]", 2)[0]);
                }
            }
        }
        assertThat(SitePages.MODEL).containsAll(roots);
    }

    @Test
    @DisplayName("todo template do site e uma pagina listada, e toda pagina listada tem template")
    void every_template_is_a_listed_page() throws IOException {
        Set<String> templates = new HashSet<>();
        for (Resource template : new PathMatchingResourcePatternResolver()
                .getResources("classpath*:templates/site/*.html")) {
            templates.add("site/" + template.getFilename().replaceAll("\\.html$", ""));
        }
        Set<String> listed = SitePages.ALL.stream().map(SitePages.Page::template).collect(Collectors.toSet());
        listed.add(SitePages.NOT_FOUND_TEMPLATE);
        assertThat(templates).isEqualTo(listed);

        // Toda traducao de pagina do site corresponde a uma pagina listada; o contrario nao e
        // exigido, porque uma pagina sem traducao responde em portugues com aviso (regra 5).
        Set<String> english = new HashSet<>();
        for (Resource template : new PathMatchingResourcePatternResolver()
                .getResources("classpath*:templates/" + Language.EN.code() + "/site/*.html")) {
            english.add("site/" + template.getFilename().replaceAll("\\.html$", ""));
        }
        assertThat(listed).containsAll(english);
    }

    @Test
    @DisplayName("nunca escreve por cima: a pasta precisa estar vazia ou nao existir")
    void refuses_a_folder_that_is_not_empty() throws IOException {
        Path site = dir.resolve("site");
        Files.createDirectories(site);
        Files.writeString(site.resolve("velho.html"), "pagina de outra execucao");

        assertThatThrownBy(() -> StaticSite.write(site, APP))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("não está vazia");
        assertThat(Files.readString(site.resolve("velho.html"))).isEqualTo("pagina de outra execucao");
    }

    @Test
    @DisplayName("a linha de comando exige a pasta e um endereco http da aplicacao, e ignora opcoes do Spring")
    void the_command_line_requires_the_folder_and_the_address() throws IOException {
        ByteArrayOutputStream errors = new ByteArrayOutputStream();
        PrintStream err = new PrintStream(errors, true, StandardCharsets.UTF_8);
        PrintStream out = new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8);

        assertThat(StaticSite.run(new String[] {"site"}, out, err)).isEqualTo(1);
        assertThat(StaticSite.run(new String[] {"site", dir.resolve("a").toString()}, out, err)).isEqualTo(1);
        assertThat(StaticSite.run(new String[] {"site", dir.resolve("b").toString(), "--aplicacao=ftp://x.test"},
                out, err)).isEqualTo(1);
        assertThat(errors.toString(StandardCharsets.UTF_8)).contains("Uso: airlab site").contains("http ou https");

        Path ok = dir.resolve("c");
        int code = StaticSite.run(new String[] {"site", ok.toString(), "--aplicacao=" + APP + "/",
                "--spring.profiles.active=local", "--spring.main.web-application-type=none"}, out, err);
        assertThat(code).isEqualTo(0);
        assertThat(read(ok.resolve("index.html"))).contains("href=\"" + APP + "/protocolos\"");
    }

    private static List<Path> htmlFiles(Path site) throws IOException {
        try (Stream<Path> files = Files.walk(site)) {
            return files.filter(file -> file.toString().endsWith(".html")).sorted().toList();
        }
    }
}
