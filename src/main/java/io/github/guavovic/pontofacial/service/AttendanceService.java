package io.github.guavovic.pontofacial.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.imageio.ImageIO;

import io.github.guavovic.pontofacial.domain.AttendanceRecord;
import io.github.guavovic.pontofacial.domain.Employee;
import io.github.guavovic.pontofacial.domain.Notice;
import io.github.guavovic.pontofacial.recognition.ImageComparator;
import io.github.guavovic.pontofacial.storage.AttendanceRepository;
import io.github.guavovic.pontofacial.storage.EmployeeRepository;
import io.github.guavovic.pontofacial.storage.NoticeRepository;
import io.github.guavovic.pontofacial.storage.PhotoStore;

public final class AttendanceService {

    public record PhotoScore(Employee employee, Path photo, double similarity) {
    }

    public record PunchResult(List<PhotoScore> scores, Optional<AttendanceRecord> record) {
    }

    private static final Locale LOCALE = Locale.forLanguageTag("pt-BR");

    private final EmployeeRepository employees;
    private final AttendanceRepository attendance;
    private final NoticeRepository notices;
    private final PhotoStore photos;
    private final ImageComparator comparator;
    private final Clock clock;

    public AttendanceService(EmployeeRepository employees, AttendanceRepository attendance, NoticeRepository notices,
            PhotoStore photos, ImageComparator comparator, Clock clock) {
        this.employees = employees;
        this.attendance = attendance;
        this.notices = notices;
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

        if (record.isEmpty()) {
            notices.add(now, unrecognizedMessage(photo, best));
        }

        return new PunchResult(scores, record);
    }

    public List<AttendanceRecord> recordsOf(Employee employee) {
        return attendance.findByEmployee(employee);
    }

    public List<AttendanceRecord> allRecords() {
        return attendance.findAll();
    }

    public List<AttendanceRecord> records(Employee employee, LocalDate from, LocalDate to) {
        return attendance.find(employee == null ? null : employee.id(), from == null ? null : from.atStartOfDay(),
                to == null ? null : to.plusDays(1).atStartOfDay());
    }

    public void deleteRecord(AttendanceRecord record) {
        attendance.delete(record.id());
    }

    public List<Notice> notices() {
        return notices.findAll();
    }

    public void clearNotices() {
        notices.deleteAll();
    }

    private static String unrecognizedMessage(Path photo, PhotoScore best) {
        if (best == null) {
            return "Foto " + photo.getFileName() + " não reconhecida: não há funcionário com foto cadastrada.";
        }
        return String.format(LOCALE, "Foto %s não reconhecida (mais parecido: %s, %.2f%%).", photo.getFileName(),
                best.employee().name(), best.similarity() * 100);
    }

    private static BufferedImage read(Path file) throws IOException {
        BufferedImage image = ImageIO.read(file.toFile());
        if (image == null) {
            throw new IOException("O arquivo " + file.getFileName() + " não é uma imagem válida");
        }
        return image;
    }
}
