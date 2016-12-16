package com.motorolasolutions.soimripper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("c:\\Users\\PWH473\\Desktop\\soim\\R5.4-SOIM-D5.4.01.docx");
        List<Element> elements = DocxParser.parse(path);

        String text = toString(elements);
        String html = HtmlGenerator.toHtml(elements);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File("C:\\temp\\main.html")))) {
            writer.write(html);
        }

        System.out.println(text);
    }

    private static String toString(List<Element> elements) {
        StringBuilder sb = new StringBuilder();
        for (Element element : elements) {
            if (!element.isEmpty()) {
                sb.append(element.getText());
                sb.append(Element.LS);
            }
        }
        return sb.toString();
    }


}

