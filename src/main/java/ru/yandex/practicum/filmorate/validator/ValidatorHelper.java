package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exception.ValidationException;

public class ValidatorHelper {

    public static void isNull(final Object value, final String errorMessageName) throws ValidationException {
        if (value != null) {
            throw new ValidationException(String.format("%s should be null", errorMessageName));
        }
    }

    public static void isNotNull(final Object value, final String errorMessageName) throws ValidationException {
        if (value == null) {
            throw new ValidationException(String.format("%s should be not null", errorMessageName));
        }
    }
}
