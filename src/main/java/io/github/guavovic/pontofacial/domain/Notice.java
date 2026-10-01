package io.github.guavovic.pontofacial.domain;

import java.time.LocalDateTime;

public record Notice(long id, LocalDateTime createdAt, String message) {
}
