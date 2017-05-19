package model

class XConstraint extends XElement {
    // Specification type e.g uml:OpaqueExpression
    String type
    // Constraint body e.g (1..10)
    String body
    // Constrained element id
    String constrainedElement

    XConstraint(Object node, XElement parent) {
        super(node, parent)
        this.type = type(node.specification)
        this.body = node.specification.body
        this.constrainedElement = node.@constrainedElement
    }

    @Override
    List<XElement> children() {
        return null
    }
}
