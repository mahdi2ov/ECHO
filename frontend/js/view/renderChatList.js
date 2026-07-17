import {createElementFromHTML, qs, parseBackendDate} from "../util/domHelpers.js";

export const renderChatList = (conversations, activeConversationId) => {
    const fragment = document.createDocumentFragment();
    conversations.forEach((conversation) => {
        const isActive = conversation.id === activeConversationId;
        const item = createElementFromHTML(`
            <div class="item${isActive ? ' active' : ''}" data-conversation-id="${conversation.id}">
                <a href="#">
                    <div class="profile"><img alt=""></div>
                    <div class="text">
                        <div class="title"><h1></h1></div>
                        <div class="last-message"><p></p></div>
                    </div>
                    <div class="time"><p></p></div>
                </a>
            </div>
        `);
        qs(".profile img", item).src = conversation.profileImagePath || "../assets/profile/default.svg";
        qs(".title h1", item).textContent = conversation.title ?? 'Unknown';
        qs(".last-message p", item).textContent = ''; // no last-message preview field in the contract yet
        const date = parseBackendDate(conversation.lastMessageAt);
        qs(".time p", item).textContent = date ? date.toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'}) : '';
        fragment.appendChild(item);
    });
    return fragment;
};