package by.clevertec.proxy.validator;

import by.clevertec.proxy.data.ProductDto;
import by.clevertec.proxy.exception.ValidationException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductDtoValidator {

    public static final String INCORRECT_DESCRIPTION_MESSAGE = "incorrect product description";
    public static final String DESCRIPTION_REG_EX = "^[а-яА-Я\\s]{10,50}$";

    /**
     * Проверяет объект для сохранения, формирует список ошибок валидации при их наличии и выбрасывает исключение содержащее ошибки проверки.
     *
     * @param productDto - проверяемый объект.
     */
    public void validate(ProductDto productDto) {
        List<String> validateErrors = new ArrayList<>();
        validateNameToNull(productDto, validateErrors);
        validateNameContent(productDto, validateErrors);
        validateDescriptionToCorrect(productDto, validateErrors);
        validatePriceToNullAndCorrectValue(productDto, validateErrors);
        throwValidationExceptionInCaseError(validateErrors);
    }

    /**
     * Проверяет объект для обновления данных, формирует список ошибок валидации при их наличии и выбрасывает исключение содержащее ошибки проверки.
     *
     * @param productDto - проверяемый объект.
     */
    public void validateUpdated(ProductDto productDto) {
        List<String> validateErrors = new ArrayList<>();
        validateNameContent(productDto, validateErrors);
        validateUpdatedDescriptionToCorrect(productDto, validateErrors);
        validateUpdatedPriceToCorrectValue(productDto, validateErrors);
        throwValidationExceptionInCaseError(validateErrors);
    }

    /**
     * Выбрасывает исключение содержащее ошибки проверки.
     *
     * @param validateErrors - список ошибок проверки.
     */
    private static void throwValidationExceptionInCaseError(List<String> validateErrors) {
        if (!validateErrors.isEmpty()) {
            throw new ValidationException(validateErrors);
        }
    }

    /**
     * Проверяет поле Price объекта по заданным требованиям.
     *
     * @param productDto     - проверяемый объект.
     * @param validateErrors - список ошибок проверки.
     */
    private static void validatePriceToNullAndCorrectValue(ProductDto productDto, List<String> validateErrors) {
        if (productDto.price() == null) {
            validateErrors.add("null product price");
        } else {
            validatePriceToCorrectValue(productDto, validateErrors);
        }
    }

    /**
     * Проверяет поле Price объекта по заданным требованиям.
     *
     * @param productDto     - проверяемый объект.
     * @param validateErrors - список ошибок проверки.
     */
    private static void validateUpdatedPriceToCorrectValue(ProductDto productDto, List<String> validateErrors) {
        if (productDto.price() != null) {
            validatePriceToCorrectValue(productDto, validateErrors);
        }
    }

    /**
     * Проверяет поле Price объекта по заданным требованиям.
     *
     * @param productDto     - проверяемый объект.
     * @param validateErrors - список ошибок проверки.
     */
    private static void validatePriceToCorrectValue(ProductDto productDto, List<String> validateErrors) {
        if (productDto.price().compareTo(BigDecimal.ZERO) <= 0) {
            validateErrors.add("product price less or equal than 0");
        }
    }

    /**
     * Проверяет поле Description объекта по заданным требованиям.
     *
     * @param productDto     - проверяемый объект.
     * @param validateErrors - список ошибок проверки.
     */
    private static void validateDescriptionToCorrect(ProductDto productDto, List<String> validateErrors) {
        if (productDto.description() != null && !productDto.description().matches(DESCRIPTION_REG_EX)) {
            validateErrors.add(INCORRECT_DESCRIPTION_MESSAGE);
        }
    }

    /**
     * Проверяет поле Description объекта по заданным требованиям.
     *
     * @param productDto     - проверяемый объект.
     * @param validateErrors - список ошибок проверки.
     */
    private static void validateUpdatedDescriptionToCorrect(ProductDto productDto, List<String> validateErrors) {
        if (productDto.description() != null) {
            if (!productDto.description().matches(DESCRIPTION_REG_EX)) {
                validateErrors.add(INCORRECT_DESCRIPTION_MESSAGE);
            }
        }
    }

    /**
     * Проверяет поле Name объекта по заданным требованиям.
     *
     * @param productDto     - проверяемый объект.
     * @param validateErrors - список ошибок проверки.
     */
    private static void validateNameContent(ProductDto productDto, List<String> validateErrors) {
        if (productDto.name() != null) {
            validateNameToEmpty(productDto, validateErrors);
            validateNameToCorrect(productDto, validateErrors);
        }
    }

    /**
     * Проверяет поле Name объекта по заданным требованиям.
     *
     * @param productDto     - проверяемый объект.
     * @param validateErrors - список ошибок проверки.
     */
    private static void validateNameToCorrect(ProductDto productDto, List<String> validateErrors) {
        if (!productDto.name().matches("^[а-яА-Я\\s]{5,20}$")) {
            validateErrors.add("incorrect product name");
        }
    }

    /**
     * Проверяет поле Name объекта по заданным требованиям.
     *
     * @param productDto     - проверяемый объект.
     * @param validateErrors - список ошибок проверки.
     */
    private static void validateNameToEmpty(ProductDto productDto, List<String> validateErrors) {
        if (productDto.name().trim().isEmpty()) {
            validateErrors.add("empty product name");
        }
    }

    /**
     * Проверяет поле Name объекта по заданным требованиям.
     *
     * @param productDto     - проверяемый объект.
     * @param validateErrors - список ошибок проверки.
     */
    private static void validateNameToNull(ProductDto productDto, List<String> validateErrors) {
        if (productDto.name() == null) {
            validateErrors.add("null productDto name");
        }
    }
}
