package by.clevertec.proxy.util;

import static by.clevertec.proxy.util.LogUtil.getErrorMessageToLog;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.jdbc.ScriptRunner;

@UtilityClass
@Log4j2
public class DbUtil {
    public static final String SCRIPT_FILE_INIT = "src/test/resources/init.sql";
    public static final String SCRIPT_FILE_FILL = "src/test/resources/fill.sql";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/proxy_test";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "root";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (SQLException e) {
            System.out.println("Connection exception: " + e.getMessage());
        }
        return connection;
    }

    public static void executeScript(String scriptFileAddress) {
        Connection connection = getConnection();
        ScriptRunner runner = new ScriptRunner(connection);
        Reader reader = null;
        try {
            reader = new BufferedReader(new FileReader(scriptFileAddress));
        } catch (FileNotFoundException exception) {
            log.error(getErrorMessageToLog("executeScript()", DbUtil.class), exception);
        }
        runner.runScript(reader);
        try {
            connection.close();
        } catch (SQLException exception) {
            log.error(getErrorMessageToLog("executeScript()", DbUtil.class), exception);
        }
    }
}
