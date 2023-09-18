package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;

public interface EventValidator {
    void validateCreate(final Event event) throws ValidationException;
}
