/*
 * Página 404 do site estático (SPEC 0.21, seção 1.3, regra 5).
 *
 * O GitHub Pages não faz redirecionamento: devolve o 404.html para qualquer endereço que não
 * existe no site. Este script leva a pessoa à aplicação, com o mesmo caminho, a mesma consulta e o
 * mesmo fragmento, e assim os endereços antigos de crivo.psigama.com — /protocolo/..., /testes/...
 * e os links de e-mail — continuam funcionando. O endereço da aplicação vem do atributo
 * data-aplicacao, que o gerador escreve; o destino nunca sai da própria página.
 */
(function () {
    'use strict';

    var aplicacao = document.documentElement.getAttribute('data-aplicacao');
    if (!aplicacao) {
        return;
    }
    // Uma página do próprio site nunca vai para a aplicação: lá, com AIRLAB_SITE_URL, ela responde
    // com 301 de volta para cá, e as duas pontas ficariam se mandando a pessoa uma para a outra.
    var paginas = (document.documentElement.getAttribute('data-paginas-do-site') || '').split(' ');
    if (paginas.indexOf(window.location.pathname) !== -1) {
        return;
    }
    var destino = aplicacao + window.location.pathname + window.location.search + window.location.hash;
    var botao = document.getElementById('destino');
    if (botao) {
        botao.setAttribute('href', destino);
    }
    window.location.replace(destino);
})();
