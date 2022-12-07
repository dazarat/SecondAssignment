package task2;

import utils.PropertyReader;
import java.util.Properties;
public class Main {
    public static void main(String[] args) {
        Properties properties = PropertyReader.readProperties("src/main/resources/name.properties");
        FinesProcessor.getFinesStatistics(properties.getProperty("task2.inputFolder"), properties.getProperty("task2.outputXml"));
    }
}
