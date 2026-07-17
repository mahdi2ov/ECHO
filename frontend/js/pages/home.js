import {isLoggedIn, getUserId} from '../model/state.js';
import * as homeController from '../controller/homeController.js';
import * as settingsController from '../controller/settingsController.js';
import * as chatController from '../controller/chatController.js';
import * as chatInfoController from '../controller/chatInfoController.js';
import * as newConversationController from '../controller/newConversationController.js';

document.documentElement.setAttribute('data-theme', localStorage.getItem('theme') || 'dark');

document.addEventListener('DOMContentLoaded', () => {
    if (!isLoggedIn()) {
        window.location.href = 'login.html';
        return;
    }

    settingsController.init();
    chatController.init();
    chatInfoController.init();
    newConversationController.init();

    homeController.init({
        onSelectConversation: (conversationId, meta) => {
            chatController.openConversation(conversationId, meta);
        },
    });

});

window.addEventListener('beforeunload', () => {
    chatController.cleanup();
});