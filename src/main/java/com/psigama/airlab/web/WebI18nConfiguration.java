package com.psigama.airlab.web;

import com.psigama.airlab.support.Language;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.cache.AbstractCacheManager;
import org.thymeleaf.cache.ExpressionCacheKey;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheEntryValidityChecker;
import org.thymeleaf.cache.StandardCacheManager;
import org.thymeleaf.cache.TemplateCacheKey;
import org.thymeleaf.engine.TemplateModel;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;

/**
 * The two languages of the interface, wired into Spring (SPEC 0.28, section 1.5).
 *
 * <p>Four pieces, and no more than four. The <strong>locale</strong> of a request is the language
 * its address says it is, decided by {@link LanguageFilter} and never negotiated again further down.
 * The <strong>English templates are an overlay</strong>: {@code templates/en/<same path>} answers
 * when the request is English, and when that file does not exist the Portuguese template answers —
 * which is rule 5, and what lets the translation be published page by page without a broken link or
 * a blank page. The <strong>template cache is one per language</strong>, because the overlay gives
 * one template name two templates. The <strong>interceptor</strong> tells the page which of the two
 * happened, so one that fell back can say so in a line instead of passing for translated.
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

    /**
     * Gives the template engine a cache that keeps the two languages apart. Thymeleaf caches a
     * parsed template under its name, and with the overlay one name means two templates: with the
     * standard cache, whichever language asked for a page first after a start answered at both
     * addresses from then on. Only production runs with the cache on, so only production showed
     * it; {@code TemplateCacheLanguageTest} runs with the cache on for that reason.
     *
     * <p>Spring Boot builds the engine and offers no setting for its cache manager, and the manager
     * can only be replaced before the engine is first used: hence a post-processor, which is
     * static so that it exists before the beans it is meant to see.
     */
    @Bean
    static BeanPostProcessor templateCachePerLanguage() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) {
                if (bean instanceof SpringTemplateEngine engine) {
                    engine.setCacheManager(new CachePerLanguage());
                }
                return bean;
            }
        };
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
            // The same question the cache asks, answered by the same method: the two must never
            // disagree about the language of a request.
            if (LanguagePages.current() != Language.EN) {
                return null;
            }
            return english.resolveTemplate(configuration, ownerTemplate, template, attributes);
        }
    }

    /**
     * The standard caches of Thymeleaf, with the template cache split by the language of the
     * request. Parsed expressions are the same text in either language, so one cache serves both.
     */
    private static final class CachePerLanguage extends AbstractCacheManager {

        private final Map<Language, StandardCacheManager> standard = new EnumMap<>(Language.class);

        private CachePerLanguage() {
            for (Language language : Language.values()) {
                standard.put(language, new StandardCacheManager());
            }
        }

        @Override
        protected ICache<TemplateCacheKey, TemplateModel> initializeTemplateCache() {
            Map<Language, ICache<TemplateCacheKey, TemplateModel>> templates = new EnumMap<>(Language.class);
            standard.forEach((language, manager) -> templates.put(language, manager.getTemplateCache()));
            return new ByLanguage<>(templates);
        }

        @Override
        protected ICache<ExpressionCacheKey, Object> initializeExpressionCache() {
            return standard.get(Language.PT).getExpressionCache();
        }
    }

    /**
     * One cache per language behind the interface of one. Reading and writing go to the cache of
     * the language of the request being handled, which is the language {@link EnglishOverlay}
     * resolved the template in; clearing reaches every language.
     */
    private static final class ByLanguage<K, V> implements ICache<K, V> {

        private final Map<Language, ICache<K, V>> caches;

        private ByLanguage(Map<Language, ICache<K, V>> caches) {
            this.caches = caches;
        }

        @Override
        public void put(K key, V value) {
            current().put(key, value);
        }

        @Override
        public V get(K key) {
            return current().get(key);
        }

        @Override
        public V get(K key, ICacheEntryValidityChecker<? super K, ? super V> validityChecker) {
            return current().get(key, validityChecker);
        }

        @Override
        public void clear() {
            caches.values().forEach(ICache::clear);
        }

        @Override
        public void clearKey(K key) {
            caches.values().forEach(cache -> cache.clearKey(key));
        }

        @Override
        public Set<K> keySet() {
            Set<K> keys = new HashSet<>();
            caches.values().forEach(cache -> keys.addAll(cache.keySet()));
            return keys;
        }

        private ICache<K, V> current() {
            return caches.get(LanguagePages.current());
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
