// Authentication controller: login, signup, password reset, and logout handlers with validation and error messaging

import {apiClient, ApiError} from "../model/apiClient.js";
import {setUser, clearUser, getUserId} from "../model/state.js";
import {
    required, minLength, passwordsMatch, validateAll, hasUppercase, hasLowercase
    , hasDigit, hasSpecialChar, notContainsUsername
} from "../util/validators.js";

const toMessage = (error) => {
    return (error instanceof ApiError) ? error.message : "Something went wrong. Please try again.";
};

export const login = async (username, password) => {
    const validationError = validateAll(
        () => required(username, "Username"),
        () => required(password, "Password"),
    );
    if (validationError) return {success: false, error: validationError};
    try {
        const data = await apiClient.login(username, password);
        setUser({id: data.userId, username: data.username});
        return {success: true, user: data};
    } catch (error) {
        return {success: false, error: toMessage(error)};
    }
};

export const signup = async (username, password, confirmPassword) => {
    const validationError = validateAll(
        () => required(username, "Username"),
        () => required(password, "Password"),
        () => minLength(password, 8, "Password"),
        () => hasUppercase(password),
        () => hasLowercase(password),
        () => hasDigit(password),
        () => hasSpecialChar(password),
        () => notContainsUsername(password, username),
        () => passwordsMatch(password, confirmPassword),
    );
    if (validationError) return {success: false, error: validationError};
    try {
        const data = await apiClient.signup(username, password, confirmPassword);
        setUser({id: data.userId, username: data.username});
        return {success: true, user: data};
    } catch (error) {
        return {success: false, error: toMessage(error)};
    }
};

export const requestPasswordReset = async (username) => {
    const validationError = required(username, "Username");
    if (validationError) return {success: false, error: validationError};
    try {
        const tempPassword = await apiClient.forgotPassword(username);
        return {success: true, tempPassword};
    } catch (error) {
        return {success: false, error: toMessage(error)};
    }
};

export const logout = async () => {
    const userId = getUserId();
    try {
        if (userId) await apiClient.logout(userId);
    } catch {

    } finally {
        clearUser();
    }
};
