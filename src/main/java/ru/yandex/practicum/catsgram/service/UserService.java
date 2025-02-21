package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAllUsers() {
        return users.values();
    }

    public User createUser(@RequestBody User user) {
        String email = user.getEmail();
        if (email == null || email.isEmpty()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        validateEmailToDuplicate(email);

        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);
        return user;
    }

    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.values()
                .stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundException(String.format("Автор с id = %d не найден", id))));
    }

    public User updateUser(@RequestBody User user) {
        validateEmailToDuplicate(user.getEmail());
        if (users.containsKey(user.getId())) {
            User oldUser = users.get(user.getId());
            if (user.getEmail() == null || user.getPassword() == null || user.getUsername() == null) {
                return oldUser;
            }
            oldUser.setEmail(user.getEmail());
            oldUser.setPassword(user.getPassword());
            oldUser.setUsername(user.getUsername());
            return user;
        } else {
            throw new NotFoundException("Id должен быть указан");
        }
    }

    private void validateEmailToDuplicate(String email) {
        Optional<User> existingEmail = users.values()
                .stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();

        if (existingEmail.isPresent()) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
