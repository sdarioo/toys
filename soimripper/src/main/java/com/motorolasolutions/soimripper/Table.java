package com.motorolasolutions.soimripper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Table implements Element {

    private final List<List<Element>> data;

    public Table(List<List<Element>> data) {
        this.data = data;
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public boolean isSimple() {
        return data.stream().allMatch(row -> row.stream().allMatch(cell -> (cell instanceof Paragraph)));
    }

    public int getColumnCount() {
        return isEmpty() ? 0 : data.get(0).size();
    }

    @Override
    public String getText() {
        return toString();
    }

    @Override
    public List<Element> flatElement() {
        List<Element> firstColumn = getColumn(0);
        if (getColumnCount() == 1) {
            return new CompositeElement(firstColumn).flatElement();
        }
        if (getColumnCount() == 2) {
            // Special case - bullet list
            if (firstColumn.stream().allMatch(Table::isBullet)) {
                List<Element> secondColumn = getColumn(1);
                return secondColumn.stream()
                        .map(e -> new Paragraph("   - " + e.getText()))
                        .collect(Collectors.toList());
            }
        }
        return Collections.singletonList(this);
    }

    private List<Element> getColumn(int n) {
        return data.stream()
                .map(row -> row.get(n))
                .collect(Collectors.toList());
    }

    public Table getFormatted() {
        if ((getColumnCount() <= 1) || !isSimple()) {
            return this;
        }
        int[] widths = new int[getColumnCount()];
        for (List<Element> row : data) {
            for (int i = 0; i < row.size(); i++) {
                widths[i] = Math.max(widths[i], row.get(i).getText().length());
            }
        }
        List<List<Element>> newData = new ArrayList<>();
        for (List<Element> row : data) {
            List<Element> newRow = new ArrayList<>();
            for (int i = 0; i < row.size(); i++) {
                String text = padRight(row.get(i).getText(), widths[i]);
                newRow.add(new Paragraph(text));
            }
            newData.add(newRow);
        }
        return new Table(newData);
    }

    private static String padRight(String text, int n) {
        StringBuilder sb = new StringBuilder(text);
        while (sb.length() < n) {
            sb.append(" ");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (List<Element> row : data) {
            sb.append(row.stream()
                    .map(Element::getText)
                    .collect(Collectors.joining(" | ")));
            sb.append(LS);
        }
        return sb.toString();
    }

    @Override
    public String toHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table style=\"width: 100%;\"border=\"1px solid black;\">");
        sb.append(LS);
        for (List<Element> row : data) {
            sb.append("<tr>");
            sb.append(LS);
            for (Element cell : row) {
                sb.append("<td>");
                sb.append(cell.toHtml());
                sb.append("</td>");
                sb.append(LS);
            }
            sb.append("</tr>");
            sb.append(LS);
        }
        sb.append("</table>");
        return sb.toString();
    }

    private static boolean isBullet(Element element) {
        return (element instanceof Paragraph) && "•".equals(element.getText());
    }
}
