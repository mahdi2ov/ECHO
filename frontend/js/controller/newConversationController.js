import {qs} from '../util/domHelpers.js';
import {apiClient} from '../model/apiClient.js';
import {getUserId} from '../model/state.js';
import {showToast} from '../view/renderToast.js';
import {renderContactList} from '../view/renderContactList.js';
import {createGroup} from './groupController.js';

const notifyConversationsChanged = () => {
    document.dispatchEvent(new CustomEvent('app:conversations-changed'));
};

export const init = () => {
    const groupForm = qs('.new-conversation .create-group .form');
    const contactForm = qs('.new-conversation .add-contact .form');
    const contactsList = qs('.new-conversation .contacts-list');

    groupForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const nameInput = qs('#group-name', groupForm);
        const name = nameInput.value.trim();
        if (!name) return;

        const result = await createGroup(name);
        if (result.success) {
            showToast('Group created', 'success');
            nameInput.value = '';
            notifyConversationsChanged();
        } else {
            showToast(result.error, 'error');
        }
    });

    contactForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const idInput = qs('#contact-id', contactForm);
        const contactId = idInput.value.trim();
        if (!contactId) return;

        const userId = getUserId();
        if (!userId) return;

        try {
            await apiClient.addContact(userId, contactId);
            showToast('Contact added', 'success');
            idInput.value = '';
            loadContacts();
        } catch {
            showToast('Could not add contact — check the ID', 'error');
        }
    });
    contactsList.addEventListener('click', (e) => {
        const item = e.target.closest('.item[data-user-id]');
        if (!item) return;
        showToast('Refreshing your conversation list…', 'success');
        notifyConversationsChanged();
    });

    loadContacts();
};

const loadContacts = async () => {
    const userId = getUserId();
    if (!userId) return;

    try {
        const contacts = await apiClient.listContacts(userId);
        const contactsList = qs('.new-conversation .contacts-list');
        contactsList.innerHTML = '';
        contactsList.appendChild(renderContactList(contacts));
    } catch {
        showToast('Could not load contacts', 'error');
    }
};