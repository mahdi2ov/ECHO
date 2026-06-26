import {router} from "./routing/Router.js";
import {stateManager} from "./state/StateManager.js";

class App {
    init() {
        console.log('[App] Initializing...');
        stateManager.resetState();
        this.#registerEventListeners();

        router.start();

        console.log('[App] Ready.');
    }

    #registerEventListeners() {
        window.addEventListener('login', (e) => {
            const {user, token} = e.detail;
            stateManager.setToken(token);
            stateManager.setCurrentUser(user);
            router.navigate('/home');
        });

        window.addEventListener('logout', () => {
            stateManager.resetState();
            window.socketClient?.disconnect();
            router.navigate('/login');
        });

        window.addEventListener('routeChange', (e) => {
            console.log('[App] Route changed:', e.detail.path, e.detail.params);
        });
    }
}

const app = new App();

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        app.init();
    });
} else {
    app.init();
}