package by.clevertec.proxy.repository.util;

import static by.clevertec.proxy.util.LogUtil.getErrorMessageToLog;

import by.clevertec.proxy.config.AppConfig;
import java.sql.SQLException;
import java.util.Map;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.dbcp2.BasicDataSource;

@Log4j2
@UtilityClass
public class TestDataSource {

    private static final AppConfig APP_CONFIG;
    private static final int MAX_CONNECTION_COUNT;
    private static final int MIN_CONNECTION_COUNT;
    private static final String URL;
    private static final String LOGIN;
    private static final String PASS;
    private static final String DRIVER_CLASS_NAME;

    private static volatile BasicDataSource instance;

    static {
        APP_CONFIG = new AppConfig();
        Map<String, Object> databaseProperties = APP_CONFIG.getProperty("database");
        URL = databaseProperties.get("url") + "_test";
        LOGIN = (String) databaseProperties.get("username");
        PASS = (String) databaseProperties.get("password");
        MAX_CONNECTION_COUNT = (Integer) databaseProperties.get("max-connection");
        MIN_CONNECTION_COUNT = (Integer) databaseProperties.get("min-connection");
        DRIVER_CLASS_NAME = (String) databaseProperties.get("driver-class-name");

    }

    /**
     * Метод создает instance соединений BasicDataSource с БД, если его еще нет, и возвращает его.
     *
     * @return instance для BasicDataSource
     */
    public static BasicDataSource getDataSource() {
        if (instance == null) {
            instance = createDataSource();
        }
        return instance;
    }

    /**
     * Метод настраивает соединений BasicDataSource с БД.
     *
     * @return настроенное соединение BasicDataSource с БД.
     */
    private static BasicDataSource createDataSource() {
        instance = new BasicDataSource();
        instance.setDriverClassName(DRIVER_CLASS_NAME);
        instance.setUrl(URL);
        instance.setUsername(LOGIN);
        instance.setPassword(PASS);
        instance.setInitialSize(MIN_CONNECTION_COUNT);
        instance.setMaxTotal(MAX_CONNECTION_COUNT);
        return instance;
    }

    /**
     * Метод закрывает все соединения BasicDataSource с БД.
     */
    public static void closeDataSource() {
        try {
            if (instance != null) {
                instance.close();
            }
        } catch (SQLException exception) {
            log.error(getErrorMessageToLog("closeDataSource()", TestDataSource.class), exception);
        }
    }
}
