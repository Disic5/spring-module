package ru.yandex.practicum.catsgram.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = "id")
@Data
public class Image {
    private Long id;
    private long postId;
    private String originalFilename;
    private String filePath;
}
