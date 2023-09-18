package ru.yandex.practicum.filmorate.validator.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.validator.EventValidator;

@Component
public class RequiredFieldEventValidator implements EventValidator {
    @Override
    public void validateCreate(Event event) throws ValidationException {

    }
}
