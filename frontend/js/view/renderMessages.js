// Pure render functions for message bubbles (list + single-append),
// matching the "bubble me/other" markup in home.html. Adds an actions row
// (edit/delete for own messages, report for others') that wasn't in the
// original static markup.

import {createElementFromHTML, qs, parseBackendDate} from "../util/domHelpers.js";

const LIKE_ICON = '<svg xmlns="http://www.w3.org/2000/svg" height="14px" viewBox="0 -960 960 960" width="14px"><path d="M720-144H264v-480l288-288 32 22q17 12 26 30.5t5 38.5l-1 5-38 192h264q30 0 51 21t21 51v57q0 8-1.5 14.5T906-467L786.93-187.8Q778-168 760-156t-40 12Zm-384-72h384l120-279v-57H488l49-243-201 201v378Zm0-378v378-378Zm-72-30v72H120v336h144v72H48v-480h216Z"/></svg>';
const DISLIKE_ICON = '<svg xmlns="http://www.w3.org/2000/svg" height="14px" viewBox="0 -960 960 960" width="14px" fill="#e3e3e3"><path d="M240-816h456v480L408-48l-32-22q-17-12-26-30.5t-5-38.5l1-5 38-192H120q-30 0-51-21t-21-51v-57q0-8 1.5-14.5T54-493l119-279q8-20 26.5-32t40.5-12Zm384 72H240L120-465v57h352l-49 243 201-201v-378Zm0 378v-378 378Zm72 30v-72h144v-336H696v-72h216v480H696Z"/></svg>';

const formatTime = (backendDateString) => {
    const date = parseBackendDate(backendDateString);
    return date ? date.toLocaleTimeString([], {hour: "2-digit", minute: "2-digit"}) : '';
};

// NOTE: the contract's Reaction object stores the LIKE/DISLIKE value under
// a field called `emoji` (not `reactionType`).
const countReactions = (reactions = [], type) => {
    return reactions.filter((reaction) => reaction.emoji === type).length;
};

const hasOwnReaction = (reactions = [], userId, type) => {
    return reactions.some((reaction) => reaction.emoji === type && reaction.userId === userId);
};

const buildBubble = (message, currentUserId) => {
    const isMine = message.senderId === currentUserId;
    const likeCount = countReactions(message.reactions, "LIKE");
    const dislikeCount = countReactions(message.reactions, "DISLIKE");
    const likedByMe = hasOwnReaction(message.reactions, currentUserId, "LIKE");
    const dislikedByMe = hasOwnReaction(message.reactions, currentUserId, "DISLIKE");

    // Own messages get edit/delete; other people's messages get report.
    // Not shown at all for already-deleted messages.
    const actionsHTML = message.deleted
        ? ''
        : isMine
            ? `<div class="message-actions">
                 <button type="button" class="edit-btn" title="Edit">✎</button>
                 <button type="button" class="delete-btn" title="Delete">🗑</button>
               </div>`
            : `<div class="message-actions">
                 <button type="button" class="report-btn" title="Report">⚑</button>
               </div>`;

    const bubble = createElementFromHTML(`
        <div class="bubble ${isMine ? 'me' : 'other'}" data-message-id="${message.id}">
            <p></p>
            ${actionsHTML}
            <div class="reactions">
                <div class="dislike reaction${dislikedByMe ? ' active' : ''}">
                    ${DISLIKE_ICON}
                    <p>${dislikeCount}</p>
                </div>
                <div class="like reaction${likedByMe ? ' active' : ''}">
                    ${LIKE_ICON}
                    <p>${likeCount}</p>
                </div>
            </div>
            <div class="time"><p></p></div>
        </div>
    `);
    qs("p", bubble).textContent = message.deleted ? "(this message was deleted)" : (message.content ?? "");
    qs(".time p", bubble).textContent = formatTime(message.createdAt) + (message.edited ? ' (edited)' : '');
    return bubble;
};

export const renderMessages = (messages, currentUserId) => {
    const fragment = document.createDocumentFragment();
    messages.forEach((message) => {
        fragment.appendChild(buildBubble(message, currentUserId));
    });
    return fragment;
};

export const renderSingleMessage = (message, currentUserId) => {
    return buildBubble(message, currentUserId);
};