package backend.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Properties;

@Slf4j
public class PropertiesUtil {
    private static Properties properties;
    public static Properties getProperties(String path){
        try (InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream(path)) {
            properties = new Properties();
            properties.load(is);
            return properties;
        } catch (IOException e) {
            log.error("cannot read properties from properties file",e);
        }
        return new Properties();
    }
    public static void loadProperties(String path) {
        try (InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream(path)) {
            properties = new Properties();
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        Properties properties = getProperties("app.properties");
        System.out.println("18752488860".equals(properties.get("username")));
        FileOutputStream os = new FileOutputStream(new File(PropertiesUtil.class.getClassLoader().getResource("app.properties").toURI()));
        properties.put("username", "maxwangein@gmail.com");
        System.out.println(properties.size());
        properties.store(os,null);
    }
}
