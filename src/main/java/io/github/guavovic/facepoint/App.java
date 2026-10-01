package io.github.guavovic.facepoint;

import java.time.Clock;
import java.util.Arrays;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import io.github.guavovic.facepoint.config.DataDirectory;
import io.github.guavovic.facepoint.recognition.ImageComparator;
import io.github.guavovic.facepoint.service.AttendanceService;
import io.github.guavovic.facepoint.service.EmployeeService;
import io.github.guavovic.facepoint.service.ReportService;
import io.github.guavovic.facepoint.service.Services;
import io.github.guavovic.facepoint.storage.AttendanceRepository;
import io.github.guavovic.facepoint.storage.Database;
import io.github.guavovic.facepoint.storage.EmployeeRepository;
import io.github.guavovic.facepoint.storage.NoticeRepository;
import io.github.guavovic.facepoint.storage.PhotoStore;
import io.github.guavovic.facepoint.storage.SampleData;
import io.github.guavovic.facepoint.storage.StorageException;
import io.github.guavovic.facepoint.ui.EmployeeScreen;
import io.github.guavovic.facepoint.ui.ManagementScreen;

public final class App {

    private App() {
    }

    public static void main(String[] args) {
        boolean management = Arrays.asList(args).contains("--admin");
        SwingUtilities.invokeLater(() -> start(management));
    }

    private static void start(boolean management) {
        try {
            DataDirectory data = DataDirectory.resolve();
            Database database = Database.open(data.database());
            EmployeeRepository employees = new EmployeeRepository(database);
            PhotoStore photos = new PhotoStore(data.photos());
            SampleData.seedIfEmpty(employees, photos, data.testPhotos());

            AttendanceService attendance = new AttendanceService(employees, new AttendanceRepository(database),
                    new NoticeRepository(database), photos, new ImageComparator(), Clock.systemDefaultZone());

            if (management) {
                Services services = new Services(attendance, new EmployeeService(employees, photos),
                        new ReportService());
                new ManagementScreen(services).open();
            } else {
                new EmployeeScreen(attendance, data.testPhotos()).open();
            }
        } catch (StorageException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "FacePoint", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
