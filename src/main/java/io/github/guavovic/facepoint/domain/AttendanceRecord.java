package io.github.guavovic.facepoint.domain;

import java.time.LocalDateTime;

public record AttendanceRecord(long id, Employee employee, LocalDateTime recordedAt, double similarity) {
}
