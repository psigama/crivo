package com.psigama.airlab.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.psigama.airlab.support.Language;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * SPEC 0.28, section 1.5, rule 1, with the template cache on — which is how production runs
 * ({@code application-prod.yml}) and how no other test does.
 *
 * <p>A page has one template name and two templates, one per language. A cache that keeps the
 * parsed template by name alone hands the first language that asked for a page to everyone who
 * asks afterwards, at both addresses. Each test here asks for a page in one language, then in the
 * other, then in the first again, and each test uses pages no other test touches, because the
 * cache outlives the test method.
 */
@SpringBootTest(properties = "spring.thymeleaf.cache=true")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TemplateCacheLanguageTest {

    private static final String PORTUGUESE = "<html lang=\"pt-BR\"";
    private static final String ENGLISH = "<html lang=\"en\"";

    @Autowired
    MockMvc mvc;

    @ParameterizedTest
    @ValueSource(strings = {"/protocolos", "/carimbos", "/verificar", "/garantias"})
    @DisplayName("cache ligado: a pagina pedida primeiro em portugues continua em ingles no endereco /en")
    void portuguese_first_does_not_fix_the_language(String path) throws Exception {
        assertThat(page(path)).contains(PORTUGUESE);
        assertThat(page(Language.EN.prefix() + path)).contains(ENGLISH);
        assertThat(page(path)).contains(PORTUGUESE);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/login", "/estudos", "/pesquisa", "/perguntas", "/participar"})
    @DisplayName("cache ligado: a pagina pedida primeiro em ingles continua em portugues no endereco de sempre")
    void english_first_does_not_fix_the_language(String path) throws Exception {
        assertThat(page(Language.EN.prefix() + path)).contains(ENGLISH);
        assertThat(page(path)).contains(PORTUGUESE);
        assertThat(page(Language.EN.prefix() + path)).contains(ENGLISH);
    }

    @Test
    @DisplayName("cache ligado: os fragmentos seguem o idioma da pagina que os inclui")
    void fragments_follow_the_language_of_the_page() throws Exception {
        String portuguese = "O que este experimento não é";
        String english = "What this experiment is not";

        assertThat(page("/protocolo/P1")).contains(portuguese).doesNotContain(english);
        assertThat(page("/en/protocolo/P1")).contains(english).doesNotContain(portuguese);
        assertThat(page("/protocolo/P1")).contains(portuguese).doesNotContain(english);
    }

    private String page(String address) throws Exception {
        return mvc.perform(get(address))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }
}
