package by.clevertec.proxy.repository.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import org.apache.commons.dbutils.GenerousBeanProcessor;

public class LocalDateTimeProcessor extends GenerousBeanProcessor {

    @Override
    protected Object processColumn(ResultSet rs, int index, Class<?> propType) throws SQLException {
        if (propType.equals(LocalDateTime.class)) {
            return rs.getTimestamp(index).toLocalDateTime();
        }
        return super.processColumn(rs, index, propType);
    }
}
