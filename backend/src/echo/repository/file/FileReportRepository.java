package echo.repository.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import echo.config.AppConfig;
import echo.model.Report;
import echo.repository.ReportRepository;
import echo.util.FileUtil;

public class FileReportRepository implements ReportRepository {
    private final Path reportData;
    private final List<Report> inMemoryReportData;

    // constructor
    public FileReportRepository() {
        reportData = Paths.get(AppConfig.getReportsPath());
        inMemoryReportData = new ArrayList<>();
        readFile();
    }

    // create
    @Override
    public void saveReport(Report report) {
        inMemoryReportData.add(report);
        writeFile();
    }

    // read
    @Override
    public synchronized Report getReportById(String id) {
        for (Report report : inMemoryReportData) {
            if (report.getId().equals(id)) {
                return report;
            }
        }
        return null;
    }

    @Override
    public synchronized boolean existById(String id) {
        return getReportById(id) != null;
    }

    @Override
    public synchronized List<Report> getAllReports() {
        return new ArrayList<>(inMemoryReportData);
    }

    // update
    @Override
    public synchronized void updateReport(Report report) {
        for (int i = 0; i < inMemoryReportData.size(); i++) {
            if (inMemoryReportData.get(i).getId().equals(report.getId())) {
                inMemoryReportData.set(i, report);
                break;
            }
        }
        writeFile();
    }

    // delete
    @Override
    public synchronized void deleteReportById(String id) {
        for (int i = 0; i < inMemoryReportData.size(); i++) {
            if (inMemoryReportData.get(i).getId().equals(id)) {
                inMemoryReportData.remove(i);
                break;
            }
        }
        writeFile();
    }

    // util methods
    @Override
    public synchronized List<Report> getReportsByReporterId(String id) {
        List<Report> reportsFound = new ArrayList<>();
        for (int i = 0; i < inMemoryReportData.size(); i++) {
            if (inMemoryReportData.get(i).getReporterUserId().equals(id)) {
                reportsFound.add(inMemoryReportData.get(i));
            }
        }
        return reportsFound;
    }

    @Override
    public synchronized List<Report> getReportsByReportedMessageId(String id) {
        List<Report> reportsFound = new ArrayList<>();
        for (int i = 0; i < inMemoryReportData.size(); i++) {
            if (inMemoryReportData.get(i).getReportedMessageId().equals(id)) {
                reportsFound.add(inMemoryReportData.get(i));
            }
        }
        return reportsFound;
    }

    // private util methods for using in this class
    private synchronized void readFile() {
        inMemoryReportData.clear();
 
        try (BufferedReader reader = Files.newBufferedReader(reportData, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                inMemoryReportData.add(FileUtil.deserializeReport(line));
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    private synchronized void writeFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(reportData, StandardCharsets.UTF_8)) {
            for (Report report : inMemoryReportData) {
                writer.write(FileUtil.serializeReport(report));
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
