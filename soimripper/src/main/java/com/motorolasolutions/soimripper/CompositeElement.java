package com.motorolasolutions.soimripper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CompositeElement implements Element {

    private final List<Element> elements = new ArrayList<>();

    public CompositeElement() {
    }

    public CompositeElement(List<Element> elements) {
        this.elements.addAll(elements);
    }

    public void add(Element element) {
        elements.add(element);
    }

    public List<Element> getElements() {
        return elements;
    }

    @Override
    public boolean isEmpty() {
        return elements.stream().allMatch(Element::isEmpty);
    }

    @Override
    public boolean containsTables() {
        return elements.stream().allMatch(Element::containsTables);
    }

    @Override
    public String getText() {
        return elements.stream()
                .map(Element::getText)
                .collect(Collectors.joining(LS));
    }

    @Override
    public String toHtml() {
        return elements.stream()
                .map(Element::toHtml)
                .collect(Collectors.joining(LS));
    }
}
