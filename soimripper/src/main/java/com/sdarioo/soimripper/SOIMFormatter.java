package com.sdarioo.soimripper;

import com.sdarioo.soimripper.model.CompositeElement;
import com.sdarioo.soimripper.model.Document;
import com.sdarioo.soimripper.model.Element;
import com.sdarioo.soimripper.model.Paragraph;
import com.sdarioo.soimripper.model.Table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class SOIMFormatter {

    public static SOIM createSOIM(Document document) {
        document = removeEmptyParagraphs(document);
        document = createHeaders(document);
        document = formatRevisionTable(document);
        return new SOIM(document);
    }

    private static Document removeEmptyParagraphs(Document document) {
        List<Element> result = new ArrayList<>();
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
            if (text.endsWith("Class Diagram")) {
                return;
            }
            if (text.endsWith("Associations")) {
                return;
            }
            if (text.endsWith("Classes")) {
                return;
            }
            result.add(element);
        });
        return new Document(result);
    }

    private static Document formatRevisionTable(Document document) {
        List<Element> result = new ArrayList<>();

        Table revisionTable = new Table();
        AtomicBoolean isTable = new AtomicBoolean();
        document.walk(element -> {
            String text = element.getText().trim();
            if (text.contains("Revision History")) {
                isTable.set(true);
                result.add(element);
                return;
            }
            if (text.contains("----------")) {
                if (revisionTable.getRowsCount() > 1) {
                    isTable.set(false);
                    result.add(revisionTable);
                }
                return;
            }
            if (isTable.get()) {
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
        return new Document(result);
    }

    private static Document createHeaders(Document document) {
        List<Element> result = new ArrayList<>();
        document.walk(element -> {
            if (element instanceof Paragraph) {
                element = asHeader((Paragraph)element);
            }
            result.add(element);
        });
        return new Document(result);
    }

    private static Paragraph asHeader(Paragraph element) {
        String text = element.getText().trim();
        if (text.startsWith("Section ")) {
            text = text.substring("Section ".length());
        }
        String headerNumber = getHeaderLevel(text);
        if (headerNumber != null) {
            text = text.substring(headerNumber.length()).trim();
            Paragraph header = new Paragraph(text);
            header.setHeader(headerNumber.split("\\.").length);
            return header;
        }
        return element;
    }

    private static String getHeaderLevel(String text) {
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
