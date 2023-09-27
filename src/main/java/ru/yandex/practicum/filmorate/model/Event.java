package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    private Integer eventId;
    private Integer userId;
    private Integer entityId;
    private EventType eventType;
    private Operation operation;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER, without = JsonFormat.Feature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
    private Instant timestamp;
}
