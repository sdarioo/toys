package generator

import model.XAttribute
import model.XModel

class PlantUmlGenerator implements Generator {

    final String name = "plantuml"
    boolean withExtraProperties

    String getName() {
        return withExtraProperties ? "${name}_with_props" : name;
    }

    @Override
    String generate(XModel model) {

        def builder = []
        builder << "@startuml ${model.name}"
        builder << "!pragma graphviz_dot jdot"
        builder << ""

        model.packages.each { xPackage ->

            builder << "package ${xPackage.name} {"

                xPackage.enums.each { xEnum ->
                    builder << "    enum ${xEnum.name} {"
                    xEnum.literals.each { xLiteral ->
                        builder << "        ${xLiteral.name}"
                    }
                    builder << "    }"
                }

                xPackage.classes.each {xClass ->
                    builder << "    class ${xClass.name} <<${xClass.stereotypeNames()}>> {"
                    xClass.attributes.each { xAttr ->
                        builder << "        ${attr(xAttr)}"
                    }
                    builder << "    }"
                }
            builder << "}"
        }
        builder << "@enduml"
        return builder.join("\n")
    }

    private String attr(XAttribute xAttr) {
        def props = [:]
        if (withExtraProperties) {
            if (xAttr.isKey()) {
                props << [isKey: true]
            }
            if (xAttr.defaultValue) {
                props << [defaultValue: xAttr.defaultValue]
            }
            if (xAttr.constraint()) {
                props << [constraint: xAttr.constraint().body]
            }
            props << xAttr.stereotypeProps()
        }

        return (xAttr.visibility == "public" ? "+" : "-") +
               (xAttr.isStatic ? " {static} " : "") +
                "${xAttr.name} : ${xAttr.type()} ${props?:""}"

    }

    @Override
    void generateDiagram(String text, File path) {
        if (!withExtraProperties) {
            path.withOutputStream {
                def s = new net.sourceforge.plantuml.SourceStringReader(text)
                s.generateImage(it);
            }
        }
    }

}
