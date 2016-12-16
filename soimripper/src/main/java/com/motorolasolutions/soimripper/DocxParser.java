package com.motorolasolutions.soimripper;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DocxParser {

    public static List<Element> parse(Path path) throws IOException {
        List<Element> result = new ArrayList<>();
        try (InputStream inputStream = Files.newInputStream(path)) {
            XWPFDocument doc = new XWPFDocument(inputStream);
            List<XWPFTable> tables = doc.getTables();
            for (XWPFTable table : tables) {
                Element element = toElement(table);
                result.add(element);
            }
        }
        return result;
    }

    private static Element toElement(XWPFTable xTable) {
        XWPFTableCell[][] cells = getTableCells(xTable);
        if ((cells.length == 1) && (cells[0].length == 1)) {
            return toElement(cells[0][0]);
        }
        List<List<Element>> tableData = new ArrayList<>();
        for (XWPFTableCell[] row : cells) {
            tableData.add(
                    Arrays.stream(row)
                            .map(DocxParser::toElement)
                            .collect(Collectors.toList()));
        }
        return new Table(tableData).getFormatted();
    }

    private static Element toElement(XWPFTableCell cell) {
        List<Element> elements = cell.getBodyElements().stream()
                .map(DocxParser::toElement)
                .filter(e -> !e.isEmpty())
                .collect(Collectors.toList());
        List<Element> flatElements = new CompositeElement(elements).flatElement();
        return new CompositeElement(flatElements).joinParagraphs();
    }

    private static Element toElement(IBodyElement element) {
        if (element instanceof XWPFParagraph) {
            return toElement(((XWPFParagraph)element));
        }
        if (element instanceof XWPFTable) {
            return toElement((XWPFTable)element);
        }
        throw new IllegalArgumentException();
    }

    private static Element toElement(XWPFParagraph paragraph) {
        if (paragraph.getRuns().size() != paragraph.getIRuns().size()) {
            throw new UnsupportedOperationException();
        }
        String text = paragraph.getRuns().stream()
                .filter(p -> !p.getCTR().isSetRsidDel())
                .map(Object::toString)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining());
        return new Paragraph(text);
    }

    private static XWPFTableCell[][] getTableCells(XWPFTable table) {
        List<XWPFTableCell[]> result = new ArrayList<>();

        List<XWPFTableRow> rows = table.getRows();
        for (XWPFTableRow row : rows) {
            List<XWPFTableCell> cells = row.getTableCells();
            result.add(cells.toArray(new XWPFTableCell[0]));
        }
        return result.toArray(new XWPFTableCell[0][]);
    }

}
