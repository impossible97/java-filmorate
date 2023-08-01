package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private int id;
    @Email(message = "Неправильный адрес электронной почты")
    @NotEmpty(message = "Поле email не может быть пустым")
    @NotNull
    private String email;
    @NotBlank(message = "Поле login не должно быть пустым или содержать только пробельные символы")
    @NotNull
    @Pattern(regexp = "\\S+")
    private String login;
    private String name;
    @NotNull(message = "Поле birthday не может быть null")
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
