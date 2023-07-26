package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
public class User {

    @NotNull
    private int id;
    @Email(message = "Неправильный адрес электронной почты")
    @NotEmpty(message = "Поле email не может быть пустым")
    @NotNull
    private String email;
    @NotBlank(message = "Поле login не должно быть пустым или содержать только пробельные символы")
    @NotNull
    private String login;
    @NotBlank(message = "Поле name не должно быть пустым или содержать только пробельные символы")
    @NotNull
    private String name;
    @NotNull(message = "Поле birthday не может быть null")
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public User() {
    }
}
