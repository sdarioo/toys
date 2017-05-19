package model

class XAttribute extends XElement {
    // Basic type (if available) e.g Integer
    String basicType
    // Reference type (XEnum or XDataType) identifier
    String refTypeId
    // Attribute visibility e.g 'public'
    String visibility
    // True for static field, false otherwise
    boolean isStatic
    // Default value
    String defaultValue

    XAttribute(Object node, XElement parent) {
        super(node, parent)
        this.refTypeId = node.@type
        this.visibility = node.@visibility
        this.isStatic = node.@isStatic == "true"
        this.basicType = parseBasicType(node.type)
        this.defaultValue = parseDefaultValue(node.defaultValue)
    }

    @Override
    List<XElement> children() {
        return null
    }

    String type() {
        basicType ?: refType().name
    }

    boolean isKey() {
        stereotypes.find { it.name in ["MMM_Profile:MMConfigKey", "MMM_Profile:MMKey"]}
    }

    XConstraint constraint() {
        def constraint = parent.constraints.find({
            it.constrainedElement == id
        })
// Constraint from referenced data type is not returned here
//        if (!constraint && refTypeId) {
//            constraint = (refType() instanceof XDataType) ? refType().constraint : null
//        }
        return constraint
    }

    XElement refType() {
        def refType = model().findChild(refTypeId)
        if (refType instanceof XEnum || refType instanceof  XDataType) {
            return refType
        }
        return null
    }

    private String parseBasicType(def typeNode) {
        if (type(typeNode) == "uml:PrimitiveType") {
            def href = typeNode.@href.text()
            return href.substring(href.lastIndexOf('#') + 1);
        }

        if (type(typeNode)) {
            println("Unexpected attribute type: ${type(typeNode)}")
        }
    }

    private String parseDefaultValue(def defaultValueNode) {

        if (type(defaultValueNode).startsWith("uml:Literal")) {
            return defaultValueNode.@value
        }
        if (type(defaultValueNode) == "uml:OpaqueExpression") {
            return defaultValueNode.body
        }
        if (type(defaultValueNode) == "uml:InstanceValue") {
            return defaultValueNode.@name
        }

        if (type(defaultValueNode)) {
            println("Unexpected default value type: ${type(defaultValueNode)} for attr=${name}")
        }
    }
}
