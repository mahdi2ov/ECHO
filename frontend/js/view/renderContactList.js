import {createElementFromHTML, qs} from '../util/domHelpers.js';

export const renderContactList = (contacts) => {
    const fragment = document.createDocumentFragment();

    contacts.forEach((contact) => {
        const item = createElementFromHTML(`
            <div class="item" data-user-id="${contact.id}">
                <div class="profile"><img alt=""></div>
                <div class="text">
                    <div class="name"><h1></h1></div>
                    <div class="id"><p></p></div>
                </div>
            </div>
        `);
        qs('.profile img', item).src = contact.profileImagePath || '../assets/profile/default.svg';
        qs('.name h1', item).textContent = contact.username ?? 'Unknown';
        qs('.id p', item).textContent = `@${contact.username ?? ''}`;
        fragment.appendChild(item);
    });

    return fragment;
};