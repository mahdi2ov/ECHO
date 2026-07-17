package echo.dto.request;

public class ReportMessageRequest {
    private final String reportedMessageId; 
    private final String reporterUserId;
    private final String reason;
    
    // constructor
    public ReportMessageRequest(String reportedMessageId, String reporterUserId, String reason) {
        this.reportedMessageId = reportedMessageId;
        this.reporterUserId = reporterUserId;
        this.reason = reason;
    }

    // getters
    public String getReportedMessageId() {
        return reportedMessageId;
    }
    public String getReporterUserId() {
        return reporterUserId;
    }
    public String getReason() {
        return reason;
    }
}
