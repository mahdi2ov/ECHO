// Entry script for login.html — wires the form to authController.login()

import {login} from "../controller/authController.js";
import {isLoggedIn} from "../model/state.js";
import {showToast} from "../view/renderToast.js";
import {qs} from "../util/domHelpers.js";

document.addEventListener("DOMContentLoaded", () => {
    if (isLoggedIn()) {
        window.location.href = "home.html";
        return;
    }
    const form = qs(".form");
    const usernameInput = document.getElementById("username");
    const passwordInput = document.getElementById("password");
    const submitButton = qs("button[type='submit']", form);
    const setLoading = (isLoading) => {
        submitButton.disabled = isLoading;
        submitButton.textContent = isLoading ? "Logging in…" : "Login";
    };
    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        setLoading(true);
        const result = await login(usernameInput.value.trim(), passwordInput.value);
        if (result.success) {
            window.location.href = "home.html";
            return;
        }
        showToast(result.error, "error");
        setLoading(false);
    });
});
