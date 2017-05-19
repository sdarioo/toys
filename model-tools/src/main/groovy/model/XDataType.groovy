package model

class XDataType extends XElement {

    XConstraint constraint

    XDataType(Object node, XElement parent) {
        super(node, parent)
    }

    @Override
    List<XElement> children() {
        return [constraint]
    }

    String type() {
        if (stereotypes.size() == 1) {
            return stereotypes[0].name
        }
        return "???"
    }
}
