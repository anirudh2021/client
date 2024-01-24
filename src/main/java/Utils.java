import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.Properties;

public final class Utils {
    public static HashMap<String, String> getPropertiesFromFile(String filePath){
        try {
            HashMap<String,String> hashMap = new HashMap<>();
            Properties properties = new Properties();
            FileInputStream fileInputStream = new FileInputStream(filePath);
            properties.load(new BufferedInputStream(new BufferedInputStream(fileInputStream)));
            fileInputStream.close();
            for(String propertyName : properties.stringPropertyNames())
                hashMap.put(propertyName, Optional.ofNullable(properties.getProperty(propertyName)).orElse(""));
            return  hashMap;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String enableSchedulerFlag = "client.job.enable.scheduler.flag";

    public static String schedulerCronExpression = "client.job.cron.expression";

    public static String directoryPath = "input.file.directory.path";

    public static String fileNameSuffix = "input.file.name.extention";

    public static String regexExpression = "input.file.keys.regex.filter";

    public static String serverUrl = "server.url";

    public static String charSet = "server.request.charset";

    public static String contentType = "server.request.contentType";
}
