package model


class XModel extends XElement {

    List<XPackage> packages = []

    XModel(def node) {
        super(node, null)
    }

    @Override
    List<XElement> children() {
        return packages
    }

    @Override
    XModel model() {
        return this
    }
}
