package echo.repository;

import java.util.List;

import echo.model.Report;

public interface ReportRepository {
    // create
    void saveReport(Report report);
    // read
    Report getReportById(String id);
    boolean existById(String id);
    List<Report> getAllReports();

    // update
    void updateReport(Report report);

    // delete
    void deleteReportById(String id);

    // util methods
    List<Report> getReportsByReporterId(String id);
    List<Report> getReportsByReportedMessageId(String id);
}
