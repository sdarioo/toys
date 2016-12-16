package com.motorolasolutions.soimripper;

import java.util.List;

public interface Element {

    String LS = "\n";

    List<Element> flatElement();

    String getText();

    String toHtml();

    boolean isEmpty();
}
