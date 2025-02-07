package ru.yandex.practicum.catsgram.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@EqualsAndHashCode(of = "email")
@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private Instant registrationDate;
}

