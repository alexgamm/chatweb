<script>
  import { onMount } from "svelte";
  import roomPasswordModal from "../../stores/room-password-modal";
  import wait from "../../utils/wait";
  import Modal from "./Modal.svelte";

  export let onSubmit;

  let state = "idle";
  let password = "";
  let errorMessage = "";

  $: canSubmit = state === "idle" && !!password;

  const submit = async () => {
    if (!canSubmit) {
      return;
    }
    state = "pending";
    try {
      await onSubmit(password);
    } catch (error) {
      errorMessage = error.message;
      state = "idle";
      return;
    }
    state = "success";
    $roomPasswordModal.close();
    await wait(300);
    state = "idle";
  };

  onMount(() => {
    $roomPasswordModal.addEventListener("cancel", (e) => e.preventDefault());
  });
</script>

<Modal
  modal={roomPasswordModal}
  onClose={() => {
    password = "";
  }}
>
  <div class="relative rounded-lg shadow bg-gray-700 modal-box w-80">
    <div class="text-center">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        fill="none"
        viewBox="0 0 24 24"
        stroke-width="1.5"
        stroke="currentColor"
        class="mx-auto mb-4 stroke-violet-400 w-14 h-14"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          d="M16.5 10.5V6.75a4.5 4.5 0 10-9 0v3.75m-.75 11.25h10.5a2.25 2.25 0 002.25-2.25v-6.75a2.25 2.25 0 00-2.25-2.25H6.75a2.25 2.25 0 00-2.25 2.25v6.75a2.25 2.25 0 002.25 2.25z"
        />
      </svg>
      <h3 class="mb-5 text-lg font-normal text-gray-400">
        Enter password to join the room
      </h3>
      <form on:submit|preventDefault={submit}>
        <label class="label" for="assword">
          <span class="label-text">password</span>
        </label>
        <input
          id="password"
          class="form-control block h-10 w-full input input-bordered"
          type="password"
          on:input={() => (errorMessage = "")}
          bind:value={password}
        />
        <div class="form-control">
          <span class="text-red-500 mb-3 mt-1 h-4 text-sm">{errorMessage}</span>
          <button
            type="submit"
            class="flex justify-center items-center w-full mb-2 btn bg-violet-500 bg-opacity-60 border-transparent hover:bg-opacity-80
                          hover:bg-violet-500 hover:border-transparent text-gray-300 disabled:bg-violet-500 disabled:bg-opacity-40 disabled:text-gray-400"
            disabled={!canSubmit}
          >
            {#if state === "pending"}
              <span class="loading loading-spinner" />
            {/if}
            Join
          </button>
        </div>
      </form>
    </div>
  </div>
</Modal>
