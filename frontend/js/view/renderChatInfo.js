import {qs} from '../util/domHelpers.js';

export const renderChatInfo = (info) => {
    const infoPanel = qs('.info');
    const isGroup = info.type === 'GROUP';

    qs('.profile img', infoPanel).src = '../assets/profile/default.svg';
    qs('.name h1', infoPanel).textContent = info.title ?? 'Unknown';
    qs('.subscribers', infoPanel).textContent = info.subtitle ?? '';

    const idRow = qs('.infos .item.id .title p', infoPanel);
    if (idRow) idRow.textContent = '';
    const bioRow = qs('.infos .item.bio .title p', infoPanel);
    if (bioRow) bioRow.textContent = '';

    qs('.group-actions', infoPanel).classList.toggle('active', isGroup);
    qs('.private-actions', infoPanel).classList.toggle('active', !isGroup);
};
