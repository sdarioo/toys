package com.motorolasolutions.soimripper;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Paragraph implements Element {

    private final String text;

    public Paragraph(String text) {
        //this.text = text;
        this.text = text.replace('\u00A0', ' ');
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String toHtml() {
        if (isEmpty()) {
            return text;
        }
        String body = StringEscapeUtils.escapeHtml4(text.trim());
        if (body.length() == 0) {
            body = "&nbsp;";
        }
        List<String> lines = splitLines(body);
        body = lines.stream().collect(Collectors.joining("<br>"));
        return "<p>" + body + "</p>";
    }

    @Override
    public List<Element> flatElement() {
        return Collections.singletonList(this);
    }

    @Override
    public boolean isEmpty() {
        return text.length() == 0;
    }

    private static List<String> splitLines(String text) {
        String[] lines = text.split("\\r?\\n");
        return Arrays.stream(lines)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
