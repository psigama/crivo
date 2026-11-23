/*
 * Idioma da primeira visita (SPEC 0.28, seção 1.5, regra 2).
 *
 * O site estático não tem servidor para negociar idioma, então quem faz isso aqui é este script:
 * quem chega a uma página em português, sem ter escolhido idioma, e com um navegador que não pede
 * português, é levado uma vez à versão em inglês. O destino não é calculado — é o endereço que a
 * própria página declara em <link rel="alternate" hreflang="en">, de modo que o script nunca invente
 * um caminho que não exista.
 *
 * Nada de localização por IP: o idioma do navegador é o que a própria pessoa configurou, não exige
 * serviço externo (Constitution P8) e não manda o endereço de ninguém para terceiros (P6).
 *
 * Na aplicação, o LanguageFilter já decidiu antes de a página existir e já deixou o cookie; aqui o
 * script encontra o cookie e não faz nada. É o mesmo arquivo nos dois lados.
 */
(function () {
    'use strict';

    var ESCOLHA = 'idioma';
    var ANO = 365 * 24 * 60 * 60;

    function cookie() {
        var achado = document.cookie.match(/(?:^|;\s*)idioma=(pt|en)(?:;|$)/);
        return achado ? achado[1] : null;
    }

    function lembrar(idioma) {
        var atributos = '; path=/; max-age=' + ANO + '; samesite=lax';
        if (window.location.protocol === 'https:') {
            atributos += '; secure';
        }
        document.cookie = ESCOLHA + '=' + idioma + atributos;
    }

    function daPagina() {
        var lang = (document.documentElement.getAttribute('lang') || '').toLowerCase();
        return lang.indexOf('en') === 0 ? 'en' : 'pt';
    }

    /* O que o navegador pede, entre os dois idiomas que existem; null se não pede nenhum deles. */
    function doNavegador() {
        var pedidos = navigator.languages && navigator.languages.length
            ? navigator.languages
            : [navigator.language || ''];
        for (var i = 0; i < pedidos.length; i++) {
            var subtag = String(pedidos[i]).toLowerCase().split('-')[0];
            if (subtag === 'pt') {
                return 'pt';
            }
            if (subtag === 'en') {
                return 'en';
            }
        }
        return null;
    }

    function endereco(idioma) {
        var link = document.querySelector('link[rel="alternate"][hreflang="' + (idioma === 'en' ? 'en' : 'pt-BR') + '"]');
        return link ? link.getAttribute('href') : null;
    }

    /* O seletor do cabeçalho: ?idioma=pt ou ?idioma=en fixa a escolha, e o endereço fica limpo. */
    var escolhido = (window.location.search.match(/[?&]idioma=(pt|en)(?:&|$)/) || [])[1];
    if (escolhido) {
        lembrar(escolhido);
        if (window.history && window.history.replaceState) {
            var limpo = window.location.pathname
                + window.location.search.replace(/([?&])idioma=(pt|en)(&|$)/, '$1').replace(/[?&]$/, '')
                + window.location.hash;
            window.history.replaceState(null, '', limpo);
        }
        return;
    }

    if (cookie()) {
        return;
    }

    var pagina = daPagina();
    var querido = doNavegador();
    if (querido === 'en' && pagina === 'pt') {
        var destino = endereco('en');
        if (destino) {
            lembrar('en');
            window.location.replace(destino);
            return;
        }
    }
    /* Seja qual for o idioma em que esta página saiu, é o que este navegador mantém daqui em diante. */
    lembrar(pagina);
})();
