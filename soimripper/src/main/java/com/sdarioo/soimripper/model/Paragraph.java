package com.sdarioo.soimripper.model;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Paragraph implements Element {

    private final String text;

    private int headerLevel;

    public Paragraph(String text) {
        this.text = text.replace('\u00A0', ' ');
    }

    public void setHeader(int headerLevel) {
        this.headerLevel = headerLevel;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String toHtml() {
        String body = text.trim();
        body = StringEscapeUtils.escapeHtml4(body);
        List<String> lines = splitLines(body);
        body = lines.stream().collect(Collectors.joining("<br>"));
        if (headerLevel > 0) {
            return "<h" + headerLevel + ">" + body + "</h" + headerLevel + ">";
        }
        return "<p>" + body + "</p>";
    }

    @Override
    public boolean isEmpty() {
        return text.length() == 0;
    }

    @Override
    public boolean containsTables() {
        return false;
    }

    private static List<String> splitLines(String text) {
        String[] lines = text.split("\\r?\\n");
        return Arrays.stream(lines)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }


}
