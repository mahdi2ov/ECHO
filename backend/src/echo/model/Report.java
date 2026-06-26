package echo.model;

import java.time.LocalDateTime;

public class Report {
    private final String id;
    private final String reportedMessageId;
    private final String reporterUserId;
    private final String reason;
    private final LocalDateTime reportedAt;
    private boolean resolved;

    // constructor
    public Report(String id, String reportedMessageId, String reporterUserId, String reason) {
        this.id = id;
        this.reportedMessageId = reportedMessageId;
        this.reporterUserId = reporterUserId;
        this.reason = reason;
        this.reportedAt = LocalDateTime.now();
        this.resolved = false;
    }
    
    // getters and setters
    public String getId() {
        return this.id;
    }
    public String getReportedMessageId() {
        return this.reportedMessageId;
    }
    public String getReporterUserId() {
        return this.reporterUserId;
    }
    public String getReason() {
        return this.reason;
    }
    public LocalDateTime getReportedAt() {
        return this.reportedAt;
    }
    public boolean getResolved() {
        return this.resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    // equals, hashCode and toString
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        Report other = (Report) obj;
        if (this.id == null) {
            if (other.id != null)
                return false;
        } else if (!this.id.equals(other.id))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "Report [id=" + id + ", reportedMessageId=" + reportedMessageId + ", reporterUserId=" + reporterUserId
                + ", reason=" + reason + ", reportedAt=" + reportedAt + ", resolved=" + resolved + "]";
    }
}
