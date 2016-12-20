package com.sdarioo.soimripper.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Table implements Element {

    private static final String BULLET = "•";

    private final List<List<Element>> data = new ArrayList<>();

    public Table() {
    }

    public Table(List<List<Element>> data) {
        this.data.addAll(data);
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public boolean containsTables() {
        return true;
    }

    public int getColumnCount() {
        return isEmpty() ? 0 : data.get(0).size();
    }

    public int getRowsCount() {
        return data.size();
    }

    public Element getCell(int row, int column) {
        return data.get(row).get(column);
    }

    public List<Element> getRow(int n) {
        return data.get(n);
    }

    public void addRow(List<Element> row) {
        data.add(row);
    }

    public List<List<Element>> data() {
        return data;
    }

    public List<Element> getColumn(int n) {
        return data.stream()
                .map(row -> row.get(n))
                .collect(Collectors.toList());
    }

    public boolean isBulletList() {
        return (getColumnCount() == 2) && getColumn(0).stream().allMatch(Table::isBullet);
    }

    public List<Element> toList(String separator) {
        List<Element> list = new ArrayList<>();
        for (List<Element> row : data) {
            String rowText = row.stream()
                    .map(Element::getText)
                    .collect(Collectors.joining(separator));
            list.add(new Paragraph(rowText));
        }
        return list;
    }

    @Override
    public String getText() {
        return toString();
    }

    @Override
    public String toString() {
        return TableFormatter.toText(this);
    }

    @Override
    public String toHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table class=\"demo\">");
        sb.append(LS);
        boolean header = true;
        for (List<Element> row : data) {
            sb.append("<tr>");
            sb.append(LS);
            for (Element cell : row) {
                sb.append(header ? "<th>" : "<td>");
                sb.append(cell.toHtml());
                sb.append(header ? "</th>" : "</td>");
                sb.append(LS);
            }
            header = false;
            sb.append("</tr>");
            sb.append(LS);
        }
        sb.append("</table>");
        return sb.toString();
    }

    private static boolean isBullet(Element element) {
        return (element instanceof Paragraph) && BULLET.equals(element.getText().trim());
    }


}
