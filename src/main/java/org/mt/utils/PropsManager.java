package org.mt.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class PropsManager {

    private static volatile PropsManager instance = null;
    private Properties properties;

    private PropsManager() {
    }

    public synchronized static PropsManager getInstance() {
        if (instance == null) {
            synchronized (PropsManager.class) {
                if (instance == null) {
                    instance = new PropsManager();
                    instance.loadProperties();
                }
            }
        }
        return instance;
    }

    private void loadProperties() {
        properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("trello.properties"));
        } catch (IOException ex) {
            System.out.println("Exception: " + ex);
            throw new Error("IOException while load config: " + ex.getMessage());
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public void set(String key, String value) {
        properties.setProperty(key, value);
    }

    public List<String> getList(String key) {
        var property = get(key);
        property = property.replace("[", "");
        property = property.replace("]", "");
        return Arrays.asList(property.split("\\s*,\\s*"));
    }

    public void setList(String key, List<String> stringList) {
        set(key, stringList.toString());
    }
}
