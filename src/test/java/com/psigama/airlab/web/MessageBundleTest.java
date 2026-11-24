package com.psigama.airlab.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.psigama.airlab.support.Language;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * O texto compartilhado do cabeçalho e do rodapé, nos dois idiomas (SPEC 0.28, seção 1.5, regra 6).
 *
 * <p>Uma chave que existe só no português aparece em português numa página em inglês, que é o pior
 * dos dois mundos; uma que existe só no inglês nunca é usada. O teste existe para que acrescentar
 * texto sem a tradução não passe.
 */
class MessageBundleTest {

    /**
     * Chaves cujo valor é igual nos dois arquivos de propósito: nomes próprios, o rótulo de um
     * idioma escrito na própria língua, um identificador público e o aviso que é em inglês nos dois
     * lados, porque é dirigido a quem está lendo a página em inglês.
     */
    private static final Set<String> SAME_ON_PURPOSE = Set.of(
            "idioma.pt", "idioma.en", "idioma.traducao-pendente",
            "rodape.licenca.psigama", "rodape.pesquisa.p1", "navegacao.menu");

    @Test
    @DisplayName("os dois arquivos de mensagens tem exatamente as mesmas chaves, e nenhuma vazia")
    void both_bundles_have_the_same_keys() throws IOException {
        Properties portuguese = load("messages.properties");
        Properties english = load("messages_" + Language.EN.code() + ".properties");

        assertThat(new TreeSet<>(english.stringPropertyNames()))
                .describedAs("chaves do pacote em inglês")
                .isEqualTo(new TreeSet<>(portuguese.stringPropertyNames()));

        for (String key : portuguese.stringPropertyNames()) {
            assertThat(portuguese.getProperty(key)).describedAs("%s em português", key).isNotBlank();
            assertThat(english.getProperty(key)).describedAs("%s em inglês", key).isNotBlank();
        }
    }

    @Test
    @DisplayName("a traducao nao e o portugues repetido, salvo onde isso e proposital")
    void the_translation_is_really_a_translation() throws IOException {
        Properties portuguese = load("messages.properties");
        Properties english = load("messages_" + Language.EN.code() + ".properties");

        Set<String> repeated = new TreeSet<>();
        for (String key : portuguese.stringPropertyNames()) {
            if (!SAME_ON_PURPOSE.contains(key)
                    && portuguese.getProperty(key).equals(english.getProperty(key))) {
                repeated.add(key);
            }
        }
        assertThat(repeated).describedAs("chaves com o português repetido no lugar da tradução").isEmpty();
    }

    private static Properties load(String name) throws IOException {
        Properties properties = new Properties();
        try (InputStream in = MessageBundleTest.class.getClassLoader().getResourceAsStream(name)) {
            assertThat(in).describedAs(name).isNotNull();
            properties.load(new java.io.InputStreamReader(in, StandardCharsets.UTF_8));
        }
        return properties;
    }
}
