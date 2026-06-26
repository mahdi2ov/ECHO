class StateManager {
    #token = null;
    #currentUser = null;
    #currentRoute = '/login';
    #routeParams = {};
    #conversations = [];
    #currentConversation = null;
    #messages = {};
    #onlineUsers = [];
    #isWebSocketConnected = false;
    #observers = [];

    #notify() {
        const snapshot = this.getState();
        this.#observers.forEach(observer => observer(snapshot));
    }

    getState() {
        return {
            token: this.#token,
            currentUser: this.#currentUser,
            currentRoute: this.#currentRoute,
            routeParams: {...this.#routeParams},
            conversations: [...this.#conversations],
            currentConversation: this.#currentConversation,
            messages: {...this.#messages},
            onlineUsers: [...this.#onlineUsers],
            isWebSocketConnected: this.#isWebSocketConnected,
        };
    }

    updateState(newState = {}) {
        if ('token' in newState) this.#token = newState.token;
        if ('currentUser' in newState) this.#currentUser = newState.currentUser;
        if ('currentRoute' in newState) this.#currentRoute = newState.currentRoute;
        if ('routeParams' in newState) this.#routeParams = newState.routeParams;
        if ('conversations' in newState) this.#conversations = newState.conversations;
        if ('currentConversation' in newState) this.#currentConversation = newState.currentConversation;
        if ('messages' in newState) this.#messages = newState.messages;
        if ('onlineUsers' in newState) this.#onlineUsers = newState.onlineUsers;
        if ('isWebSocketConnected' in newState) this.#isWebSocketConnected = newState.isWebSocketConnected;
        this.#notify();
    }

    subscribe(observer) {
        this.#observers.push(observer);
        observer(this.getState());
        return () => {
            const index = this.#observers.indexOf(observer);
            if (index > -1) this.#observers.splice(index, 1);
        };
    }

    resetState() {
        this.#token = null;
        this.#currentUser = null;
        this.#currentRoute = '/login';
        this.#routeParams = {};
        this.#conversations = [];
        this.#currentConversation = null;
        this.#messages = {};
        this.#onlineUsers = [];
        this.#isWebSocketConnected = false;
        this.#notify();
    }

    getToken() {
        return this.#token;
    }

    setToken(token) {
        this.updateState({token});
    }

    getCurrentUser() {
        return this.#currentUser;
    }

    setCurrentUser(user) {
        this.updateState({currentUser: user});
    }

    isLoggedIn() {
        return !!this.#token && !!this.#currentUser;
    }

    clearAuth() {
        this.updateState({token: null, currentUser: null});
    }
}

export const stateManager = new StateManager();