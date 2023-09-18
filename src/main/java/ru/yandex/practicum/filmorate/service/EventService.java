package ru.yandex.practicum.filmorate.service;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventDbStorage;
import ru.yandex.practicum.filmorate.model.Event;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {
    private final EventDbStorage eventDbStorage;

    public List<Event> getEvents(final int userId) {
        final Event event = Event.builder()
            .eventId(1L)
            .entityId(1)
            .userId(userId)
            .eventType(Event.EventType.FRIEND)
            .operation(Event.Operation.REMOVE)
            .timestamp(Instant.now())
            .build();
        return List.of(event);
    }
}
