package model

class XClass extends XElement {

    List<XConstraint> constraints = []
    List<XAttribute> attributes = []

    XClass(Object node, XElement parent) {
        super(node, parent)
    }

    @Override
    List<XElement> children() {
        return [constraints, attributes].flatten()
    }

    String type() {
        if (stereotypes.find {it.name == "MMM_Profile:MMTable"}) "Table"
        if (stereotypes.find {it.name == "MMM_Profile:MMScalar"}) "Scalar"
    }

}
