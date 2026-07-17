// Small, dependency-free DOM helpers shared across controllers and views.

export const qs = (selector, scope = document) => scope.querySelector(selector);

export const qsa = (selector, scope = document) => [...scope.querySelectorAll(selector)];

export const createElementFromHTML = (htmlString) => {
    const template = document.createElement('template');
    template.innerHTML = htmlString.trim();
    return template.content.firstElementChild;
};

export const debounce = (fn, delay = 300) => {
    let timeoutId;
    return (...args) => {
        clearTimeout(timeoutId);
        timeoutId = setTimeout(() => fn(...args), delay);
    };
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

// The backend returns timestamps as "Date: 2026-07-16, Time: 01:23:45"
// instead of ISO-8601, so new Date(str) won't parse them directly.
// Converts to a real Date object, or null if the string doesn't match.
export const parseBackendDate = (str) => {
    const match = /Date:\s*([\d-]+),\s*Time:\s*([\d:]+)/.exec(str ?? '');
    if (!match) return null;
    const date = new Date(`${match[1]}T${match[2]}`);
    return Number.isNaN(date.getTime()) ? null : date;
};

// Reverse of parseBackendDate — formats a JS Date into the backend's
// custom "Date: YYYY-MM-DD, Time: HH:MM:SS" string, needed when polling
// with a `since` cursor.
export const formatBackendDate = (date) => {
    const pad = (n) => String(n).padStart(2, '0');
    const datePart = `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
    const timePart = `${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
    return `Date: ${datePart}, Time: ${timePart}`;
};