package ru.yandex.practicum.filmorate.service;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {
    private final EventDbStorage eventStorage;
    private final UserDbStorage userStorage;

    public Event raiseEvent(final Event event) throws ValidationException {
        transformCreate(event);
        validateCreate(event);
        return eventStorage.createEvent(event);
    }

    private void transformCreate(final Event event) {
        if (event.getTimestamp() == null) {
            event.setTimestamp(Instant.now());
        }
    }

    private void validateCreate(final Event event) throws ValidationException {
        if (event.getEventId() != null) {
            throw new ValidationException("Id should be null");
        }
        if (event.getUserId() == null) {
            throw new ValidationException("User Id should be not null");
        }
        if (event.getEntityId() == null) {
            throw new ValidationException("Entity Id should be not null");
        }
        if (event.getEventType() == null) {
            throw new ValidationException("Event Type id should be not null");
        }
        if (event.getOperation() == null) {
            throw new ValidationException("Operation should be not null");
        }
        if (event.getTimestamp() == null) {
            throw new ValidationException("Timestamp should be not null");
        }

        // Suppose that userId and entityId is already validated
    }

    public List<Event> getFeed(final int userId) {

        // check if user exists
        userStorage.getUserById(userId);

        // get feed for user
        return eventStorage.getFeed(userId);
    }
}
