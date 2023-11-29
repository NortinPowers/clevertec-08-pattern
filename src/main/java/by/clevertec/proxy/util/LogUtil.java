package by.clevertec.proxy.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LogUtil {

    /**
     * Формирует форматированное сообщение об ошибке для лога.
     *
     * @param methodName  - имя метода в котором произошла ошибка.
     * @param classObject - объект класса, к которому относится метод.
     * @param <T>         - класс, к которому относится метод.
     * @return форматированное сообщение об ошибке.
     */
    public static <T> String getErrorMessageToLog(String methodName, T classObject) {
        return String.format("Exception in the %s inside the %s\n", methodName, classObject.getClass().getName());
    }
}
