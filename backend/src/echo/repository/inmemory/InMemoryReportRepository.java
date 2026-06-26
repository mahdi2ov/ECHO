package echo.repository.inmemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import echo.model.Report;
import echo.repository.ReportRepository;

public class InMemoryReportRepository implements ReportRepository {
    // in memory implementation for deadline one
    private final Map<String, Report> reportsById;

    // constructor
    public InMemoryReportRepository() {
        this.reportsById = new HashMap<>();
    }

    // create
    @Override
    public synchronized void saveReport(Report report) {
        reportsById.put(report.getId(), report);
    }

    // read
    @Override
    public synchronized Report getReportById(String id) {
        return reportsById.get(id);
    }

    @Override
    public synchronized boolean existById(String id) {
        return reportsById.get(id) != null;
    }

    @Override
    public synchronized List<Report> getAllReports() {
        return new ArrayList<>(reportsById.values());
    }

    // update
    @Override
    public synchronized void updateReport(Report report) {
        reportsById.put(report.getId(), report);
    }

    // delete
    @Override
    public synchronized void deleteReportById(String id) {
        reportsById.remove(id);
    }

    // util methods
    @Override
    public synchronized List<Report> getReportsByReportedMessageId(String id) {
        List<Report> reportsFound = new ArrayList<>();
        for (Report report : reportsById.values()) {
            if (report.getReportedMessageId().equals(id)) {
                reportsFound.add(report);
            }
        }
        return reportsFound;
    }

    @Override
    public synchronized List<Report> getReportsByReporterId(String id) {
        List<Report> reportsFound = new ArrayList<>();
        for (Report report : reportsById.values()) {
            if (report.getReporterUserId().equals(id)) {
                reportsFound.add(report);
            }
        }
        return reportsFound;
    }
}
