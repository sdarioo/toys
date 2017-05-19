package parser

import model.XAttribute
import model.XClass
import model.XConstraint
import model.XDataType
import model.XElement
import model.XEnum
import model.XEnumLiteral
import model.XModel
import model.XPackage
import model.XStereotype

class RSMParser {

    XModel parse(String path) {

        def uml = new XmlSlurper()
                .parse(new File(path))
                .declareNamespace(xmi : "http://www.omg.org/XMI")
                .declareNamespace(xsi : "http://www.w3.org/2001/XMLSchema-instance")
                .declareNamespace(uml : "http://www.eclipse.org/uml2/2.1.0/UML")

        def model = new XModel(uml.Model)
        uml.Model.packagedElement.each {
            if (type(it) == "uml:Package") {
                model.packages.add(parsePackage(it, model))
            } else {
                println("Unexpected packagedElement at model level: ${type(it)}")
            }
        }

        //Parse stereotypes with namespace aware xml parser
        new XmlParser(true, false)
                .parse(new File(path))
                .children().each {

            if (it.name().contains(':')) {
                def baseElementAttr = it.attributes().find { attr -> attr.getKey().startsWith("base_")}
                if (baseElementAttr) {
                    def baseElement = model.findChild(baseElementAttr.getValue())
                    if (baseElement) {
                        Map properties = [:]
                        properties.putAll(it.attributes())
                        properties.remove(baseElementAttr.getKey())
                        properties.remove("xmi:id")
                        baseElement.stereotypes.add(new XStereotype(name: it.name(), properties: properties))
                    }
                }
            }
        }
        return model
    }

    private XPackage parsePackage(def node, XModel model) {
        def xPackage = new XPackage(node, model)
        node.packagedElement.each {
            if (type(it) == "uml:DataType") {
                xPackage.dataTypes.add(parseDataType(it, xPackage))
            } else if (type(it) == "uml:Enumeration") {
                xPackage.enums.add(parseEnum(it, xPackage))
            } else if (type(it) == "uml:Class") {
                xPackage.classes.add(parseClass(it, xPackage))
            } else {
                println("Unexpected packagedElement at package level: ${type(it)}")
            }
        }
        return xPackage
    }

    private XEnum parseEnum(def node, XPackage pkg) {
        def xEnum = new XEnum(node, pkg)
        node.ownedLiteral.each {
            xEnum.literals.add(new XEnumLiteral(it, pkg))
        }
        return xEnum;
    }

    private XDataType parseDataType(def node, XPackage pkg) {
        def xDataType = new XDataType(node, pkg);
        xDataType.constraint = new XConstraint(node.ownedRule, xDataType)
        return xDataType
    }

    private XClass parseClass(def node, XPackage pkg) {
        def xClass = new XClass(node, pkg)
        node.ownedRule.each {
            xClass.constraints.add(new XConstraint(it, xClass))
        }
        node.ownedAttribute.each {
            xClass.attributes.add(new XAttribute(it, xClass))
        }
        return xClass
    }

    String type(def node) {
        return XElement.type(node)
    }
}
