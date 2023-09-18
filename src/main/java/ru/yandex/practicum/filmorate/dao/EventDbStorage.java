package ru.yandex.practicum.filmorate.dao;

import java.util.List;
import ru.yandex.practicum.filmorate.model.Event;

public interface EventDbStorage {

    Event createEvent(final Event event);

    List<Event> getFeed(final int userId);
}
