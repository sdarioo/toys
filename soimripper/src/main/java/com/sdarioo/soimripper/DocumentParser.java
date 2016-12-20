package com.sdarioo.soimripper;

import com.sdarioo.soimripper.model.*;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class DocumentParser {

    private DocumentParser() {}

    public static Document parse(Path path) throws IOException {
        List<Element> elements = new ArrayList<>();
        try (InputStream inputStream = Files.newInputStream(path)) {
            XWPFDocument doc = new XWPFDocument(inputStream);
            for (XWPFTable xTable : doc.getTables()) {
                Element element = parseTable(xTable);
                elements.add(element);
            }
        }
        return new Document(elements);
    }

    private static Element parseTable(XWPFTable xTable) {

        Table table = new Table();

        XWPFTableCell[][] cells = getTableCells(xTable);
        for (XWPFTableCell[] row : cells) {
            List<Element> rowData = new ArrayList<>();
            for (XWPFTableCell cell : row) {
                rowData.add(parseCell(cell));
            }
            table.addRow(rowData);
        }
        if (table.getColumnCount() == 1) {
            return (table.getRowsCount() == 1) ? table.getCell(0, 0) : new CompositeElement(table.getColumn(0));
        }
        if (table.isBulletList()) {
            return new CompositeElement(table.toList(" "));
        }
        return table;
    }

    private static Element parseCell(XWPFTableCell cell) {
        CompositeElement result = new CompositeElement();
        for (IBodyElement xElement : cell.getBodyElements()) {
            Element element = parseBody(xElement);
            if (!element.isEmpty()) {
                result.add(element);
            }
        }
        if (result.isEmpty()) {
            return new Paragraph("");
        }
        if (result.getElements().size() == 1) {
            return result.getElements().get(0);
        }
        return result;
    }

    private static Element parseBody(IBodyElement element) {
        if (element instanceof XWPFParagraph) {
            return parseParagraph(((XWPFParagraph)element));
        }
        if (element instanceof XWPFTable) {
            return parseTable((XWPFTable)element);
        }
        throw new IllegalArgumentException();
    }

    private static Element parseParagraph(XWPFParagraph paragraph) {
        StringBuilder text = new StringBuilder();
        for (XWPFRun run : paragraph.getRuns()) {
            if (run.getCTR().isSetRsidDel()) {
                continue;
            }
            text.append(run.toString().trim());
        }
        return new Paragraph(text.toString());
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
