package ru.yandex.practicum.filmorate.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.EventDbStorage;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventDbStorageImpl implements EventDbStorage {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public Event createEvent(final Event event) {
        final String sql = "INSERT INTO events (user_id, entity_id, event_type, operation, event_timestamp) " +
            "values(:user_id, :entity_id, :event_type, :operation, :event_timestamp)";
        final SqlParameterSource parameters = new MapSqlParameterSource()
            .addValue("user_id", event.getUserId())
            .addValue("entity_id", event.getEntityId())
            .addValue("event_type", event.getEventType().name())
            .addValue("operation", event.getOperation().name())
            .addValue("event_timestamp", event.getTimestamp());
        final KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcOperations.update(sql, parameters, keyHolder, new String[] {"id"});
        event.setEventId(keyHolder.getKey().intValue());
        return event;
    }

    @Override
    public List<Event> getFeed(final int userId) {
        final String sql = "SELECT e.id, e.user_id, e.entity_id, e.event_type, e.operation, e.event_timestamp " +
            "FROM events e " +
            "WHERE e.user_id = :user_id " +
            "ORDER BY e.event_timestamp";
        final SqlParameterSource parameters = new MapSqlParameterSource()
            .addValue("user_id", userId);
        return namedParameterJdbcOperations.query(sql, parameters, this::mapRowToEvent);
    }

    private Event mapRowToEvent(final ResultSet row, final int rowNumber) throws SQLException {
        return Event.builder()
            .eventId(row.getInt("id"))
            .userId(row.getInt("user_id"))
            .entityId(row.getInt("entity_id"))
            .eventType(EventType.valueOf(row.getString("event_type")))
            .operation(Operation.valueOf(row.getString("operation")))
            .timestamp(row.getTimestamp("event_timestamp").toInstant())
            .build();
    }
}
