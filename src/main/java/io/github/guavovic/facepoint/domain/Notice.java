package io.github.guavovic.facepoint.domain;

import java.time.LocalDateTime;

public record Notice(long id, LocalDateTime createdAt, String message) {
}
