<script>
  import { onMount } from "svelte";
  import { navigate } from "svelte-routing";
  import useApi from "../../../hooks/api";
  import { pushToast } from "../../../stores/toast";

  export let title;

  let dictionaries = [];
  let dictionaryId;

  const {
    authorized: { get, post },
  } = useApi();

  let password = "";
  let state = "idle";
  $: canSubmit = state === "idle";

  const submit = async () => {
    state = "pending";
    // TODO select dictionary
    let game;
    try {
      game = await post(`/api/codenames/game`, {
        dictionaryId,
        password,
      });
    } catch (error) {
      console.error("could not create game", error);
      return;
    } finally {
      state = "idle";
    }
    navigate(`/room/${game.id}`);
  };

  onMount(async () => {
    try {
      dictionaries = await get(`/api/codenames/dictionaries`).then(
        ({ dictionaries }) => dictionaries
      );
      dictionaryId = dictionaries[0]?.id;
    } catch (error) {
      pushToast({ type: "error", message: error.message });
    }
  });
</script>

<div class="rounded-xl bg-neutral-focus p-3">
  <h2 class="text-lg font-bold mb-2">{title}</h2>
  <form on:submit|preventDefault={submit}>
    <div class="form-control">
      <select
        class="select select-bordered select-sm w-full max-w-xs"
        on:change={({ target: { value } }) => (dictionaryId = value)}
      >
        {#each dictionaries as dictionary (dictionary.id)}
          <option
            value={dictionary.id}
            selected={dictionary.id === dictionaryId}>{dictionary.name}</option
          >
        {/each}
      </select>
    </div>
    <div class="form-control">
      <label class="label" for="password">
        <span class="label-text text-xs">password (optional)</span>
      </label>
      <input
        class="input input-bordered input-sm"
        type="password"
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
