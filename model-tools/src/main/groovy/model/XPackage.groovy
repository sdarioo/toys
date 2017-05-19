package model

class XPackage extends XElement {

    List<XClass> classes = []
    List<XEnum> enums = []
    List<XDataType> dataTypes = []

    XPackage(Object node, XElement parent) {
        super(node, parent)
    }

    @Override
    List<XElement> children() {
        return [classes, enums, dataTypes].flatten()
    }

}

