import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class ApplicationProperties {
    static Logger logger = Logger.getLogger(ApplicationProperties.class.getName());

    private static final Properties properties = new Properties();
    public void importPropertiesFromFile(String filePath){
        logger.info("Start");
        try {
            properties.putAll(Utils.getPropertiesFromFile(filePath));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        logger.info("End");
    }

    private void addProperty(String propertyName, String propertyValue){
        properties.put(propertyName,propertyValue);
    }

    public static String getProperty(String propertyName){
        return properties.getProperty(propertyName);
    }

    public String[] getAllProperties(){
        return (String[]) properties.stringPropertyNames().toArray();
    }
}
