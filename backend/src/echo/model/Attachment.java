package echo.model;

public class Attachment {
    private final String fileId;
    private final String originalName;
    private final String filePath;
    private final String mimeType;
    private final long fileSize;

    // constructor
    public Attachment(String fileId, String originalName, String filePath, String mimeType, long fileSize) {
        this.fileId = fileId;
        this.originalName = originalName;
        this.filePath = filePath;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
    }
    
    // getters (no setters because fields are constant)
    public String getFileId() {
        return this.fileId;
    }
    public String getOriginalName() {
        return this.originalName;
    }
    public String getFilePath() {
        return this.filePath;
    }
    public String getMimeType() {
        return this.mimeType;
    }
    public long getFileSize() {
        return this.fileSize;
    }

    // equals, hashCode and toString
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.fileId == null) ? 0 : this.fileId.hashCode());
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
        Attachment other = (Attachment) obj;
        if (this.fileId == null) {
            if (other.fileId != null)
                return false;
        } else if (!this.fileId.equals(other.fileId))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "Attachment [fileId = " + this.fileId + ", originalName = " + this.originalName + ", filePath = " + this.filePath
                + ", mimeType = " + this.mimeType + ", fileSize = " + this.fileSize + "]";
    }
}
