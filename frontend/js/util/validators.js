// Client-side form validators (username/password rules, email format, etc.)

export const required = (value, fieldName = "This field") => {
    return (!value || !value.trim()) ? `${fieldName} is required` : null;
};

export const minLength = (value, min, fieldName = "This field") => {
    return (value && value.length < min) ? `${fieldName} must be at least ${min} characters` : null;
};

export const maxLength = (value, max, fieldName = "This field") => {
    return (value && value.length > max) ? `${fieldName} must be at most ${max} characters` : null;
};

export const isValidEmail = (value) => {
    if (!value) return null;
    const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return pattern.test(value) ? null : "Enter a valid email address";
};

export const passwordsMatch = (password, confirmPassword) => {
    return (password !== confirmPassword) ? "Passwords do not match" : null;
};

// Password complexity rules
export const hasUppercase = (value) => {
    const pattern = /[A-Z]/;
    return pattern.test(value) ? null : "Password must contain an uppercase letter";
};
export const hasLowercase = (value) => {
    const pattern = /[a-z]/;
    return pattern.test(value) ? null : "Password must contain a lowercase letter";
};

export const hasDigit = (value) => {
    const pattern = /\d/;
    return pattern.test(value) ? null : "Password must contain a digit";
};

export const hasSpecialChar = (value) => {
    const pattern = /[!@%$#^&*]/;
    return pattern.test(value) ? null : "Password must contain a special character (!@%$#^&*)";
};

export const notContainsUsername = (password, username) => {
    return (username && password.toLowerCase().includes(username.toLowerCase())) ?
        "Password must not contain your username" : null;
};

export const validateAll = (...validatorFunctions) => {
    for (const validatorFunction of validatorFunctions) {
        const error = validatorFunction();
        if (error) return error;
    }
    return null;
};