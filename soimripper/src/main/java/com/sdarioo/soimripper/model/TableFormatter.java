package com.sdarioo.soimripper.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TableFormatter {

    public static String toText(Table table) {
        table = makeAllColumnsSameWidth(table);
        StringBuilder sb = new StringBuilder();
        for (List<Element> row : table.data()) {
            sb.append(row.stream()
                    .map(Element::getText)
                    .collect(Collectors.joining(" | ")));
            sb.append(Element.LS);
        }
        return sb.toString();
    }


    private static Table makeAllColumnsSameWidth(Table table) {
        if ((table.getColumnCount() <= 1) || !isTextTable(table)) {
            return table;
        }
        int[] widths = new int[table.getColumnCount()];

        for (List<Element> row : table.data()) {
            for (int i = 0; i < row.size(); i++) {
                widths[i] = Math.max(widths[i], row.get(i).getText().length());
            }
        }
        List<List<Element>> newData = new ArrayList<>();
        for (List<Element> row : table.data()) {
            List<Element> newRow = new ArrayList<>();
            for (int i = 0; i < row.size(); i++) {
                String text = padRight(row.get(i).getText(), widths[i]);
                newRow.add(new Paragraph(text));
            }
            newData.add(newRow);
        }
        return new Table(newData);
    }

    private static boolean isTextTable(Table table) {
        for (List<Element> row : table.data()) {
            for (Element cell : row) {
                if (cell.containsTables()) {
                    return false;
                }
            }
        }
        return true;
    }

    private static String padRight(String text, int n) {
        StringBuilder sb = new StringBuilder(text);
        while (sb.length() < n) {
            sb.append(" ");
        }
        return sb.toString();
    }
}
