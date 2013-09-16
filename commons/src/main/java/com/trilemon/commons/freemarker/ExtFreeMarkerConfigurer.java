package com.trilemon.commons.freemarker;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.List;

/**
 * @author kevin
 */
public class ExtFreeMarkerConfigurer extends FreeMarkerConfigurer {
    private static Logger logger = LoggerFactory.getLogger(ExtFreeMarkerConfigurer.class);
    @Override
    protected void postProcessTemplateLoaders(List<TemplateLoader> templateLoaders) {
        super.postProcessTemplateLoaders(templateLoaders);
        templateLoaders.add(new ClassTemplateLoader(ExtFreeMarkerConfigurer.class,
                "/"));
        logger.info("ClassTemplateLoader for Spring ext macros added to FreeMarker configuration");
    }
}
