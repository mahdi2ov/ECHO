package echo.model;

import java.time.LocalDateTime;

import echo.util.DateTimeUtil;

public class MessageEdit {
    private final String oldContent;
    private final LocalDateTime editedAt;
    
    // constructor
    public MessageEdit(String oldContent) {
        this.oldContent = oldContent;
        this.editedAt = DateTimeUtil.now();
    }

    // getters (no setters because fields are constant)
    public String getOldContent() {
        return this.oldContent;
    }
    public LocalDateTime getEditedAt() {
        return this.editedAt;
    }
    
    // equals, haghCode and toString
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.oldContent == null) ? 0 : this.oldContent.hashCode());
        result = prime * result + ((this.editedAt == null) ? 0 : this.editedAt.hashCode());
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
        MessageEdit other = (MessageEdit) obj;
        if (this.oldContent == null) {
            if (other.oldContent != null)
                return false;
        } else if (!this.oldContent.equals(other.oldContent))
            return false;
        if (this.editedAt == null) {
            if (other.editedAt != null)
                return false;
        } else if (!this.editedAt.equals(other.editedAt))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("MessageEdit [oldContent = %.10s, editedAt = %s]", this.oldContent, this.editedAt);
    }
    
}
