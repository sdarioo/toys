package com.sdarioo.soimripper;

import com.sdarioo.soimripper.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class SoimDocument extends Document {

    private final String body;

    public SoimDocument(Document doc) {
        super(format(doc));
        body = createBody();
    }

    public String getBody() {
        return body;
    }

    private String createBody() {
        StringBuilder body = new StringBuilder();
        for (Element element : getElements()) {
            body.append(element.toHtml());
            body.append(Element.LS);
        }
        return body.toString();
    }

    /**
     * Formats raw document so it will reflect correct soim document
     * @param document parsed raw document
     * @return formatted soim document
     */
    private static List<Element> format(Document document) {
        List<Element> result = new ArrayList<>();

        Table revisionTable = new Table();
        AtomicBoolean isRevisionTable = new AtomicBoolean();

        document.walk(element -> {
            if (element.isEmpty()) {
                return;
            }
            if (element instanceof CompositeElement) {
                return;
            }
            String text = element.getText().trim();
            if (text.startsWith("Figure")) {
                return;
            }
            if (element instanceof Paragraph) {
                element = convertToHeaderIfNeeded((Paragraph)element);
            }
            if (text.contains("Revision History")) {
                isRevisionTable.set(true);
                return;
            }
            if (text.contains("----------")) {
                if (revisionTable.getRowsCount() > 1) {
                    isRevisionTable.set(false);
                    element = revisionTable;
                } else {
                    return;
                }
            }
            if (isRevisionTable.get()) {
                List<Element> row = Arrays.stream(text.split("(\\s){4,}"))
                        .filter(s -> !s.trim().isEmpty())
                        .map(Paragraph::new)
                        .collect(Collectors.toList());
                if (row.size() == 4) {
                    revisionTable.addRow(row);
                }
                return;
            }
            result.add(element);
        });
        return result;
    }

    private static Paragraph convertToHeaderIfNeeded(Paragraph element) {
        String text = element.getText().trim();
        if (text.startsWith("Section ")) {
            text = text.substring("Section ".length());
        }
        String headerNumber = getHeaderNumber(text);
        if (headerNumber != null) {
            text = text.substring(headerNumber.length()).trim();
            Paragraph header = new Paragraph(text);
            header.setHeader(headerNumber.split("\\.").length);
            return header;
        }
        return element;
    }

    private static String getHeaderNumber(String text) {
        int idx = 0;
        text = text.trim();
        while ((idx < text.length()) &&
                (Character.isDigit(text.charAt(idx)) || (text.charAt(idx) == '.'))) {
            idx ++;
        }
        if (idx > 0) {
            return text.substring(0, idx);
        }
        return null;
    }
}
