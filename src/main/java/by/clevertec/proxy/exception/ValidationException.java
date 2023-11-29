package by.clevertec.proxy.exception;

import java.util.List;
import java.util.stream.Collectors;

public class ValidationException extends RuntimeException {

    public ValidationException(List<String> errors) {
        super(errors.stream()
                .collect(Collectors.joining(System.lineSeparator())));
    }
}
