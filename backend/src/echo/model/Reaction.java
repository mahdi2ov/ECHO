package echo.model;

import java.time.LocalDateTime;

public class Reaction {
    private final String userId;
    private final ReactionType emoji;
    private final LocalDateTime reactedAt;
    
    // constructor
    public Reaction(String userId, ReactionType emoji) {
        this.userId = userId;
        this.emoji = emoji;
        this.reactedAt = LocalDateTime.now();
    }

    // getters (no setters because fields are constant)
    public String getUserId() {
        return this.userId;
    }
    public ReactionType getEmoji() {
        return this.emoji;
    }
    public LocalDateTime getReactedAt() {
        return this.reactedAt;
    }
    
    // equals, hashCode and toString
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.userId == null) ? 0 : this.userId.hashCode());
        result = prime * result + ((this.emoji == null) ? 0 : this.emoji.hashCode());
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
        Reaction other = (Reaction) obj;
        if (this.userId == null) {
            if (other.userId != null)
                return false;
        } else if (!this.userId.equals(other.userId))
            return false;
        if (this.emoji == null) {
            if (other.emoji != null)
                return false;
        } else if (!this.emoji.equals(other.emoji))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "Reaction [userId = " + this.userId + ", emoji = " + this.emoji + ", reactedAt = " + this.reactedAt + "]";
    }
}
