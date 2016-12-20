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
        Path path = Paths.get("c:\\Temp\\main.docx");
        Path htmlPath = Paths.get("c:\\temp\\main.html");

        Document document = DocumentParser.parse(path);
        SOIM soim = SOIMFormatter.createSOIM(document);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(htmlPath.toFile()))) {
            writer.write(soim.toHtml());
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

