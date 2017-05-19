package generator

import model.XModel

interface Generator {

    String getName()

    String generate(XModel model)

    void generateDiagram(String text, File path)
}