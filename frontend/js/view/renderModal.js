import {qs, createElementFromHTML} from "../util/domHelpers.js";

let modalElement = null;

const removeModal = () => {
    modalElement?.remove();
    modalElement = null;
};

export const closeModal = () => {
    removeModal();
};

export const showModal = ({
                              title, bodyHTML = "", confirmLabel = "Confirm", cancelLabel = "Cancel",
                              danger = false, onConfirm,
                              onCancel,
                          } = {}) => {
    modalElement = createElementFromHTML(`
        <div class="modal-overlay">
            <div class="modal">
                <div class="modal-header"><h2></h2></div>
                <div class="modal-body"></div>
                <div class="modal-actions">
                    <button type="button" class="btn cancel-btn"></button>
                    <button type="button" class="btn ${danger ? 'btn-danger' : 'btn-primary'} confirm-btn"></button>
                </div>
            </div>
        </div>
    `);
    qs(".modal-header h2", modalElement).textContent = title ?? "";
    qs(".modal-body", modalElement).innerHTML = bodyHTML;
    qs(".cancel-btn", modalElement).textContent = cancelLabel;
    qs(".confirm-btn", modalElement).textContent = confirmLabel;
    qs(".confirm-btn", modalElement).addEventListener("click", async () => {
        await onConfirm?.();
        removeModal();
    });
    qs(".cancel-btn", modalElement).addEventListener("click", () => {
        onCancel?.();
        removeModal();
    });
    modalElement.addEventListener("click",()=>{

    });
    modalElement.addEventListener('click', (e) => {
        if (e.target === modalElement) {
            onCancel?.();
            removeModal();
        }
    });

    document.body.appendChild(modalElement);
};