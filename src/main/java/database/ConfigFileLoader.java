package database;

import java.io.InputStream;
import java.util.Properties;

public class ConfigFileLoader {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigFileLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Config File nicht gefunden nicht gefunden!");
            }
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Laden der Konfigurationsdatei: " + e.getMessage(), e);
        }
    }

    /// Ein Stringwert aus den Properties laden
    public static String get(String key) {
        return properties.getProperty(key);
    }

}

