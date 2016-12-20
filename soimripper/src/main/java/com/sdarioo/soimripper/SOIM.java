package com.sdarioo.soimripper;

import com.sdarioo.soimripper.model.Document;
import com.sdarioo.soimripper.model.Element;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Locale;

public class SOIM {
    private static final String TEMPLATE = "main";

    private final Document document;

    public SOIM(Document document) {
        this.document = document;
    }

    public String toHtml() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");

        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(templateResolver);

        Context context = new Context(Locale.getDefault());
        context.setVariable("doc", this);
        return engine.process(TEMPLATE, context);
    }

    public String createBody() {
        StringBuilder body = new StringBuilder();
        for (Element element : document.getElements()) {
            body.append(element.toHtml());
            body.append(Element.LS);
        }
        return body.toString();
    }

}
