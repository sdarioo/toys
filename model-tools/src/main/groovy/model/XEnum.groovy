package model

class XEnum extends XElement {

    List<XEnumLiteral> literals = []

    XEnum(Object node, XElement parent) {
        super(node, parent)
    }

    @Override
    List<XElement> children() {
        return literals
    }
}
