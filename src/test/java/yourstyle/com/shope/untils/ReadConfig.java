package yourstyle.com.shope.untils;

import java.io.FileInputStream;
import java.util.Properties;

public class ReadConfig {
    static String localDir = System.getProperty("user.dir");
    static Properties properties = new Properties();

    public static Properties loadPropertices() {
        try {
            FileInputStream inputStream = new FileInputStream(localDir + "/src/test/java/resources/config/properties");
            properties.load(inputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return properties;
    }

    public static String getProperty(String key) {
        return loadPropertices().getProperty(key);
    }

}
