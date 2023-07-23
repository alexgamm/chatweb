<script>
  export let message;
  export let onContextMenu;
</script>

<div class="chat chat-start" bind:clientHeight={message.elemHeight}>
  <div class="chat-header font-bold mb-1 break-all">{message.username}</div>
  <div
    class={`chat-bubble w-auto max-w-4xl text-gray-200 break-all ${message.color}`}
    on:contextmenu|preventDefault={onContextMenu}
  >
    {#if message.repliedMessage}
      <div
        class="flex gap-2 w-auto bg-black bg-opacity-25 rounded-lg p-4 text-sm text-gray-300 mb-1"
      >
        <i
          class="block h-100 bg-white bg-opacity-20 rounded-md w-1 mt-1 min-w-[4px]"
        />
        <div>
          <div class="font-semibold mb-1 break-all">
            {message.repliedMessage.username}
          </div>
          <div class="break-all">{message.repliedMessage.message}</div>
        </div>
      </div>
    {/if}
    {message.message}
  </div>
</div>
