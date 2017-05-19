package model

abstract class XElement {
    String id
    String name
    String comment;
    XElement parent;

    List<XStereotype> stereotypes = []

    XElement(def node, XElement parent) {
        this.parent = parent
        this.id = node.'@xmi:id'.text()
        this.name = node.@name
        this.comment = node.ownedComment.body
    }

    abstract List<XElement> children()

    XModel model() {
        def model = parent;
        while ((model != null) && !(model instanceof XModel)) {
            model = model.parent
        }
        return model
    }

    XElement findChild(String id) {
        for (def e in children()) {
            if (e.id == id) {
                return e
            }
            def child = e.findChild(id)
            if (child != null) {
                return child
            }
        }
        return null
    }

    Map<String, String> stereotypeProps() {
        def result = [:]
        stereotypes.each {
            result.putAll(it.properties)
        }
        return result
    }

    List<String> stereotypeNames() {
        stereotypes.collect { it.name }
    }

    static String type(def node) {
        return node.'@xmi:type'.text()
    }

}
