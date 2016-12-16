package com.motorolasolutions.soimripper;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Paragraph implements Element {

    private final String text;

    private boolean isHeader = false;

    public Paragraph(String text) {
        this.text = text;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String toHtml() {
        String body = text.replace('\u00A0', ' ').trim();
        body = StringEscapeUtils.escapeHtml4(body);
        List<String> lines = splitLines(body);
        body = lines.stream().collect(Collectors.joining("<br>"));
        if (isHeader) {
            int level = getHeaderLevel(body);
            return "<h" + level + ">" + body + "</h" + level + ">";
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

    private static int getHeaderLevel(String text) {
        int idx = 0;
        text = text.trim();
        while ((idx < text.length()) &&
                (Character.isDigit(text.charAt(idx)) || (text.charAt(idx) == '.'))) {
            idx ++;
        }
        if (idx > 0) {
            String number = text.substring(0, idx);
            return number.split("\\.").length;
        }
        return 0;
    }
}
