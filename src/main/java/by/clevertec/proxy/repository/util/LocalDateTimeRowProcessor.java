package by.clevertec.proxy.repository.util;

import org.apache.commons.dbutils.BasicRowProcessor;

public class LocalDateTimeRowProcessor extends BasicRowProcessor {

    public LocalDateTimeRowProcessor(LocalDateTimeProcessor processor) {
        super(processor);
    }
}
