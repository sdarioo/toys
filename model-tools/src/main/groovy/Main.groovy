import generator.JsonGenerator
import generator.PlantUmlGenerator
import generator.XmlGenerator
import parser.RSMParser

def path = "c:\\Temp\\models\\site_server\\data_model\\ASTM_ATRAgentDesignModel.emx"

def parser = new RSMParser()
def model = parser.parse(path)

def generators = [ new JsonGenerator(),
                   new XmlGenerator(),
                   new PlantUmlGenerator(withExtraProperties: true),
                   new PlantUmlGenerator(withExtraProperties: false) ]

generators.each {
    println it.name

    def text = it.generate(model)

    it.generateDiagram(text, new File(path.replace(".emx", ".${it.name}.png")))

    new File(path.replace(".emx", ".${it.name}")).write(text)
}




