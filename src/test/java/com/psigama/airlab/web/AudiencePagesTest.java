package com.psigama.airlab.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.psigama.airlab.catalog.Protocol;
import com.psigama.airlab.catalog.ProtocolCatalog;
import com.psigama.airlab.participant.AccountService;
import com.psigama.airlab.support.Text;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.util.HtmlUtils;

/**
 * The second phase of SPEC section 1.4 (v0.24): three doors on the home page, the pages for
 * mediums, for questions and for researchers, the grouped menu, the guarantees in layers and the
 * consent term with its summary.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AudiencePagesTest {

    private static final String APP = "https://app.exemplo.test";

    @Autowired
    MockMvc mvc;

    @TempDir
    Path dir;

    @Test
    @DisplayName("a pagina inicial do site estatico abre com as tres portas, que levam a aplicacao")
    void the_home_page_has_three_doors() throws Exception {
        StaticSite.write(dir.resolve("site"), APP);
        String home = Files.readString(dir.resolve("site/index.html"), StandardCharsets.UTF_8);
        assertThat(home)
                .contains("class=\"entradas")
                .contains("href=\"" + APP + "/consulente\"")
                .contains("href=\"" + APP + "/medium\"")
                .contains("href=\"" + APP + "/pesquisa\"")
                .contains("Perdi alguém", "Sou médium", "Pesquiso ou quero conferir")
                // The technical paragraph stays, one tap away.
                .contains("class=\"detalhe-tecnico")
                .contains("recepção anômala de informação");
    }

    @Test
    @DisplayName("as paginas de medium, de perguntas e de pesquisa respondem sem login")
    void the_new_pages_are_public() throws Exception {
        page("/medium")
                .andExpect(content().string(containsString("Você é médium e quer contribuir")))
                .andExpect(content().string(containsString("id=\"o-que-nao-e\"")))
                .andExpect(content().string(containsString("id=\"o-que-fazer\"")))
                .andExpect(content().string(containsString("Não é um certificado de mediunidade")));
        page("/perguntas")
                .andExpect(content().string(containsString("Preciso ser espírita")))
                .andExpect(content().string(containsString("<details>")));
        page("/pesquisa").andExpect(content().string(containsString("Um protocolo que se defende sozinho")));
        page("/en/pesquisa")
                .andExpect(content().string(containsString("lang=\"en\"")))
                .andExpect(content().string(containsString("A protocol that defends itself")));
    }

    @Test
    @DisplayName("a pagina de pesquisa tira do catalogo o acaso e os limites do P1, nas duas linguas")
    void the_research_page_reads_the_catalog() throws Exception {
        Protocol p1 = ProtocolCatalog.structuredReading();
        ResultActions pt = page("/pesquisa")
                .andExpect(content().string(containsString(
                        p1.chanceNumerator() + " em " + p1.chanceDenominator())));
        ResultActions en = page("/en/pesquisa")
                .andExpect(content().string(containsString(
                        p1.chanceNumerator() + " in " + p1.chanceDenominator())));
        // SPEC 0.28, secao 1.5, regra 6: cada pagina mostra os limites no idioma dela, vindos do
        // catalogo. Ate a 0.27 a pagina em ingles citava os limites em portugues, por nao haver
        // onde guardar a traducao.
        for (Text limit : p1.limits()) {
            pt.andExpect(content().string(containsString(
                    HtmlUtils.htmlEscape(limit.pt(), StandardCharsets.UTF_8.name()))));
            en.andExpect(content().string(containsString(
                    HtmlUtils.htmlEscape(limit.en(), StandardCharsets.UTF_8.name()))));
        }
    }

    @Test
    @DisplayName("o menu agrupado marca a pagina atual e o submenu que a contem")
    void the_grouped_menu_marks_the_page_and_its_submenu() throws Exception {
        page("/medium")
                .andExpect(content().string(containsString("class=\"submenu participar atual\"")))
                .andExpect(content().string(containsString("href=\"/medium\" aria-current=\"page\"")))
                .andExpect(content().string(not(containsString("class=\"submenu pesquisa atual\""))));
        page("/pesquisa")
                .andExpect(content().string(containsString("class=\"submenu pesquisa atual\"")))
                .andExpect(content().string(containsString("href=\"/pesquisa\" aria-current=\"page\"")));
        page("/perguntas")
                .andExpect(content().string(containsString("href=\"/perguntas\" aria-current=\"page\"")));
    }

    @Test
    @DisplayName("toda pagina tem Contato no menu, levando ao e-mail do projeto no rodape (SPEC 0.27)")
    void every_page_has_the_contact_in_the_menu_and_the_footer() throws Exception {
        for (String path : new String[] {"/", "/medium", "/pesquisa", "/garantias", "/login", "/protocolo/P1"}) {
            page(path)
                    .andExpect(content().string(containsString("<a class=\"contato\" href=\"#contato\">Contato</a>")))
                    .andExpect(content().string(containsString("id=\"contato\"")))
                    .andExpect(content().string(containsString(
                            "href=\"mailto:contato@psigama.com\">contato@psigama.com</a>")));
        }
    }

    @Test
    @DisplayName("garantias em camadas: frase simples e detalhe tecnico")
    void the_guarantees_come_in_layers() throws Exception {
        page("/garantias")
                .andExpect(content().string(containsString("Ninguém muda as regras no meio do jogo")))
                .andExpect(content().string(containsString("class=\"detalhe-tecnico\"")))
                .andExpect(content().string(containsString("SHA-256 em modo contador")));
        page("/protocolo/P1").andExpect(content().string(containsString("id=\"em-poucas-palavras\"")));
        page("/protocolo/P3").andExpect(content().string(not(containsString("id=\"em-poucas-palavras\""))));
    }

    @Test
    @DisplayName("o termo da criacao de conta e a conta-v4, com o resumo antes do texto completo")
    void the_consent_term_has_its_summary() throws Exception {
        assertThat(AccountService.CURRENT_TERM_VERSION).isEqualTo("conta-v4");
        String html = page("/conta/criar").andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(html).contains("conta-v4").contains("Em seis pontos").contains("Texto completo");
        assertThat(html.indexOf("Em seis pontos")).isLessThan(html.indexOf("Texto completo"));
    }

    private ResultActions page(String path) throws Exception {
        return mvc.perform(get(path)).andExpect(status().isOk());
    }
}
