package com.sdarioo.soimripper;

import com.sdarioo.soimripper.model.Document;
import com.sdarioo.soimripper.model.Element;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("d:\\devel\\projects\\com.github.sdarioo\\toys\\soimripper\\src\\main\\resources\\main.docx");
        Path htmlPath = Paths.get("d:\\temp\\main.html");

        Document document = DocumentParser.parse(path);

        String html = HtmlGenerator.toHtml(document);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(htmlPath.toFile()))) {
            writer.write(html);
        }

        System.out.println("Done.");
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

