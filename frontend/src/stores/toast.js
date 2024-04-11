import { writable } from "svelte/store";

const toasts = writable([]);
export function pushToast({ type, message }) {
  toasts.update((toasts) => [...toasts, { type, message }]);
  setTimeout(() => {
    toasts.update((toasts) => toasts.slice(1));
  }, 2000);
}

export default toasts;
