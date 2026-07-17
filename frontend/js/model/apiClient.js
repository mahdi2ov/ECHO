const DEFAULT_BASE_URL = "http://localhost:8080";

class ApiClient {
    #baseUrl;

    constructor(baseUrl = DEFAULT_BASE_URL) {
        this.#baseUrl = baseUrl;
    }

    async #request(path, {method = 'GET', body} = {}) {
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

    // --- Auth ---

    signup(username, email, password, confirmPassword) {
        return this.#request('/auth/signup', {method: 'POST', body: {username, email, password, confirmPassword}});
    }

    login(username, password) {
        return this.#request('/auth/login', {method: 'POST', body: {username, password}});
    }

    forgotPassword(username, email) {
        return this.#request('/auth/forgot-password', {method: 'POST', body: {username, email}});
    }

    logout() {
        return this.#request('/auth/logout', {method: 'POST'});
    }

    // --- Users ---

    addContact(userId, otherUserId) {
        return this.#request('/users/contacts', {method: 'POST', body: {userId, otherUserId}});
    }

    deleteContact(userId, otherUserId) {
        return this.#request('/users/contacts', {method: 'DELETE', body: {userId, otherUserId}});
    }

    blockUser(userId, otherUserId) {
        return this.#request('/users/blocked-users', {method: 'POST', body: {userId, otherUserId}});
    }

    unblockUser(userId, otherUserId) {
        return this.#request('/users/blocked-users', {method: 'DELETE', body: {userId, otherUserId}});
    }

    getUserProfile(userId) {
        return this.#request('/users/profile/get', {method: 'POST', body: {userId}});
    }

    updateProfile(userId, {username, profileImagePath} = {}) {
        return this.#request('/users/profile', {
            method: 'PUT',
            body: {userId, username, profileImagePath},
        });
    }

    listContacts(userId) {
        return this.#request('/users/contacts/list', {method: 'POST', body: {userId}});
    }

    listBlockedUsers(userId) {
        return this.#request('/users/blocked-users/list', {method: 'POST', body: {userId}});
    }

    // --- Groups ---

    createGroup(requesterId, name) {
        return this.#request('/groups', {method: 'POST', body: {requesterId, name}});
    }

    deleteGroup(requesterId, name) {
        return this.#request('/groups', {method: 'DELETE', body: {requesterId, name}});
    }

    updateGroup(requesterId, groupId, {name, description, profileImagePath} = {}) {
        return this.#request('/groups', {
            method: 'PUT',
            body: {requesterId, groupId, name, description, profileImagePath},
        });
    }

    addGroupMember(groupId, requesterId, userId) {
        return this.#request('/groups/members/add', {method: 'POST', body: {requesterId, groupId, userId}});
    }

    removeGroupMember(groupId, requesterId, userId) {
        return this.#request('/groups/members/remove', {method: 'DELETE', body: {requesterId, groupId, userId}});
    }

    addGroupAdmin(groupId, requesterId, userId) {
        return this.#request('/groups/admins/add', {method: 'POST', body: {requesterId, groupId, userId}});
    }

    removeGroupAdmin(groupId, requesterId, userId) {
        return this.#request('/groups/admins/remove', {method: 'DELETE', body: {requesterId, groupId, userId}});
    }

    getGroupMembers(groupId) {
        return this.#request('/groups/members/list', {method: 'POST', body: {groupId}});
    }

    // --- Chat ---
    sendMessage(senderId, conversationId, content, attachment = {}) {
        const {
            attachmentOriginalName = '',
            attachmentMimeType = '',
            attachmentBase64 = '',
        } = attachment;
        return this.#request('/messages', {
            method: 'POST',
            body: {senderId, conversationId, content, attachmentOriginalName, attachmentMimeType, attachmentBase64},
        });
    }

    deleteMessage(messageId, requesterId) {
        return this.#request(`/messages/${messageId}`, {method: 'DELETE', body: {messageId, requesterId}});
    }

    editMessage(messageId, requesterId, newContent) {
        return this.#request(`/messages/${messageId}`, {
            method: 'PUT',
            body: {messageId, requesterId, newContent},
        });
    }

    reactToMessage(messageId, userId, reactionType) {
        return this.#request(`/messages/${messageId}/reactions`, {
            method: 'POST',
            body: {messageId, userId, reactionType},
        });
    }

    removeReaction(messageId, userId) {
        return this.#request(`/messages/${messageId}/reactions/${userId}`, {
            method: 'DELETE',
            body: {messageId, userId},
        });
    }

    reportMessage(reportedMessageId, reporterUserId, reason) {
        return this.#request('/reports', {method: 'POST', body: {reportedMessageId, reporterUserId, reason}});
    }

    listConversations(userId) {
        return this.#request('/conversations/list', {method: 'POST', body: {userId}});
    }

    getConversationInfo(conversationId, requesterId) {
        return this.#request('/conversations/info', {method: 'POST', body: {conversationId, requesterId}});
    }

    getMessages(conversationId, since) {
        const sinceValue = since ?? 'Date: 2000-01-01, Time: 00:00:00';
        const query = `?conversationId=${encodeURIComponent(conversationId)}&since=${encodeURIComponent(sinceValue)}`;
        return this.#request(`/messages${query}`);
    }

    // --- Saved messages ---

    saveMessage(userId, messageId) {
        return this.#request('/saved-messages', {method: 'POST', body: {userId, messageId}});
    }

    deleteSavedMessage(userId, messageId) {
        return this.#request('/saved-messages', {method: 'DELETE', body: {userId, messageId}});
    }

    listSavedMessages(userId) {
        return this.#request(`/saved-messages/${userId}`);
    }
}

export class ApiError extends Error {
    constructor(message, status, payload) {
        super(message);
        this.name = 'ApiError';
        this.status = status;
        this.payload = payload;
    }
}

export const apiClient = new ApiClient();