import {Login} from '../components/Login.js';
// import { Home }           from '../components/Home.js';
// import { Signup }         from '../components/Signup.js';
// import { ForgotPassword } from '../components/ForgotPassword.js';
// import { Chat }           from '../components/Chat.js';
// import { ChatInfo }       from '../components/ChatInfo.js';
// import { SavedMessages }  from '../components/SavedMessages.js';
// import { Archive }        from '../components/Archive.js';
// import { Settings }       from '../components/Settings.js';
// import { NewConversation }from '../components/NewConversation.js';

import {stateManager} from "../state/StateManager.js";

class RouteRegistry {
    #routes = {
        '/login': Login,
        // '/home':             Home,
        // '/signup':           Signup,
        // '/forgot-password':  ForgotPassword,
        // '/chat':             Chat,
        // '/chat-info':        ChatInfo,
        // '/saved-messages':   SavedMessages,
        // '/archive':          Archive,
        // '/settings':         Settings,
        // '/new-conversation': NewConversation,
    };

    #metadata = {
        '/login': {title: 'login', requiresAuth: false, layout: 'auth'},
        // '/home':             { title: 'home',             requiresAuth: true,  layout: 'main' },
        // '/signup':           { title: 'signup',           requiresAuth: false, layout: 'auth' },
        // '/forgot-password':  { title: 'forgot password',  requiresAuth: false, layout: 'auth' },
        // '/chat':             { title: 'chat',             requiresAuth: true,  layout: 'main' },
        // '/chat-info':        { title: 'chat info',        requiresAuth: true,  layout: 'main' },
        // '/saved-messages':   { title: 'saved messages',   requiresAuth: true,  layout: 'main' },
        // '/archive':          { title: 'archive',          requiresAuth: true,  layout: 'main' },
        // '/settings':         { title: 'settings',         requiresAuth: true,  layout: 'main' },
        // '/new-conversation': { title: 'new conversation', requiresAuth: true,  layout: 'main' },
    };

    #publicRoutes = ['/login', '/signup', '/forgot-password'];

    authGuard = (to, _from, next) => {
        const meta = this.#metadata[to];
        const loggedIn = stateManager.isLoggedIn();
        if (meta?.requiresAuth && !loggedIn) return next('/login');
        if (!meta?.requiresAuth && loggedIn) return next('/home');
        return next();
    }

    #toRegex(pattern) {
        const escaped = pattern
            .replace(/:[^\s/]+/g, '([^/]+)')
            .replace(/\//g, '\\/');
        return new RegExp(`^${escaped}$`);
    }

    #matchRoute(pattern, path) {
        return this.#toRegex(pattern).test(path);
    }

    #extractParams(pattern, path) {
        const params = {};
        const pathParts = path.split('/');
        pattern.split('/').forEach((part, index) => {
            if (part.startsWith(':')) {
                params[part.slice(1)] = decodeURIComponent(pathParts[index] || '')
            }
        });
        return params;
    }

    getRoutes() {
        return {...this.#routes};
    }

    getMetadata() {
        return {...this.#metadata};
    }

    getPublicRoutes() {
        return [...this.#publicRoutes];
    }

    getRoute(path) {
        if (this.#routes[path]) {
            return {
                path,
                component: this.#routes[path],
                meta: this.#metadata[path] ?? {},
                params: {},
            };
        }

        for (const [routePath, component] of Object.entries(this.#routes)) {
            if (this.#matchRoute(routePath, path)) {
                return {
                    path: routePath,
                    component,
                    meta: this.#metadata[routePath] ?? {},
                    params: this.#extractParams(routePath, path),
                }
            }
        }

        return null;
    }

    getPageTitle(path) {
        const meta = this.#metadata[path];
        return meta ? `ECHO — ${meta.title}` : 'ECHO';
    }
}

export const routeRegistry = new RouteRegistry();