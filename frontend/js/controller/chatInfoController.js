// Chat Info panel logic: block/add-contact (private chats) and
// leave/add-member/edit/history (groups). Now that ChatInfoDTO includes
// otherUserId/groupId, block/add-contact/leave-group are fully wired.

import {qs} from '../util/domHelpers.js';
import {apiClient} from '../model/apiClient.js';
import {getUserId} from '../model/state.js';
import {showToast} from '../view/renderToast.js';
import {showModal} from '../view/renderModal.js';
import {renderChatInfo} from '../view/renderChatInfo.js';
import {leaveGroup} from './groupController.js';

// The last-loaded ChatInfoDTO: {conversationId, title, subtitle, type, otherUserId, groupId}
let current = null;

export const init = () => {
    const infoPanel = qs('.info');
    const menuToggle = qs('.menu-toggle', infoPanel);
    const actionsMenu = qs('.info-actions-menu', infoPanel);

    menuToggle.addEventListener('click', () => {
        actionsMenu.classList.toggle('open');
        menuToggle.classList.toggle('active');
    });

    actionsMenu.addEventListener('click', (e) => {
        const item = e.target.closest('.item[data-action]');
        if (!item) return;
        actionsMenu.classList.remove('open');
        menuToggle.classList.remove('active');
        handleAction(item.dataset.action);
    });
};

export const openConversationInfo = async (conversationId) => {
    const requesterId = getUserId();
    if (!requesterId) return;

    try {
        const info = await apiClient.getConversationInfo(conversationId, requesterId);
        current = info;
        renderChatInfo(info);
    } catch {
        showToast('Could not load chat info', 'error');
    }
};

const handleAction = (action) => {
    const userId = getUserId();
    if (!userId || !current) return;

    switch (action) {
        case 'block':
            runAction(() => apiClient.blockUser(userId, current.otherUserId), 'User blocked');
            break;

        case 'add-contact':
            runAction(() => apiClient.addContact(userId, current.otherUserId), 'Contact added');
            break;

        case 'leave-group':
            showModal({
                title: 'Leave group?',
                bodyHTML: '<p>You will no longer receive messages from this group.</p>',
                danger: true,
                confirmLabel: 'Leave',
                onConfirm: async () => {
                    const result = await leaveGroup(current.groupId);
                    result.success ? showToast('Left group', 'success') : showToast(result.error, 'error');
                },
            });
            break;

        // TODO: needs a "pick a contact" UI — wire once
        // newConversationController.js's contact list supports selection
        // for this purpose specifically.
        case 'add-member':
            showToast('Add member UI not built yet', 'error');
            break;

        // TODO: needs an edit form/modal for name/description/photo.
        case 'edit-group':
            showToast('Edit group UI not built yet', 'error');
            break;

        // NOTE: ConversationDTO has an `archived` flag but the contract
        // has no endpoint to actually set it — raise with your teammate.
        case 'archive':
            showToast('Archiving is not supported by the backend yet', 'error');
            break;

        // NOTE: editHistory exists per-message (MessageDTO) but there's no
        // endpoint to fetch deleted/edited history in bulk for a whole
        // conversation — raise with your teammate.
        case 'history':
            showToast('Message history is not supported by the backend yet', 'error');
            break;
    }
};

const runAction = async (fn, successMessage) => {
    try {
        await fn();
        showToast(successMessage, 'success');
    } catch {
        showToast('Action failed', 'error');
    }
};