package com.motorolasolutions.soimripper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CompositeElement implements Element {

    private final List<Element> elements;

    public CompositeElement(List<Element> elements) {
        this.elements = elements;
    }

    @Override
    public boolean isEmpty() {
        return elements.stream().allMatch(Element::isEmpty);
    }

    @Override
    public List<Element> flatElement() {
        List<Element> result = new ArrayList<>();
        elements.forEach(e -> result.addAll(e.flatElement()));
        return result;
    }

    public Element joinParagraphs() {
        List<Element> flatElements = flatElement();
        if (flatElements.stream().allMatch(e -> (e instanceof Paragraph))) {
            String text = flatElements.stream().map(Element::getText).collect(Collectors.joining(LS));
            return new Paragraph(text);
        }
        return this;
    }

    @Override
    public String getText() {
        return elements.stream().map(Element::getText).collect(Collectors.joining(LS));
    }

    @Override
    public String toHtml() {
        return elements.stream().map(Element::toHtml).collect(Collectors.joining(LS));
    }
}
