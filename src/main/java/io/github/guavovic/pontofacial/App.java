package io.github.guavovic.pontofacial;

import java.time.Clock;
import java.util.Arrays;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import io.github.guavovic.pontofacial.config.DataDirectory;
import io.github.guavovic.pontofacial.recognition.ImageComparator;
import io.github.guavovic.pontofacial.service.AttendanceService;
import io.github.guavovic.pontofacial.service.EmployeeService;
import io.github.guavovic.pontofacial.service.ReportService;
import io.github.guavovic.pontofacial.service.Services;
import io.github.guavovic.pontofacial.storage.AttendanceRepository;
import io.github.guavovic.pontofacial.storage.Database;
import io.github.guavovic.pontofacial.storage.EmployeeRepository;
import io.github.guavovic.pontofacial.storage.NoticeRepository;
import io.github.guavovic.pontofacial.storage.PhotoStore;
import io.github.guavovic.pontofacial.storage.SampleData;
import io.github.guavovic.pontofacial.storage.StorageException;
import io.github.guavovic.pontofacial.ui.EmployeeScreen;
import io.github.guavovic.pontofacial.ui.ManagementScreen;
import io.github.guavovic.pontofacial.ui.Theme;

public final class App {

    private App() {
    }

    public static void main(String[] args) {
        boolean management = Arrays.asList(args).contains("--admin");
        SwingUtilities.invokeLater(() -> {
            Theme.install();
            start(management);
        });
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

            Services services = new Services(attendance, new EmployeeService(employees, photos),
                    new ReportService());
            if (management) {
                new ManagementScreen(services).open();
            } else {
                new EmployeeScreen(services, data.testPhotos()).open();
            }
        } catch (StorageException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Ponto Facial", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
