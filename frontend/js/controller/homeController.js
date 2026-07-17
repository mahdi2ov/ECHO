import {qs, qsa} from '../util/domHelpers.js';
import {apiClient} from '../model/apiClient.js';
import {getUserId} from '../model/state.js';
import {showToast} from '../view/renderToast.js';
import {renderChatList} from '../view/renderChatList.js';
import * as chatInfoController from './chatInfoController.js';

let activeConversationId = null;
let conversationsById = new Map();

export const init = ({onSelectConversation} = {}) => {
    const menuButton = qs(".menu.button");
    const hamburgerMenu = qs(".hamburger-menu");
    const backSearchButton = qs(".header.chat .back.button");
    const searchInput = qs("#search");
    const searchResult = qs(".search-result");
    const chatList = qs(".chat-list");
    const newMessageButton = qs(".sidebar .new-message");
    const headerChatList = qs(".header.chat");

    const settingButton = qs(".sidebar .hamburger-menu .setting-button");
    const settingsPanel = qs(".sidebar .settings");
    const headerSetting = qs(".header.setting");
    const backSettingButton = qs(".header.setting .back.button");

    const newConversationPanel = qs(".sidebar .new-conversation");
    const headerNewConversation = qs(".header.new-conversation");
    const backNewConversationButton = qs(".header.new-conversation .back.button");

    const infoPanel = qs(".info");
    const closeInfoButton = qs(".info .close");
    const chatInfoHeader = qs(".chat .chat-info");
    const pageMain = qs(".page-main");

    const searchInChatButton = qs(".chat .chat-info .search");
    const chatInfoText = qs(".chat .chat-info .text");
    const chatInfoSearchInput = qs(".chat .chat-info .input-search");
    const closeInfoSearchButton = qs(".chat .chat-info .close");

    // closing the info panel on load
    infoPanel.classList.remove("open");
    pageMain.style.gridTemplateColumns = "360px auto";

    // hamburger menu
    menuButton.addEventListener("click", () => {
        hamburgerMenu.classList.toggle("open");
        menuButton.classList.toggle("active");
    });

    // sidebar search
    searchInput.addEventListener("focus", () => {
        searchResult.classList.add("open");
        chatList.classList.remove("open");
        newMessageButton.classList.remove("open");
        menuButton.classList.remove("open");
        backSearchButton.classList.add("open");
        hamburgerMenu.classList.remove("open");
        menuButton.classList.remove("active");
    });
    backSearchButton.addEventListener("click", () => {
        searchResult.classList.remove("open");
        chatList.classList.add("open");
        newMessageButton.classList.add("open");
        menuButton.classList.add("open");
        backSearchButton.classList.remove("open");
    });

    // settings panel
    settingButton.addEventListener("click", () => {
        settingsPanel.classList.add("open");
        chatList.classList.remove("open");
        headerChatList.classList.remove("open");
        headerSetting.classList.add("open");
        newMessageButton.classList.remove("open");
        hamburgerMenu.classList.remove("open");
    });
    backSettingButton.addEventListener("click", () => {
        settingsPanel.classList.remove("open");
        chatList.classList.add("open");
        headerChatList.classList.add("open");
        headerSetting.classList.remove("open");
        newMessageButton.classList.add("open");
        menuButton.classList.remove("active");
    });

    // new-conversation panel (same open/close pattern as settings)
    newMessageButton.addEventListener("click", () => {
        newConversationPanel.classList.add("open");
        chatList.classList.remove("open");
        headerChatList.classList.remove("open");
        headerNewConversation.classList.add("open");
        newMessageButton.classList.remove("open");
        hamburgerMenu.classList.remove("open");
    });
    backNewConversationButton.addEventListener("click", () => {
        newConversationPanel.classList.remove("open");
        chatList.classList.add("open");
        headerChatList.classList.add("open");
        headerNewConversation.classList.remove("open");
        newMessageButton.classList.add("open");
    });

    document.addEventListener("app:conversations-changed", () => {
        newConversationPanel.classList.remove("open");
        chatList.classList.add("open");
        headerChatList.classList.add("open");
        headerNewConversation.classList.remove("open");
        newMessageButton.classList.add("open");
        loadConversations();
    });

    // chat info panel
    chatInfoHeader.addEventListener("click", () => {
        infoPanel.classList.add("open");
        pageMain.style.gridTemplateColumns = "360px auto 360px";
        if (activeConversationId) {
            chatInfoController.openConversationInfo(activeConversationId);
        }
    });
    closeInfoButton.addEventListener("click", () => {
        infoPanel.classList.remove("open");
        pageMain.style.gridTemplateColumns = "360px auto";
    });

    // search in chat
    // click handler above and open the info panel at the same time
    searchInChatButton.addEventListener("click", (e) => {
        e.stopPropagation();
        searchInChatButton.classList.remove("open");
        chatInfoText.classList.remove("open");
        chatInfoSearchInput.classList.add("open");
    });
    closeInfoSearchButton.addEventListener("click", (e) => {
        e.stopPropagation();
        searchInChatButton.classList.add("open");
        chatInfoText.classList.add("open");
        chatInfoSearchInput.classList.remove("open");
    });

    // chat list item selection
    chatList.addEventListener("click", (e) => {
        const item = e.target.closest('.item[data-conversation-id]');
        if (!item) return;
        e.preventDefault();
        selectConversation(item.dataset.conversationId, onSelectConversation);
    });

    loadConversations();
};

const selectConversation = (conversationId, onSelectConversation) => {
    activeConversationId = conversationId;
    qsa(".chat-list .item[data-conversation-id]").forEach((element) => {
        element.classList.toggle("active", element.dataset.conversationId === conversationId);
    });
    const meta = conversationsById.get(conversationId);
    onSelectConversation?.(conversationId, meta);
};

const ACCENT_COLORS = [
    "4f5fe0", "6473ec", "e8940c", "12c281",
    "1c8fe0", "e02c78", "a44de8", "b15a6b",
    "e8bc0c", "e02a34", "16c4e0", "5a4de8",
];

const colorForConversation = (conversationId, opacity = 0.8) => {
    let hash = 0;
    for (let i = 0; i < conversationId.length; i++) {
        hash = (hash * 31 + conversationId.charCodeAt(i)) >>> 0;
    }
    const hex = ACCENT_COLORS[hash % ACCENT_COLORS.length];
    const r = parseInt(hex.slice(0, 2), 16);
    const g = parseInt(hex.slice(2, 4), 16);
    const b = parseInt(hex.slice(4, 6), 16);
    return `rgba(${r}, ${g}, ${b}, ${opacity})`;
};

const loadConversations = async () => {
    const userId = getUserId();
    if (!userId) return;
    try {
        const conversations = await apiClient.listConversations(userId);
        conversationsById = new Map(conversations.map((c) => [c.id, c]));

        const chatList = qs(".chat-list");
        qsa(".chat-list .item:not(.saved-message)").forEach((element) => element.remove());
        chatList.appendChild(renderChatList(conversations, activeConversationId));
        qsa(".chat-list .item[data-conversation-id]").forEach((item) => {
            qs(".profile", item).style.backgroundColor = colorForConversation(item.dataset.conversationId);
        });
    } catch {
        showToast("Could not load conversations", "error");
    }
};