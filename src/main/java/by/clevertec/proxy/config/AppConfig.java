package by.clevertec.proxy.config;

import java.io.InputStream;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.yaml.snakeyaml.Yaml;

@Log4j2
public class AppConfig {

    private static final String CONFIG_FILE = "application.yaml";

    private Map<String, Map<String, Object>> config;

    public AppConfig() {
        loadConfig();
    }

    /**
     * Метод считывает файл yaml в config Map.
     */
    private void loadConfig() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            Yaml yaml = new Yaml();
            config = yaml.load(inputStream);
        } catch (Exception e) {
            log.error("Exception (loadConfig())", e);
        }
    }

    /**
     * Метод возвращает ключ-значение из config Map по ключу.
     *
     * @param key - ключ значения
     * @return Map of String/Object - ключ-значение
     */
    public Map<String, Object> getProperty(String key) {
        return config.get(key);
    }
}
