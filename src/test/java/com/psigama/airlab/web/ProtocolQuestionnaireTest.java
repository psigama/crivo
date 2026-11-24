package com.psigama.airlab.web;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.psigama.airlab.catalog.Questionnaire;
import com.psigama.airlab.catalog.Questionnaires;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.util.HtmlUtils;

/**
 * The page of the automated protocol publishes the questionnaire in force, field by field (SPEC
 * 0.17, catalog rule 7; E13, annex). It is public by design: frozen into every preregistration
 * and part of every commitment. The page shows the questionnaire, never an answer.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProtocolQuestionnaireTest {

    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("a pagina do P1, publica, mostra o questionario em vigor campo a campo: opcoes, nota e por que")
    void the_p1_page_shows_the_questionnaire_field_by_field() throws Exception {
        Questionnaire questionnaire = Questionnaires.current();
        ResultActions page = mvc.perform(get("/protocolo/P1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"questionario\"")))
                .andExpect(content().string(containsString(html(questionnaire.version()))));
        for (Questionnaire.Field field : questionnaire.fields()) {
            page.andExpect(content().string(containsString(html(field.label().pt()))))
                    .andExpect(content().string(containsString(html(field.key()))));
            for (Questionnaire.Option option : field.options()) {
                page.andExpect(content().string(containsString(html(option.label().pt()))));
            }
            if (!field.scored()) {
                page.andExpect(content().string(containsString(html(field.outOfScoreReason().pt()))));
            }
        }
        if (questionnaire.draft()) {
            page.andExpect(content().string(containsString("Esta versão é um rascunho")));
        }
    }

    @Test
    @DisplayName("as paginas dos outros protocolos nao trazem o questionario")
    void only_the_p1_page_has_it() throws Exception {
        for (String code : new String[] {"P2", "P3", "P4"}) {
            mvc.perform(get("/protocolo/" + code))
                    .andExpect(status().isOk())
                    .andExpect(content().string(org.hamcrest.Matchers.not(containsString("id=\"questionario\""))));
        }
    }

    @Test
    @DisplayName("o painel do administrador mostra a versao em vigor e leva ao questionario do P1")
    void the_admin_panel_links_to_it() throws Exception {
        mvc.perform(get("/painel/adm").with(user("pesquisador").roles("ADMIN", "RESEARCHER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(html(Questionnaires.current().version()))))
                .andExpect(content().string(containsString("href=\"/protocolo/P1#questionario\"")));
    }

    private static String html(String text) {
        return HtmlUtils.htmlEscape(text, "UTF-8");
    }
}
