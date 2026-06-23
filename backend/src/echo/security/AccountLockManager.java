package echo.security;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import echo.config.AppConfig;
import echo.util.DateTimeUtil;

public class AccountLockManager {
    private final Map<String, Integer> failedAttempts;
    private final Map<String, LocalDateTime> lockUntils;
    
    // constructor
    public AccountLockManager() {
        this.failedAttempts = new HashMap<>();
        this.lockUntils = new HashMap<>();
    }

    // method for increase failed attemps
    public synchronized void increaseFailedAttempt(String username) {
        if (this.isLocked(username)) {
            return;
        }
        int attempts = this.failedAttempts.get(username) != null ? this.failedAttempts.get(username) : 0;
        this.failedAttempts.put(username, attempts + 1);
        if (attempts + 1 >= AppConfig.getMaxLoginAttempts()) {
            this.lock(username);
        }
    }

    // method for reseting failed attemp of an account
    public synchronized void reset(String username) {
        this.failedAttempts.remove(username);
        this.lockUntils.remove(username);
    }

    // method for checking an account is locked
    public synchronized boolean isLocked(String username) {
        LocalDateTime lockUntil = this.lockUntils.get(username);
        if (lockUntil == null) {
            return false;
        }
        if (DateTimeUtil.isFinished(lockUntil)) {
            this.unlock(username);
            return false;
        }
        return true;
    }

    // method to lock an account
    public synchronized void lock(String username) {
        this.lockUntils.put(username, DateTimeUtil.plusMinutes(DateTimeUtil.now(), AppConfig.getLockAccountMinutes()));
        this.failedAttempts.remove(username);
    }

    // method for unlocking an account if lock time is finished
    public synchronized void unlock(String username) {
        this.lockUntils.remove(username);
        this.failedAttempts.remove(username);
    }

    
}
