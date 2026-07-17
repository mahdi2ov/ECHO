import {qs, formatBackendDate} from "../util/domHelpers.js";
import {apiClient} from "../model/apiClient.js";
import {getUserId} from "../model/state.js";
import {showToast} from "../view/renderToast.js";
import {showModal} from "../view/renderModal.js";
import {renderMessages, renderSingleMessage} from "../view/renderMessages.js";
import {maxLength} from "../util/validators.js";

let currentConversationId = null;

const SEND_LIMIT = 5;
const SEND_WINDOW_MS = 1000;
const recentSendTimestamps = [];

const MAX_MESSAGE_LENGTH = 2000;

const POLL_INTERVAL_MS = 4000;
let pollIntervalId = null;
let lastSeenTimestamp = null;

const isRateLimited = () => {
    const now = Date.now();
    while (recentSendTimestamps.length && now - recentSendTimestamps[0] > SEND_WINDOW_MS) {
        recentSendTimestamps.shift();
    }
    return recentSendTimestamps.length >= SEND_LIMIT;
};

export const init = () => {
    const textarea = qs("#message");
    const sendButton = qs(".chat .new-message .send");
    const bubblesInner = qs(".bubbles-inner");
    const attachmentButton = qs(".chat .new-message .attachment");

    textarea.addEventListener("input", () => {
        textarea.style.height = 'auto';
        textarea.style.height = `${textarea.scrollHeight}px`;
    });

    textarea.addEventListener("keydown", (e) => {
        if (e.key === "Enter" && e.ctrlKey) {
            e.preventDefault();
            sendCurrentMessage(textarea);
        }
    });

    sendButton.addEventListener("click", () => {
        sendCurrentMessage(textarea);
    });

    attachmentButton.addEventListener("click", () => {
        showToast("Attachment picker isn't built yet", "error");
    });

    bubblesInner.addEventListener("click", (e) => {
        const bubble = e.target.closest(".bubble[data-message-id]");
        if (!bubble) return;
        const messageId = bubble.dataset.messageId;

        if (e.target.closest(".reaction")) {
            const reactionElement = e.target.closest(".reaction");
            const reactionType = reactionElement.classList.contains("like") ? "LIKE" : "DISLIKE";
            const isCurrentlyActive = reactionElement.classList.contains("active");
            handleReactionClick(messageId, reactionType, isCurrentlyActive);
            return;
        }

        if (e.target.closest(".edit-btn")) {
            const currentContent = qs("p", bubble).textContent;
            handleEditClick(messageId, currentContent);
            return;
        }

        if (e.target.closest(".delete-btn")) {
            handleDeleteClick(messageId);
            return;
        }

        if (e.target.closest(".report-btn")) {
            handleReportClick(messageId);
        }
    });
};

const sendCurrentMessage = async (textarea) => {
    const content = textarea.value.trim();
    if (!content || !currentConversationId) return;

    const lengthError = maxLength(content, MAX_MESSAGE_LENGTH, 'Message');
    if (lengthError) {
        showToast(lengthError, 'error');
        return;
    }

    if (isRateLimited()) {
        showToast("You are sending messages too fast", "warning");
        return;
    }
    const senderId = getUserId();
    if (!senderId) return;

    recentSendTimestamps.push(Date.now());

    try {
        const message = await apiClient.sendMessage(senderId, currentConversationId, content);
        appendMessage(message);
        textarea.value = "";
        textarea.style.height = "auto";
    } catch {
        showToast("Could not send message", "error");
    }
};

const handleReactionClick = async (messageId, reactionType, isCurrentlyActive) => {
    const userId = getUserId();
    if (!userId) return;

    try {
        const updated = isCurrentlyActive
            ? await apiClient.removeReaction(messageId, userId)
            : await apiClient.reactToMessage(messageId, userId, reactionType);
        replaceMessage(updated);
    } catch {
        showToast("Could not update reaction", "error");
    }
};

const handleEditClick = (messageId, currentContent) => {
    const userId = getUserId();
    if (!userId) return;

    showModal({
        title: 'Edit message',
        bodyHTML: '<textarea class="edit-message-input input-field" rows="3"></textarea>',
        confirmLabel: 'Save',
        onConfirm: async () => {
            const textarea = qs('.edit-message-input');
            const newContent = textarea.value.trim();
            if (!newContent) return;
            try {
                const updated = await apiClient.editMessage(messageId, userId, newContent);
                replaceMessage(updated);
            } catch {
                showToast('Could not edit message', 'error');
            }
        },
    });

    const textarea = qs('.edit-message-input');
    if (textarea) textarea.value = currentContent;
};

