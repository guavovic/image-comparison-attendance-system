package io.github.guavovic.facepoint.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public record PunchResult(List<PhotoScore> scores, List<AttendanceRecord> records) {
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
        List<AttendanceRecord> records = new ArrayList<>();

        for (Employee employee : employees.findAll()) {
            for (Path registered : photos.photosOf(employee)) {
                double similarity = comparator.similarity(probe, read(registered));
                scores.add(new PhotoScore(employee, registered, similarity));
                if (similarity >= ImageComparator.THRESHOLD) {
                    records.add(attendance.add(employee, now, similarity));
                }
            }
        }

        return new PunchResult(scores, records);
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
