package generator

import groovy.json.JsonBuilder
import model.XModel

class JsonGenerator implements Generator {

    final String name = "json"

    @Override
    String generate(XModel model) {
        def json = new JsonBuilder()

        json.Model {
            name model.name
            Package(model.packages.collect { xPackage -> [
                name: xPackage.name,

                DataType: (xPackage.dataTypes.collect { xDataType -> [
                    name: xDataType.name,
                        type: xDataType.type(),
                        constraint: xDataType.constraint.body
                ] + xDataType.stereotypeProps() }),

                Enum: (xPackage.enums.collect { xEnum -> [
                    name: xEnum.name,
                    Literal: (xEnum.literals.collect { xLiteral -> [
                        name: xLiteral.name,
                        value: xLiteral.value,
                    ] + xLiteral.stereotypeProps() })

                ] + xEnum.stereotypeProps() }),

                Class: (xPackage.classes.collect { xClass -> [
                    name: xClass.name,
                    type: xClass.type(),

                    Attr: (xClass.attributes.collect { xAttr -> [
                        name: xAttr.name,
                            type: xAttr.type(),
                            isKey: xAttr.isKey(),
                            defaultValue: xAttr.defaultValue,
                            visibility: xAttr.visibility,
                            isStatic: xAttr.isStatic,
                            constraint: xAttr.constraint()?.body

                    ] + xAttr.stereotypeProps() })

                ] + xClass.stereotypeProps() })

            ] + xPackage.stereotypeProps() })
        }

        return json.toPrettyString()
    }

    @Override
    void generateDiagram(String text, File path) {
    }
}
