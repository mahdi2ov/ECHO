import {apiClient, ApiError} from '../model/apiClient.js';
import {setUser, clearUser} from '../model/state.js';
import {
    required, minLength, isValidEmail, passwordsMatch, validateAll,
    hasUppercase, hasLowercase, hasDigit, hasSpecialChar, notContainsUsername,
} from '../util/validators.js';

const toMessage = (error) => (error instanceof ApiError ? error.message : 'Something went wrong. Please try again.');

export const login = async (username, password) => {
    const validationError = validateAll(
        () => required(username, 'Username'),
        () => required(password, 'Password'),
    );
    if (validationError) return {success: false, error: validationError};

    try {
        const user = await apiClient.login(username, password); // {id, username, profileImagePath}
        setUser(user);
        return {success: true, user};
    } catch (error) {
        return {success: false, error: toMessage(error)};
    }
};

export const signup = async (username, email, password, confirmPassword) => {
    const validationError = validateAll(
        () => required(username, 'Username'),
        () => required(email, 'Email'),
        () => isValidEmail(email),
        () => required(password, 'Password'),
        () => minLength(password, 8, 'Password'),
        () => hasUppercase(password),
        () => hasLowercase(password),
        () => hasDigit(password),
        () => hasSpecialChar(password),
        () => notContainsUsername(password, username),
        () => passwordsMatch(password, confirmPassword),
    );
    if (validationError) return {success: false, error: validationError};

    try {
        const user = await apiClient.signup(username, email, password, confirmPassword);
        setUser(user);
        return {success: true, user};
    } catch (error) {
        return {success: false, error: toMessage(error)};
    }
};

export const requestPasswordReset = async (username, email) => {
    const validationError = validateAll(
        () => required(username, 'Username'),
        () => required(email, 'Email'),
        () => isValidEmail(email),
    );
    if (validationError) return {success: false, error: validationError};

    try {
        const result = await apiClient.forgotPassword(username, email);
        return {success: true, tempPassword: result?.newPassword};
    } catch (error) {
        return {success: false, error: toMessage(error)};
    }
};

export const logout = async () => {
    try {
        await apiClient.logout();
    } catch (error) {
        console.warn('Server logout failed, clearing local session anyway:', error);
    } finally {
        clearUser();
    }
};