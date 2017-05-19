package generator

import groovy.xml.MarkupBuilder
import model.XModel

class XmlGenerator implements Generator {

    final String name = "xml"

    @Override
    String generate(XModel model) {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        xml.doubleQuotes = true
        xml.omitEmptyAttributes = true
        xml.omitNullAttributes = true

        xml.Model([name: model.name] + model.stereotypeProps()) {

            model.packages.each { xPackage ->
                Package([name: xPackage.name] + xPackage.stereotypeProps()) {

                    xPackage.dataTypes.each { xDataType ->
                        DataType([name: xDataType.name,
                                  type: xDataType.type(),
                                  constraint: xDataType.constraint.body] + xDataType.stereotypeProps())
                    }

                    xPackage.classes.each { xClass ->
                        Class([name: xClass.name, type: xClass.type()] + xClass.stereotypeProps()) {
                            xClass.attributes.each { xAttr ->
                                Attr([name: xAttr.name,
                                      type: xAttr.type(),
                                      isKey: xAttr.isKey() ?: null, // skip if false
                                      visibility: xAttr.visibility,
                                      isStatic: xAttr.isStatic,
                                      defauleValue: xAttr.defaultValue,
                                      constraint: xAttr.constraint()?.body] + xAttr.stereotypeProps())
                            }
                        }
                    }

                    xPackage.enums.each { xEnum ->
                        Enum([name: xEnum.name] + xEnum.stereotypeProps()) {
                            xEnum.literals.each { xLiteral ->
                                Literal([name: xLiteral.name, value: xLiteral.value] + xLiteral.stereotypeProps())
                            }
                        }
                    }
                }
            }
        }
        return writer
    }


    @Override
    void generateDiagram(String text, File path) {
    }
}
