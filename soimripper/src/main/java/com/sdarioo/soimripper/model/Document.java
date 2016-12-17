package com.sdarioo.soimripper.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Document {

    private final List<Element> elements = new ArrayList<>();

    public Document(List<Element> elements) {
        this.elements.addAll(elements);
    }

    public List<Element> getElements() {
        return elements;
    }

    public void walk(Consumer<Element> visitor) {
        walk(elements, visitor);
    }

    private static void walk(List<Element> elements, Consumer<Element> visitor) {
        for (Element element : elements) {
            visitor.accept(element);
            if (element instanceof CompositeElement) {
                walk(((CompositeElement)element).getElements(), visitor);
            }
        }
    }
}
