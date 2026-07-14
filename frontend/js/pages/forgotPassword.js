// Entry script for login.html — wires the form to authController.login()

import {requestPasswordReset} from '../controller/authController.js';
import {isLoggedIn} from '../model/state.js';
import {showToast} from '../view/renderToast.js';
import {qs} from "../util/domHelpers.js";

document.addEventListener("DOMContentLoaded", () => {
    if (isLoggedIn()) {
        window.location.href = "home.html";
        return;
    }
    const form = qs(".form");
    const usernameInput = document.getElementById('username');
    const submitButton = qs("button[type='submit']", form);
    const successMessage = qs(".form-success");
    const setLoading = (isLoading) => {
        submitButton.disabled = isLoading;
        submitButton.textContent = isLoading ? "Sending…" : "Password Recovery";
    };
    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        setLoading(true);
        successMessage.hidden = true;
        const result = await requestPasswordReset(usernameInput.value.trim());
        if (result.success) {
            successMessage.textContent = `Your temporary password is: ${result.tempPassword}  —  ` +
                "use it to log in, then change it from Settings.";
            successMessage.hidden = false;
            form.reset();
        } else {
            showToast(result.error, "error");
        }
        setLoading(false);
    });
});