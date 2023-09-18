package ru.yandex.practicum.filmorate.dao.impl;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.EventDbStorage;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;

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
            .addValue("event_timestamp", Timestamp.from(event.getTimestamp()));
        final KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcOperations.update(sql, parameters, keyHolder, new String[] { "id" });
        final long id = keyHolder.getKey().longValue();

        event.setEventId(id);
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
            .eventId(row.getLong("id"))
            .userId(row.getInt("user_id"))
            .entityId(row.getInt("entity_id"))
            .eventType(Event.EventType.valueOf(row.getString("event_type")))
            .operation(Event.Operation.valueOf(row.getString("operation")))
            .timestamp(row.getTimestamp("event_timestamp").toInstant())
            .build();
    }
}
