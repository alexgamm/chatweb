<script>
  import { Link } from "svelte-routing";
  import ChatMessageReactions from "./ChatMessageReactions.svelte";

  export let message;
  export let onContextMenu;
  export let onReaction;
</script>

<div class="chat chat-start" bind:this={message.elem}>
  <div class="chat-header font-bold mb-1 break-all">
    {message.username}
    <time class="text-xs font-normal opacity-50">
      {message.sendDateFormatted}
    </time>
  </div>
  <div
    class={`chat-bubble w-auto max-w-4xl text-gray-200 break-all bg-opacity-80 ${message.color} ${message.reactions?.length ? "mb-8" : ""}`}
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
    {#if message.buttons}
      <div class="flex gap-2 pt-2">
        {#each message.buttons as button}
          <Link
            class="btn btn-sm w-full max-w-xs bg-opacity-40 hover:bg-opacity-60 border-transparent hover:border-transparent"
            to={button.link}
          >
            {button.text}
          </Link>
        {/each}
      </div>
    {/if}
    {#if message.reactions?.length}
      <div class="-mt-10 translate-x-4 translate-y-11">
        <ChatMessageReactions color={message.color} reactions={message.reactions} {onReaction} />
      </div>
    {/if}
  </div>
</div>
