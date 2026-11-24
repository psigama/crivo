package com.psigama.airlab.web;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.RequestDispatcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * The error page a browser gets, in the site's own layout instead of the framework's
 * "Whitelabel Error Page". MockMvc does not forward a failed request to {@code /error} the way
 * the servlet container does, so the test calls {@code /error} with the attributes the
 * container sets.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ErrorPageTest {

    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("um endereco inexistente mostra a pagina do Crivo, nao a Whitelabel")
    void not_found_uses_the_site_page() throws Exception {
        mvc.perform(get("/error").accept(MediaType.TEXT_HTML)
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 404)
                        .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/protocolo/P9"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Página não encontrada")))
                .andExpect(content().string(containsString("/protocolos")))
                .andExpect(content().string(not(containsString("Whitelabel"))));
    }

    @Test
    @DisplayName("acesso negado e erro interno tambem usam a pagina do Crivo, sem a mensagem da excecao")
    void other_errors_use_the_site_page_without_the_exception_message() throws Exception {
        mvc.perform(get("/error").accept(MediaType.TEXT_HTML)
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 403))
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("Esta página não é do seu perfil")));

        mvc.perform(get("/error").accept(MediaType.TEXT_HTML)
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 500)
                        .requestAttr(RequestDispatcher.ERROR_EXCEPTION,
                                new IllegalStateException("detalhe-interno-que-nao-pode-vazar")))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Algo deu errado")))
                .andExpect(content().string(not(containsString("detalhe-interno-que-nao-pode-vazar"))))
                .andExpect(content().string(not(containsString("Whitelabel"))));
    }
}
