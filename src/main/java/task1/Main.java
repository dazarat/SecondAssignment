package task1;

import utils.PropertyReader;
import java.util.Properties;
public class Main {
    public static void main(String[] args) {
        Properties properties = PropertyReader.readProperties("src/main/resources/name.properties");
        PersonParser.processXMLFile(properties.getProperty("task1.inputXml"), properties.getProperty("task1.outputXml"));
    }
}
