package io.github.rogeriocisi.airlab.web;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * The application's side of the split between site and application (SPEC 0.21, section 1.3): it
 * serves the editorial pages itself while no site address is configured, and sends them to the
 * site with a permanent redirect once one is.
 */
class SiteRoutesTest {

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    @ActiveProfiles("test")
    @DisplayName("sem AIRLAB_SITE_URL, como no perfil local")
    class WithoutSiteAddress {

        @Autowired
        MockMvc mvc;

        @Test
        @DisplayName("a aplicacao serve cada pagina do site, e todas sao publicas")
        void serves_every_editorial_page() throws Exception {
            for (SitePages.Page page : SitePages.ALL) {
                mvc.perform(get(page.path()))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString("href=\"/participar\"")));
            }
        }

        @Test
        @DisplayName("a pagina inicial e curta: traz o esclarecimento do P1 e os caminhos, nao o estado da trilha")
        void the_home_page_is_short() throws Exception {
            mvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("id=\"o-que-nao-e\"")))
                    .andExpect(content().string(containsString("href=\"/garantias\"")))
                    .andExpect(content().string(not(containsString("oito etapas"))))
                    .andExpect(content().string(not(containsString("Eventos na trilha"))));
        }

        @Test
        @DisplayName("o estado da trilha fica em /verificar, lido do banco")
        void the_trail_state_is_at_verificar() throws Exception {
            mvc.perform(get("/verificar"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Eventos na trilha")))
                    .andExpect(content().string(containsString("Hash do último evento")));
        }

        @Test
        @DisplayName("as oito etapas do P1 ficam na pagina do protocolo")
        void the_eight_steps_are_on_the_protocol_page() throws Exception {
            mvc.perform(get("/protocolo/P1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("id=\"etapas\"")))
                    .andExpect(content().string(containsString("oito etapas")));
        }

        @Test
        @DisplayName("os links entre as paginas ficam relativos: um processo so serve tudo")
        void links_stay_relative() throws Exception {
            mvc.perform(get("/protocolos"))
                    .andExpect(content().string(containsString("href=\"/historia\"")))
                    .andExpect(content().string(containsString("href=\"/login\"")))
                    .andExpect(content().string(not(containsString("https://crivo.psigama.com/historia"))));
        }
    }

    @Nested
    @SpringBootTest(properties = "airlab.site-url=https://site.exemplo.test/")
    @AutoConfigureMockMvc
    @ActiveProfiles("test")
    @DisplayName("com AIRLAB_SITE_URL, como em producao depois da virada")
    class WithSiteAddress {

        @Autowired
        MockMvc mvc;

        @Test
        @DisplayName("cada pagina do site responde 301 para o mesmo caminho no site")
        void every_editorial_page_redirects_to_the_site() throws Exception {
            for (SitePages.Page page : SitePages.ALL) {
                mvc.perform(get(page.path()))
                        .andExpect(status().isMovedPermanently())
                        .andExpect(header().string("Location", "https://site.exemplo.test" + page.path()));
            }
            mvc.perform(get("/historia?de=psigama"))
                    .andExpect(status().isMovedPermanently())
                    .andExpect(header().string("Location", "https://site.exemplo.test/historia?de=psigama"));
        }

        @Test
        @DisplayName("as paginas da aplicacao apontam para o site pelo endereco dele, e para si mesmas sem ele")
        void application_pages_link_to_the_site_by_its_address() throws Exception {
            mvc.perform(get("/protocolos"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("href=\"https://site.exemplo.test/\"")))
                    .andExpect(content().string(containsString("href=\"https://site.exemplo.test/historia\"")))
                    .andExpect(content().string(containsString("href=\"/protocolos\" aria-current=\"page\"")))
                    .andExpect(content().string(containsString("href=\"/login\"")));
        }

        @Test
        @DisplayName("o que vem do codigo dos protocolos ou do banco continua na aplicacao")
        void protocol_and_database_pages_stay() throws Exception {
            mvc.perform(get("/protocolo/P1")).andExpect(status().isOk());
            mvc.perform(get("/consulente")).andExpect(status().isOk())
                    .andExpect(content().string(containsString("href=\"https://site.exemplo.test/sobre\"")));
            mvc.perform(get("/verificar")).andExpect(status().isOk());
        }
    }
}
