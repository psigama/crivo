package io.github.rogeriocisi.airlab.web;

import io.github.rogeriocisi.airlab.support.Language;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Map;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;

/**
 * The two languages of the interface, wired into Spring (SPEC 0.28, section 1.5).
 *
 * <p>Three pieces, and no more than three. The <strong>locale</strong> of a request is the language
 * its address says it is, decided by {@link LanguageFilter} and never negotiated again further down.
 * The <strong>English templates are an overlay</strong>: {@code templates/en/<same path>} answers
 * when the request is English, and when that file does not exist the Portuguese template answers —
 * which is rule 5, and what lets the translation be published page by page without a broken link or
 * a blank page. The <strong>interceptor</strong> tells the page which of the two happened, so one
 * that fell back can say so in a line instead of passing for translated.
 *
 * <p>The shared text — header, footer, buttons, validation messages — comes from the message
 * bundle: {@code messages.properties} is Portuguese, the language of origin and the default, and
 * {@code messages_en.properties} is the translation.
 */
@Configuration(proxyBeanMethods = false)
class WebI18nConfiguration implements WebMvcConfigurer {

    /**
     * The language of the request, as its address declared it. There is nothing to set: changing
     * language means going to the address of the other language (rule 1), so no hidden state can
     * disagree with what the URL says.
     */
    @Bean
    LocaleResolver localeResolver() {
        return new AddressLocaleResolver();
    }

    /**
     * The English overlay, ahead of the standard resolver and silent for everything else: it
     * answers only for an English request, and only when the template is really there.
     */
    @Bean
    ITemplateResolver englishTemplateResolver(ApplicationContext context, ThymeleafProperties properties) {
        SpringResourceTemplateResolver english = new SpringResourceTemplateResolver();
        english.setApplicationContext(context);
        english.setPrefix("classpath:/templates/" + Language.EN.code() + "/");
        english.setSuffix(properties.getSuffix());
        english.setTemplateMode(properties.getMode());
        english.setCharacterEncoding(properties.getEncoding().name());
        english.setCacheable(properties.isCache());
        // Without this, a missing English template would be reported as an error instead of
        // letting the Portuguese one answer.
        english.setCheckExistence(true);
        return new EnglishOverlay(english);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TranslationNotice());
    }

    /** The locale of a request is the language of its address. */
    private static final class AddressLocaleResolver implements LocaleResolver {

        @Override
        public Locale resolveLocale(HttpServletRequest request) {
            return LanguagePages.of(request).locale();
        }

        @Override
        public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
            throw new UnsupportedOperationException(
                    "o idioma vem do endereço da página (SPEC 0.28, seção 1.5, regra 1)");
        }
    }

    /**
     * Resolves {@code templates/en/...} for an English request and steps aside for a Portuguese
     * one, so that the resolver chain falls through to the Portuguese template.
     */
    private static final class EnglishOverlay implements ITemplateResolver {

        private final ITemplateResolver english;

        private EnglishOverlay(ITemplateResolver english) {
            this.english = english;
        }

        @Override
        public String getName() {
            return "englishOverlay";
        }

        /** Before the standard resolver, which Spring Boot leaves unordered, and so last. */
        @Override
        public Integer getOrder() {
            return 1;
        }

        @Override
        public TemplateResolution resolveTemplate(IEngineConfiguration configuration, String ownerTemplate,
                                                  String template, Map<String, Object> attributes) {
            Locale locale = LocaleContextHolder.getLocale();
            if (!Language.EN.locale().getLanguage().equals(locale.getLanguage())) {
                return null;
            }
            return english.resolveTemplate(configuration, ownerTemplate, template, attributes);
        }
    }

    /**
     * Marks the page that is answering in Portuguese at an English address, so the shared header
     * can say it in one line (rule 5). A page that has its own English template says nothing.
     */
    private static final class TranslationNotice implements HandlerInterceptor {

        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                               ModelAndView modelAndView) {
            if (modelAndView == null || LanguagePages.of(request) != Language.EN) {
                return;
            }
            String view = modelAndView.getViewName();
            if (view == null || view.startsWith("redirect:") || view.startsWith("forward:")) {
                return;
            }
            modelAndView.addObject("traducaoPendente", !LanguagePages.translated(view));
        }
    }
}
