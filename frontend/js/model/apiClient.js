// Thin fetch() wrapper around the backend REST API

const DEFAULT_BASE_URL = "http://localhost:8080";

class ApiClient {
    #baseUrl;

    constructor(baseUrl = DEFAULT_BASE_URL) {
        this.#baseUrl = baseUrl;
    }

    async #request(path, {method = 'GET',body} = {}) {
        let response;
        try {
            response = await fetch(this.#baseUrl + path, {
                method,
                headers: {'Content-Type': 'application/json'},
                body: body !== undefined ? JSON.stringify(body) : undefined
            });
        } catch {
            throw new ApiError('Network error — please check your connection', 0, null);
        }
        let payload = null;
        try {
            payload = await response.json();
        } catch {
        }

        if (!response.ok || payload?.success === false) {
            const message = payload?.message || `Request failed (${response.status})`;
            throw new ApiError(message, response.status, payload);
        }
        return payload?.data;
    }

    // Authentication

    login(username, password) {
        return this.#request('/auth/login', {method: 'POST', body: {username, password}});
    }

    signup(username,password,confirmPassword) {
        return this.#request('/auth/signup',{method:'POST',body:{username,password,confirmPassword}});
    }

    forgotPassword(username) {
        return this.#request('/auth/forgot-password',{method:'POST',body:{username}});
    }

    logout(userId) {
        return this.#request('/auth/logout',{method:'POST',body:{userId}});
    }
}

// Special API error

export class ApiError extends Error {
    constructor(message,status,payload) {
        super(message);
        this.name = 'ApiError';
        this.status = status;
        this.payload = payload;
    }
}

export const apiClient = new ApiClient();