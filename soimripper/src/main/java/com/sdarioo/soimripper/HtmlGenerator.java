package com.sdarioo.soimripper;

import com.sdarioo.soimripper.model.Document;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Locale;

public class HtmlGenerator {

    private static final String TEMPLATE = "main";

    public static String toHtml(Document document) {
        SoimDocument soim = new SoimDocument(document);
        return processTemplate(soim);
    }

    private static String processTemplate(SoimDocument doc) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");

        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(templateResolver);

        IContext context = new Context(Locale.getDefault());
        context.getVariables().put("doc", doc);

        return engine.process(TEMPLATE, context);
    }

}
