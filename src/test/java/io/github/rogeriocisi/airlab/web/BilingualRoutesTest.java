package io.github.rogeriocisi.airlab.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.rogeriocisi.airlab.support.Language;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * SPEC 0.28, section 1.5: the interface in two languages, one address each.
 *
 * <p>What is checked here is the acceptance of the section: every page answers at its Portuguese
 * address and at the same address under {@code /en}; the old English address redirects; the first
 * visit of a browser that does not ask for Portuguese is forwarded once and then remembered; a
 * {@code POST} is never forwarded; the selector is on every page and leads to the same page; an
 * English address with no translation answers in Portuguese and says so; and — the invariant that
 * keeps the whole scheme honest — no internal link of an English page drops the reader back into
 * Portuguese.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BilingualRoutesTest {

    /** The public pages of the application, without the language prefix. */
    private static final List<String> PAGES = List.of("/", "/protocolos", "/protocolo/P1", "/protocolo/P2",
            "/protocolo/P3", "/protocolo/P4", "/pesquisa", "/consulente", "/medium", "/perguntas", "/login",
            "/conta/criar", "/verificar", "/estudos", "/carimbos", "/participar", "/por-que", "/garantias",
            "/auditar", "/historia", "/referencias", "/sobre");

    private static final Pattern HREF = Pattern.compile("href=\"(/[^\"]*)\"");

    /** Paths that are the same bytes in both languages, so they carry no prefix. */
    private static final Pattern ASSET =
            Pattern.compile("^/(css/|js/|img/|targets/|favicon\\.svg|favicon\\.ico).*");

    @Autowired
    MockMvc mvc;

    @ParameterizedTest
    @ValueSource(strings = {"/", "/protocolos", "/protocolo/P1", "/pesquisa", "/consulente", "/medium",
            "/perguntas", "/login", "/conta/criar", "/verificar", "/estudos", "/carimbos", "/garantias"})
    @DisplayName("regra 1: cada pagina responde no endereco em portugues e no mesmo endereco sob /en")
    void every_page_answers_in_both_languages(String path) throws Exception {
        mvc.perform(get(path)).andExpect(status().isOk());
        mvc.perform(get(Language.EN.prefix() + path)).andExpect(status().isOk());
    }

    @Test
    @DisplayName("regra 1: /en sem barra e o endereco antigo da pagina em ingles redirecionam")
    void the_addresses_that_moved_redirect() throws Exception {
        mvc.perform(get("/en"))
                .andExpect(status().isMovedPermanently())
                .andExpect(header().string("Location", "/en/"));
        mvc.perform(get("/pesquisa/en"))
                .andExpect(status().isMovedPermanently())
                .andExpect(header().string("Location", "/en/pesquisa"));
    }

    @Test
    @DisplayName("regra 2: navegador em ingles, sem escolha feita, e levado uma vez para o ingles")
    void the_first_visit_goes_to_the_language_of_the_browser() throws Exception {
        MvcResult forwarded = mvc.perform(get("/protocolos").header("Accept-Language", "en-US,en;q=0.9"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/en/protocolos"))
                .andExpect(header().string("Vary", "Accept-Language"))
                .andReturn();
        assertThat(cookie(forwarded)).isEqualTo(Language.EN.code());

        // Portuguese browser: stays, and the choice is remembered all the same.
        MvcResult kept = mvc.perform(get("/protocolos").header("Accept-Language", "pt-BR,pt;q=0.9,en;q=0.8"))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(cookie(kept)).isEqualTo(Language.PT.code());

        // A browser that asks for neither language is not forwarded anywhere.
        mvc.perform(get("/protocolos").header("Accept-Language", "de-DE,de;q=0.9"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("regra 2: quem ja escolheu nao e mais encaminhado, e um POST nunca e encaminhado")
    void a_choice_already_made_is_not_undone() throws Exception {
        mvc.perform(get("/protocolos")
                        .header("Accept-Language", "en-US,en;q=0.9")
                        .cookie(new Cookie(Language.CHOICE, Language.PT.code())))
                .andExpect(status().isOk());

        // No redirect on a POST: a form must never be answered with a change of language.
        mvc.perform(post("/login").header("Accept-Language", "en-US,en;q=0.9"))
                .andExpect(status().is(403));
    }

    @Test
    @DisplayName("regra 3: o seletor fixa a escolha e leva a mesma pagina, com o endereco limpo")
    void the_selector_switches_the_page_and_records_the_choice() throws Exception {
        MvcResult toPortuguese = mvc.perform(get("/en/protocolos").param(Language.CHOICE, Language.PT.code())
                        .header("Accept-Language", "en-US,en;q=0.9"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/protocolos"))
                .andReturn();
        assertThat(cookie(toPortuguese)).isEqualTo(Language.PT.code());

        MvcResult toEnglish = mvc.perform(get("/protocolos").param(Language.CHOICE, Language.EN.code()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/en/protocolos"))
                .andReturn();
        assertThat(cookie(toEnglish)).isEqualTo(Language.EN.code());
    }

    @Test
    @DisplayName("regras 3 e 4: toda pagina declara os dois enderecos e traz o seletor")
    void every_page_declares_both_addresses() throws Exception {
        for (String path : PAGES) {
            String portuguese = body(get(path));
            assertThat(portuguese).describedAs(path)
                    .contains("hreflang=\"pt-BR\" href=\"" + path + "\"")
                    .contains("hreflang=\"en\" href=\"" + Language.EN.prefix() + path + "\"")
                    .contains("hreflang=\"x-default\" href=\"" + path + "\"")
                    .contains("class=\"idiomas\"")
                    .contains("href=\"" + path + "?idioma=pt\"")
                    .contains("href=\"" + Language.EN.prefix() + path + "?idioma=en\"");

            String english = body(get(Language.EN.prefix() + path));
            assertThat(english).describedAs("/en" + path)
                    .contains("hreflang=\"pt-BR\" href=\"" + path + "\"")
                    .contains("hreflang=\"en\" href=\"" + Language.EN.prefix() + path + "\"");
        }
    }

    @Test
    @DisplayName("regra 5: pagina em ingles ou esta traduzida, ou responde em portugues e avisa")
    void an_untranslated_page_says_so() throws Exception {
        for (String path : PAGES) {
            String english = body(get(Language.EN.prefix() + path));
            boolean translated = english.contains("lang=\"en\"") && !english.contains("aviso-traducao");
            boolean warned = english.contains("aviso-traducao");
            assertThat(translated || warned)
                    .describedAs("/en%s precisa estar traduzida ou avisar que nao esta", path)
                    .isTrue();
        }
        // The overlay is what decides it, and it answers for a template that is really there.
        assertThat(LanguagePages.translated("site/index")).isTrue();
        assertThat(LanguagePages.translated("nao-existe")).isFalse();
    }

    @Test
    @DisplayName("regra 1: nenhum link interno de uma pagina em ingles volta para o portugues")
    void no_link_of_an_english_page_leaves_the_language() throws Exception {
        for (String path : PAGES) {
            String english = body(get(Language.EN.prefix() + path));
            List<String> escaped = new ArrayList<>();
            Matcher links = HREF.matcher(english);
            while (links.find()) {
                String target = links.group(1);
                if (ASSET.matcher(target).matches()
                        // The selector and the alternate address of the page are meant to leave it.
                        || target.endsWith("?idioma=pt") || target.equals(path)
                        || target.startsWith(Language.EN.prefix() + "/")) {
                    continue;
                }
                escaped.add(target);
            }
            assertThat(escaped).describedAs("links de /en%s que saem do ingles", path).isEmpty();
        }
    }

    private String body(org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder request)
            throws Exception {
        return mvc.perform(request).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    private static String cookie(MvcResult result) {
        Cookie cookie = result.getResponse().getCookie(Language.CHOICE);
        return cookie == null ? null : cookie.getValue();
    }
}
