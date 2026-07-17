// Transient success/error/warning toast notifications, appended to a shared

import {createElementFromHTML, qs} from "../util/domHelpers.js";

let containerEl = null;
const getContainer = () => {
    if (containerEl) return containerEl;
    containerEl = createElementFromHTML(`<div class="toast-container"></div>`);
    document.body.appendChild(containerEl);
    return containerEl;
};

export const showToast = (message, type = "error", durationMs = 4000) => {
    const toast = createElementFromHTML(`
        <div class="toast toast-${type}" role="status">
            <span class="toast-message"></span>
        </div>
    `);
    qs('.toast-message', toast).textContent = message;
    getContainer().appendChild(toast);
    requestAnimationFrame(() => toast.classList.add('show'));
    setTimeout(() => {
        toast.classList.remove('show');
        toast.addEventListener('transitionend', () => toast.remove(), {once: true});
        setTimeout(() => toast.remove(), 300);
    }, durationMs);
};