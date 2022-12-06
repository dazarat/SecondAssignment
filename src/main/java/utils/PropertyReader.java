package utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertyReader {

    public static Properties readProperties(String path) {
        Properties properties = new Properties();
        try (FileReader reader = new FileReader(path)) {
            properties.load(reader);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return properties;
    }
}
