package echo.security;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import echo.config.AppConfig;
import echo.util.DateTimeUtil;

public class AccountLockManager {
    private final Map<String, Integer> userFailedAttemptsById;
    private final Map<String, LocalDateTime> userLockUntilsById;

    private static final int MAX_LOGIN_ATTEMPTS = AppConfig.getMaxLoginAttempts();
    private static final int LOCK_ACCOUNT_MINUTES = AppConfig.getLockAccountMinutes();
    
    // constructor
    public AccountLockManager() {
        this.userFailedAttemptsById = new HashMap<>();
        this.userLockUntilsById = new HashMap<>();
    }

    // method for increase failed attemps
    public synchronized void increaseFailedAttempt(String id) {
        if (this.isLocked(id)) {
            return;
        }
        int attempts = this.userFailedAttemptsById.get(id) != null ? this.userFailedAttemptsById.get(id) : 0;
        this.userFailedAttemptsById.put(id, attempts + 1);
        if (attempts + 1 >= MAX_LOGIN_ATTEMPTS) {
            this.lock(id);
        }
    }

    // method for reseting failed attemps and lock until of an account
    public synchronized void reset(String id) {
        this.userFailedAttemptsById.remove(id);
        this.userLockUntilsById.remove(id);
    }

    // method for checking an account is locked
    public synchronized boolean isLocked(String id) {
        LocalDateTime lockUntil = this.userLockUntilsById.get(id);
        if (lockUntil == null) {
            return false;
        }
        if (DateTimeUtil.isFinished(lockUntil)) {
            this.unlock(id);
            return false;
        }
        return true;
    }

    // method to lock an account
    public synchronized void lock(String id) {
        this.userLockUntilsById.put(id, DateTimeUtil.plusMinutes(DateTimeUtil.now(), LOCK_ACCOUNT_MINUTES));
        this.userFailedAttemptsById.remove(id);
    }

    // method for unlocking an account if lock time is finished
    public synchronized void unlock(String id) {
        this.userLockUntilsById.remove(id);
        this.userFailedAttemptsById.remove(id);
    }
}