const handleDeleteClick = (messageId) => {
    const userId = getUserId();
    if (!userId) return;

    showModal({
        title: 'Delete message?',
        bodyHTML: '<p>This cannot be undone.</p>',
        danger: true,
        confirmLabel: 'Delete',
        onConfirm: async () => {
            try {
                await apiClient.deleteMessage(messageId, userId);
                markMessageDeleted(messageId);
            } catch {
                showToast('Could not delete message', 'error');
            }
        },
    });
};

const handleReportClick = (messageId) => {
    const userId = getUserId();
    if (!userId) return;

    showModal({
        title: 'Report message',
        bodyHTML: '<textarea class="report-reason-input input-field" rows="3" placeholder="Reason"></textarea>',
        confirmLabel: 'Report',
        danger: true,
        onConfirm: async () => {
            const textarea = qs('.report-reason-input');
            const reason = textarea.value.trim();
            if (!reason) return;
            try {
                await apiClient.reportMessage(messageId, userId, reason);
                showToast('Message reported', 'success');
            } catch {
                showToast('Could not report message', 'error');
            }
        },
    });
};

const markMessageDeleted = (messageId) => {
    const bubble = qs(`.bubble[data-message-id="${messageId}"]`);
    if (!bubble) return;
    qs('p', bubble).textContent = '(this message was deleted)';
    qs('.message-actions', bubble)?.remove();
};

const scrollToBottom = () => {
    const bubbles = qs(".bubbles");
    bubbles.scrollTop = bubbles.scrollHeight;
};

const appendMessage = (message) => {
    const bubblesInner = qs(".bubbles-inner");
    bubblesInner.appendChild(renderSingleMessage(message, getUserId()));
    scrollToBottom();
    if (message.createdAt) lastSeenTimestamp = message.createdAt;
};

const replaceMessage = (message) => {
    const existing = qs(`.bubble[data-message-id="${message.id}"]`);
    if (!existing) {
        appendMessage(message);
        return;
    }
    existing.replaceWith(renderSingleMessage(message, getUserId()));
};

const updateChatHeader = ({title, profileImagePath} = {}) => {
    const chatInfoHeader = qs('.chat .chat-info');
    const img = qs('.profile img', chatInfoHeader);
    const titleEl = qs('.title h1', chatInfoHeader);
    if (img) img.src = profileImagePath || '../assets/profile/default.svg';
    if (titleEl) titleEl.textContent = title ?? '';
};

const updateChatHeaderSubtitle = (subtitle) => {
    const chatInfoHeader = qs('.chat .chat-info');
    const subtitleEl = qs('.subscribers', chatInfoHeader);
    if (subtitleEl) subtitleEl.textContent = subtitle ?? '';
};

// --- polling ---

const stopPolling = () => {
    if (pollIntervalId) {
        clearInterval(pollIntervalId);
        pollIntervalId = null;
    }
};

const startPolling = () => {
    stopPolling();
    pollIntervalId = setInterval(pollForNewMessages, POLL_INTERVAL_MS);
};

const pollForNewMessages = async () => {
    if (!currentConversationId) return;
    try {
        const newMessages = await apiClient.getMessages(currentConversationId, lastSeenTimestamp);
        newMessages.forEach((message) => {
            // Skip messages we already have — either shown from the
            // initial load, or ones we just sent ourselves.
            if (qs(`.bubble[data-message-id="${message.id}"]`)) return;
            appendMessage(message);
        });
    } catch {
    }
};

export const cleanup = () => {
    stopPolling();
};

export const openConversation = async (conversationId, meta = {}) => {
    currentConversationId = conversationId;
    updateChatHeader(meta);
    stopPolling();

    const bubblesInner = qs(".bubbles-inner");
    const requesterId = getUserId();

    try {
        const [messages, info] = await Promise.all([
            apiClient.getMessages(conversationId),
            requesterId ? apiClient.getConversationInfo(conversationId, requesterId) : Promise.resolve(null),
        ]);
        bubblesInner.innerHTML = '';
        bubblesInner.appendChild(renderMessages(messages, getUserId()));
        scrollToBottom();
        if (info) updateChatHeaderSubtitle(info.subtitle);

        const latest = messages[messages.length - 1];
        lastSeenTimestamp = latest ? latest.createdAt : formatBackendDate(new Date());
        startPolling();
    } catch {
        showToast("Could not load messages", "error");
    }
};