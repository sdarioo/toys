package com.motorolasolutions.soimripper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

public class HtmlGenerator {

    private static final String TEMPLATE = "main.html";
    private static final String TEMPLATE_ENCODING = "UTF-8";

    private static final String BODY = "${body}";

    public static String toHtml(List<Element> elements) {
        StringBuilder body = new StringBuilder();
        for (Element element : elements) {
            if (!element.isEmpty()) {
                body.append(element.toHtml());
                body.append(Element.LS);
            }
        }
        String html = loadTemplate();
        return html.replace(BODY, body.toString());
    }

    private static String loadTemplate() {
        try (InputStream inputStream = HtmlGenerator.class.getClassLoader().getResourceAsStream(TEMPLATE)) {
            return read(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
    }

    private static String read(InputStream input) throws IOException {
        return new Scanner(input, TEMPLATE_ENCODING).useDelimiter("\\Z").next();
    }

}
