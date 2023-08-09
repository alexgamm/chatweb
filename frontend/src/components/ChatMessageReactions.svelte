<script>
  import { scale } from "svelte/transition";

  export let reactions;
  export let onReaction;
</script>

<div class="flex justify-start items-center gap-1 mt-2">
  {#each reactions as { reaction, count } (reaction)}
    <button
      class="flex justify-center items-center bg-black bg-opacity-20 rounded-xl py-1 px-2 hover:scale-95 active:scale-90 transition"
      transition:scale
      on:click={() => onReaction(reaction)}
    >
      <span>{reaction}</span>
      {#if count > 1}
        <span class="text-xs text-gray-200 ml-1">{count}</span>
      {/if}
    </button>
  {/each}
</div>
