import {qs, qsa} from '../util/domHelpers.js';
import {apiClient} from '../model/apiClient.js';
import {getUser, getUserId, setUser, getTheme, setTheme} from '../model/state.js';
import {showToast} from '../view/renderToast.js';
import {logout} from './authController.js';

export const init = () => {
    initTheme();
    initForm();
    initLeaveDelete();
}

const initTheme = () => {
    const themeButtons = qsa(".themes .item");
    const applyTheme = (themeName) => {
        themeButtons.forEach((button) => {
            button.classList.toggle("active", button.dataset.theme === themeName);
        });
        document.documentElement.setAttribute("data-theme", themeName);
        setTheme(themeName);
    };
    themeButtons.forEach((button) => {
        button.addEventListener("click", () => {
            applyTheme(button.dataset.theme);
        });
    });
    applyTheme(getTheme() ?? "dark");
};

const initForm = () => {
    const form = qs('.account-data.personal .form');
    const usernameInput = qs('#username', form);
    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const userId = getUserId();
        if (!userId) return;
        try {
            const updated = await apiClient.updateProfile(userId, {
                username: usernameInput.value.trim(),
            });
            setUser({...getUser(), username: updated.username, profileImagePath: updated.profileImagePath});
            showToast('Profile updated', 'success');
        } catch {
            showToast('Could not update profile', 'error');
        }
    });
};

const initLeaveDelete = () => {
    const leaveButton = qs(".account-data.leave-delete .btn-warning");
    const deleteButton = qs(".account-data.leave-delete .btn-danger");

    leaveButton.addEventListener("click", async () => {
        await logout();
        window.location.href = "login.html";
    });
    deleteButton.addEventListener("click", () => {
        showToast("not supported yet", "error");
    });
};