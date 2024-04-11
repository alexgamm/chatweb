<script>
  import toasts from "../stores/toast";

  const CLASS_NAMES = {
    error: "alert-error",
    success: "alert-success",
  };
</script>

<div class="toast toast-top toast-end">
  {#each $toasts as toast}
    <div class={`alert gap-2 ${CLASS_NAMES[toast.type]}`}>
      <svg
        xmlns="http://www.w3.org/2000/svg"
        fill="none"
        viewBox="0 0 24 24"
        stroke-width="1.5"
        stroke="currentColor"
        class="w-7 h-7"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          d="M12 9v3.75m9-.75a9 9 0 1 1-18 0 9 9 0 0 1 18 0Zm-9 3.75h.008v.008H12v-.008Z"
        />
      </svg>
      <span>{toast.message}</span>
    </div>
  {/each}
</div>
