package echo.service;

import java.util.List;

import echo.model.User;
import echo.repository.UserRepository;
import echo.security.AccountLockManager;
import echo.security.PasswordHasher;
import echo.security.validation.password.PasswordValidator;
import echo.util.IdGenerator;

public class AuthService {
    private final UserRepository userRepository;
    private final PasswordValidator passwordValidator;
    private final AccountLockManager accountLockManager;

    // constructor
    public AuthService(UserRepository userRepository, PasswordValidator passwordValidator,
                        AccountLockManager accountLockManager) {
        this.userRepository = userRepository;
        this.passwordValidator = passwordValidator;
        this.accountLockManager = accountLockManager;
    }

    // TODO: methods input validation
    // TODO: make spacial exceptions
    
    public synchronized User singup(String username , String password, String confirmPassword, String email) {

        // checking confirmation password
        if (!confirmPassword.equals(password)) {
            throw new RuntimeException("Please confirm your password correctly.");
        }

        // username can't repeat // TODO
        if (userRepository.existByUsername(username)) {
            throw new RuntimeException("Username already exists.");
        }

        // password and username validation:
        List<String> validationErrors = passwordValidator.validate(password, username); 
        if (!validationErrors.isEmpty()) {
            throw new RuntimeException(String.join("\n", validationErrors));
        }

        // create new user and hashing password
        String id = IdGenerator.nextUserId();
        String passwordSalt = PasswordHasher.randomString();
        String passwordHash = PasswordHasher.hash(confirmPassword, passwordSalt);
        User newUser = new User(username, email, id, passwordHash);
        newUser.setPasswordsalt(passwordSalt);
        userRepository.saveUser(newUser);
        return newUser;
    }

    public synchronized User login(String username, String password){
        // input validation // TODO

        // user must exist before login
        User existUser = userRepository.getUserByUsername(username);
        if (existUser == null) {
            throw new RuntimeException("Username or password is invalid.");
        }
        
        // checking failed attempts and lock account using account lock manager
        String UserId = existUser.getId();
        if (accountLockManager.isLocked(UserId)) {
            throw new RuntimeException("Account is locked because of too many failed attempts.");
        }

        // checking input password
        String passwordSalt = existUser.getPasswordsalt(), passwordHash = existUser.getPasswordHash();
        String inputPasswordHash = PasswordHasher.hash(password, passwordSalt);
        if (!inputPasswordHash.equals(passwordHash)) {
            accountLockManager.increaseFailedAttempt(UserId);
            throw new RuntimeException("Password is invalid for entered username.");
        }

        // signup
        accountLockManager.reset(UserId);
        return existUser;
    }

    public synchronized String passwordRecovery(String username, String email) {
        // input validation // TODO

        // user must exist before password recovery
        User existUser = userRepository.getUserByUsername(username);
        if (existUser == null) {
            throw new RuntimeException("User not exist.");
        }

        if (existUser.getEmail() != email) {
            throw new RuntimeException("User email is invalid.");
        }

        // generate random password
        String randomPassword = PasswordHasher.randomString();
        String randomPasswordSalt = PasswordHasher.randomString();
        String randomPasswordHash = PasswordHasher.hash(randomPassword, randomPasswordSalt);
        
        // update user password
        existUser.setPasswordsalt(randomPasswordSalt);
        existUser.setPasswordHash(randomPasswordHash);
        userRepository.updateUser(existUser);

        accountLockManager.reset(existUser.getId());
        return randomPassword;
    }

    public synchronized void logout() {
        // TODO
    }
}
