<script>
  import EditIcon from "./icons/EditIcon.svelte";

  const REACTIONS = ["❤️", "🚑", "🤔", "😂", "🤬"];
  export let show;
  export let canEdit;
  export let canDelete;
  export let position;
  export let onReply;
  export let onEdit;
  export let onDelete;
  export let onReaction;
</script>

<div
  class={`bg-neutral absolute px-2 rounded-2xl ${!show ? "hidden" : ""}`}
  style={`left: ${position.x}px; top: ${position.y}px`}
>
  <button
    class="btn p-2 bg-transparent hover:opacity-70 border-none hover:bg-transparent normal-case text-gray-300 text-base z-10"
    on:click={onReply}
  >
    <svg
      class="h-5 w-5 text-gray-300"
      fill="none"
      viewBox="0 0 24 24"
      stroke="currentColor"
    >
      <path
        stroke-linecap="round"
        stroke-linejoin="round"
        stroke-width="2"
        d="M3 10h10a8 8 0 018 8v2M3 10l6 6m-6-6l6-6"
      />
    </svg>
  </button>
  {#if canEdit}
    <button
      class="btn p-2 bg-transparent hover:opacity-70 border-none hover:bg-transparent normal-case text-gray-300 text-base z-10"
      on:click={onEdit}
    >
      <EditIcon />
    </button>
  {/if}
  {#if canDelete}
    <button
      class="btn p-2 bg-transparent hover:opacity-70 border-none hover:bg-transparent normal-case text-gray-300 text-base z-10"
      on:click={onDelete}
    >
      <svg
        class="h-5 w-5 text-gray-300"
        fill="none"
        viewBox="0 0 24 24"
        stroke-width="1.5"
        stroke="currentColor"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          d="M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0"
        />
      </svg>
    </button>
  {/if}
  {#each REACTIONS as reaction}
    <button
      class="btn px-2 bg-transparent hover:opacity-70 border-none hover:bg-transparent normal-case text-gray-300 text-xl z-10"
      on:click={() => onReaction(reaction)}
    >
      {reaction}
    </button>
  {/each}
</div>
