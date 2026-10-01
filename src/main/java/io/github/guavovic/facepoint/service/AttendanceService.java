package io.github.guavovic.facepoint.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import io.github.guavovic.facepoint.domain.AttendanceRecord;
import io.github.guavovic.facepoint.domain.Employee;
import io.github.guavovic.facepoint.recognition.ImageComparator;
import io.github.guavovic.facepoint.storage.AttendanceRepository;
import io.github.guavovic.facepoint.storage.EmployeeRepository;
import io.github.guavovic.facepoint.storage.PhotoStore;

public final class AttendanceService {

    public record PhotoScore(Employee employee, Path photo, double similarity) {
    }

    public record PunchResult(List<PhotoScore> scores, Optional<AttendanceRecord> record) {
    }

    private final EmployeeRepository employees;
    private final AttendanceRepository attendance;
    private final PhotoStore photos;
    private final ImageComparator comparator;
    private final Clock clock;

    public AttendanceService(EmployeeRepository employees, AttendanceRepository attendance, PhotoStore photos,
            ImageComparator comparator, Clock clock) {
        this.employees = employees;
        this.attendance = attendance;
        this.photos = photos;
        this.comparator = comparator;
        this.clock = clock;
    }

    public PunchResult punch(Path photo) throws IOException {
        BufferedImage probe = read(photo);
        LocalDateTime now = LocalDateTime.now(clock).withNano(0);
        List<PhotoScore> scores = new ArrayList<>();
        PhotoScore best = null;

        for (Employee employee : employees.findAll()) {
            for (Path registered : photos.photosOf(employee)) {
                double similarity = comparator.compare(probe, read(registered)).score();
                PhotoScore score = new PhotoScore(employee, registered, similarity);
                scores.add(score);
                if (best == null || similarity > best.similarity()) {
                    best = score;
                }
            }
        }

        Optional<AttendanceRecord> record = Optional.ofNullable(best)
                .filter(score -> score.similarity() >= ImageComparator.THRESHOLD)
                .map(score -> attendance.add(score.employee(), now, score.similarity()));

        return new PunchResult(scores, record);
    }

    public List<AttendanceRecord> recordsOf(Employee employee) {
        return attendance.findByEmployee(employee);
    }

    public List<AttendanceRecord> allRecords() {
        return attendance.findAll();
    }

    private static BufferedImage read(Path file) throws IOException {
        BufferedImage image = ImageIO.read(file.toFile());
        if (image == null) {
            throw new IOException("O arquivo " + file.getFileName() + " não é uma imagem válida");
        }
        return image;
    }
}
