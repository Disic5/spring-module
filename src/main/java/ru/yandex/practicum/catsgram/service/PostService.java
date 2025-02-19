package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {
    private final UserService userService;
    private final Map<Long, Post> posts = new HashMap<>();

    public Collection<Post> findAll(String sort, Integer size, Integer from) {
        var validSize = (size == null || size > 10) ? 10 : size;
        var validFrom = (from == null || from < 0) ? 0 : from;
        SortOrder sortOrder = sort == null ? SortOrder.ASCENDING : SortOrder.from(sort);
        Comparator<Post> comparator = Comparator.comparing(Post::getPostDate);
        if (sortOrder == SortOrder.DESCENDING) {
            comparator = comparator.reversed();
        }

        List<Post> sortedPosts = posts.values().stream().sorted(comparator).toList();

        // Проверка: если `from` больше количества постов, корректируем его
        if (validFrom >= sortedPosts.size()) {
            validFrom = Math.max(0, sortedPosts.size() - validSize);
        }
        return sortedPosts.stream()
                .skip(validFrom)
                .limit(validSize)
                .toList();
    }

    public Post create(Post post) throws ConditionsNotMetException {
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }
        if (post.getAuthorId() == null) {
            throw new NotFoundException("id автора не должно быть null");
        }
        userService.getUserById(post.getAuthorId());
        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post findById(long id) {
        return posts.values()
                .stream()
                .filter(post -> post.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("id is not found"));
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
