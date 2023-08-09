<script>
  export let modal;
  export let onClose;
</script>

<dialog class="modal" bind:this={$modal} on:close={onClose}>
  <slot />
</dialog>
