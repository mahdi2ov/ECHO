import {stateManager} from "../state/StateManager.js";
import {routeRegistry} from "./RouteRegistry.js";

class Router {
    #currentComponent = null;
    #currentRoute = null;
    #currentParams = {};

    #middlewares = [];
    #guards = [];

    #app = null;

    constructor() {
        this.#app = document.getElementById('app');
        this.#guards = [routeRegistry.authGuard];
        this.#init();
    }

    #init() {
        window.addEventListener('hashchange', () => {
            this.#handleRoute(this.#getCurrentPath());
        });

        window.addEventListener('error', (e) => {
            console.error('[Router] Unhandled error:', e.error);
        });

        console.log('[Router] Initialized');
    }

    navigate(path, params = {}) {
        if (!path) path = '/login';
        if (!path.startsWith('/')) path = '/' + path;

        let finalPath = path;
        for (const middleware of this.#middlewares) {
            const result = middleware(finalPath);
            if (result === false) return;
            if (typeof result === 'string') finalPath = result;
        }

        this.#runGuards(finalPath, (resolvedPath) => {
            if (!resolvedPath) return;
            const query = this.#buildQuery(params);
            const newHash = '#' + resolvedPath + query;

            if (window.location.hash === newHash) {
                this.#handleRoute(resolvedPath);
            } else {
                window.location.hash = newHash;
            }
        });
    }

    start() {
        this.#handleRoute(this.#getCurrentPath());
    }

    addMiddleware(middleware) {
        this.#middlewares.push(middleware);
        return this;
    }

    addGuard(guard) {
        this.#guards.push(guard);
        return this;
    }

    back() {
        window.history.back();
    }

    forward() {
        window.history.forward();
    }

    refresh() {
        this.#handleRoute(this.#getCurrentPath());
    }

    isActive(path) {
        return path === this.#currentRoute;
    }

    getParams() {
        return {
            ...this.#currentParams
        };
    }

    getCurrentRoute() {
        return {
            path: this.#currentRoute,
            params: this.#currentParams,
            component: this.#currentComponent,
        };
    }

    #getCurrentPath() {
        const hash = window.location.hash;
        if (!hash || hash === '#') return '/login';
        return hash.replace('#', '').split('?')[0];
    }

    #getCurrentQueryParams() {
        const hash = window.location.hash;
        if (!hash.includes('?')) return {};
        const params = {};
        hash.split('?')[1].split('&').forEach(pair => {
            const [key, value] = pair.split('=');
            if (key) params[decodeURIComponent(key)] = decodeURIComponent(value || '');
        });
        return params;
    }

    #buildQuery(params) {
        const keys = Object.keys(params);
        if (keys.length === 0) return '';
        return '?' + keys
            .map(k => `${encodeURIComponent(k)}=${encodeURIComponent(params[k])}`)
            .join('&');
    }

    #runGuards(path, callback) {
        if (this.#guards.length === 0) {
            callback(path);
            return;
        }

        const from = this.#currentRoute || '/login';

        const run = (guardIndex, currentPath) => {
            if (guardIndex >= this.#guards.length) {
                callback(currentPath);
                return;
            }

            this.#guards[guardIndex](currentPath, from, (result) => {
                if (result === false) {
                    callback(false);
                } else if (typeof result === 'string') {
                    run(0, result);
                } else {
                    run(guardIndex + 1, currentPath);
                }
            });
        };
        run(0, path);
    }

    #handleRoute(path) {
        try {
            const queryParams = this.#getCurrentQueryParams();
            const route = routeRegistry.getRoute(path);

            if (!route) {
                this.#handleNotFound();
                return;
            }

            this.#currentRoute = route.path;
            this.#currentParams = {...queryParams, ...(route.params || {})};

            stateManager.updateState({
                currentRoute: this.#currentRoute,
                routeParams: this.#currentParams,
            });

            document.title = routeRegistry.getPageTitle(route.path);
            this.#renderComponent(route.component, this.#currentParams);
            this.#emitRouteChange(route.path, this.#currentParams);
            window.scrollTo(0, 0);
        } catch (error) {
            console.error('[Router] Route handling error:', error);
            this.#handleError(error);
        }
    }

    #renderComponent(Component, params) {
        this.#app.innerHTML = '';
        try {
            const instance = new Component(params);

            if (instance instanceof Node) {
                this.#app.appendChild(instance);
                this.#currentComponent = instance;
            } else if (instance && typeof instance.render === 'function') {
                const result = instance.render();
                if (result instanceof Node) {
                    this.#app.appendChild(result);
                } else if (typeof result === 'string') {
                    this.#app.innerHTML = result;
                }
                this.#currentComponent = instance;
            } else {
                throw new Error(`Invalid component: ${Component.name} must return a Node or implement render()`);
            }
        } catch (error) {
            console.error('[Router] Render error:', error);
            this.#app.innerHTML = `
                <div class="error-page">
                    <h2>خطا در بارگذاری صفحه</h2>
                    <p>${error.message}</p>
                    <button onclick="window.router.navigate('/home')">بازگشت به خانه</button>
                </div>
            `;
        }
    }

    #handleNotFound() {
        console.warn('[Router] Route not found — redirecting to login');
        this.navigate('/login');
    }

    #handleError(error) {
        console.error('[Router] Error:', error);
    }

    #emitRouteChange(path, params) {
        window.dispatchEvent(new CustomEvent('routeChange', {
            detail: {path, params},
            bubbles: true,
        }));
    }
}

export const router = new Router();
window.router = router;