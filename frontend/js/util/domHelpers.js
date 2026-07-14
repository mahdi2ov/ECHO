// Small, framework-free DOM helpers shared across controllers and views.

export const qs = (selector, scope = document) => {
    return scope.querySelector(selector);
};

export const qsa = (selector, scope = document) => {
    return [...scope.querySelectorAll(selector)];
};

export const createElementFromHTML = (htmlString) => {
    const template = document.createElement('template');
    template.innerHTML = htmlString;
    return template.content.firstElementChild;
};

export const debounce = (fn, delay = 300) => {
    let timeoutId;
    return (...args) => {
        clearTimeout(timeoutId);
        timeoutId = setTimeout(() => fn(...args), delay);
    }
};

export const formatRelativeTime = (timestamp) => {
    const date = new Date(timestamp);
    const diffSec = Math.floor((Date.now() - date.getTime()) / 1000);
    if (diffSec < 60) return "just now";
    const diffMin = Math.floor(diffSec / 60);
    if (diffMin < 60) return `${diffMin}m ago`;
    const diffHour = Math.floor(diffMin / 60);
    if (diffHour < 24) return `${diffHour}h ago`;
    const diffDay = Math.floor(diffHour / 24);
    if (diffDay < 7) return `${diffDay}d ago`;
    return date.toLocaleDateString();
};