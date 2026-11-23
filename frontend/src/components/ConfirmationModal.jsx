import React, { useEffect, useRef } from "react";

import Button from "./Button.jsx";

export default function ConfirmationModal({
  isOpen,
  title,
  message,
  confirmLabel = "Confirm",
  isBusy = false,
  onConfirm,
  onCancel,
}) {
  const dialogRef = useRef(null);

  useEffect(() => {
    const dialog = dialogRef.current;
    if (!dialog) {
      return;
    }

    if (isOpen && !dialog.open) {
      dialog.showModal();
    } else if (!isOpen && dialog.open) {
      dialog.close();
    }
  }, [isOpen]);

  return (
    <dialog
      ref={dialogRef}
      className="confirmation-dialog"
      aria-labelledby="confirmation-title"
      aria-describedby="confirmation-message"
      onCancel={(event) => {
        event.preventDefault();
        if (!isBusy) {
          onCancel();
        }
      }}
    >
      <div className="confirmation-content">
        <span className="confirmation-icon" aria-hidden="true">!</span>
        <div>
          <h2 id="confirmation-title">{title}</h2>
          <p id="confirmation-message">{message}</p>
        </div>
      </div>
      <div className="confirmation-actions">
        <Button onClick={onCancel} disabled={isBusy}>Cancel</Button>
        <Button variant="delete" onClick={onConfirm} disabled={isBusy}>
          {isBusy ? "Deleting…" : confirmLabel}
        </Button>
      </div>
    </dialog>
  );
}
