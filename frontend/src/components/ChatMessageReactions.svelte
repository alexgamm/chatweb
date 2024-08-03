<script>
  import { scale } from "svelte/transition";

  export let color;
  export let reactions;
  export let onReaction;
</script>

<div class="flex justify-end items-center gap-1 mt-2">
  {#each reactions as { reaction, count } (reaction)}
    <button
      class={`flex justify-center items-center bg-opacity-20 rounded-xl py-1 px-2 hover:scale-95 active:scale-90 transition ${color}`}
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
