<script>
  import { navigate } from "svelte-routing";

  export let title;
  let password = "";
  let state = "idle";
  $: canSubmit = state === "idle";

  function handleSubmit() {
    navigate(`/room/codenames-test`);
  }
</script>

<div class="rounded-xl bg-neutral-focus p-3">
  <h2 class="text-lg font-bold mb-2">{title}</h2>
  <form on:submit|preventDefault={handleSubmit}>
    <div class="form-control">
      <label class="label" for="password">
        <span class="label-text text-xs">password (optional)</span>
      </label>
      <input
        class="input input-bordered input-sm"
        type="text"
        id="password"
        placeholder="leave blank to make a public game"
        bind:value={password}
      />
    </div>
    <div class="form-control mt-6">
      <button
        type="submit"
        class="btn bg-violet-500 bg-opacity-60 border-transparent hover:bg-opacity-80 hover:bg-violet-500 hover:border-transparent mb-2 w-full text-gray-300"
        disabled={!canSubmit}
      >
        {#if state === "pending"}
          <span class="loading loading-spinner" />
        {/if}
        Play
      </button>
    </div>
  </form>
</div>
