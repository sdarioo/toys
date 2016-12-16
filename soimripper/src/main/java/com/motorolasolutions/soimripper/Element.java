package com.motorolasolutions.soimripper;

public interface Element {

    String LS = "\n";

    /**
     * @return text representation of this element
     */
    String getText();

    /**
     * @return html representation of this element
     */
    String toHtml();

    /**
     * @return whether element has no visual representation
     */
    boolean isEmpty();

    /**
     * @return whether this elements or one of its children represents table
     */
    boolean containsTables();
}
