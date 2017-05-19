package model

class XEnumLiteral extends XElement {
    // Literal type e.g uml:LiteralInteger
    String type
    // Literal value e.g '1'
    String value

    XEnumLiteral(Object node, XElement parent) {
        super(node, parent)
        this.value = node.specification.@value
        this.type = type(node.specification)
    }

    @Override
    List<XElement> children() {
        return null
    }
}
