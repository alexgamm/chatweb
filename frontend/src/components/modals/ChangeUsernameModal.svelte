<script>
  import useApi from "../../hooks/api";
  import changeUsernameModal from "../../stores/change-username-modal";
  import user, { getUser } from "../../stores/user";
  import wait from "../../utils/wait";
  import Modal from "./Modal.svelte";
  const {
    authorized: { put },
  } = useApi();
  let state = "idle";
  let newUsername = "";
  let errorMessage = "";
  $: canSubmit = !!newUsername && state === "idle";
  const submit = async () => {
    if (!canSubmit) {
      return;
    }
    state = "pending";
    try {
      await put("/api/users/me/username", {
        username: newUsername,
      });
    } catch (error) {
      errorMessage = error.message;
      state = "idle";
      return;
    }
    state = "success";
    await wait(3000);
    $changeUsernameModal.close();
    await wait(300);
    state = "idle";
    newUsername = "";
    getUser();
  };
</script>

<Modal
  modal={changeUsernameModal}
  onClose={() => {
    errorMessage = "";
    newUsername = "";
  }}
>
  <div class="relative rounded-lg shadow bg-gray-700 modal-box w-80">
    <button
      type="button"
      class="absolute top-3 right-2.5 text-gray-400 bg-transparent
                    rounded-lg text-sm p-1.5 ml-auto inline-flex items-center
                    hover:bg-gray-800 hover:text-white"
      on:click={() => $changeUsernameModal.close()}
    >
      <svg
        aria-hidden="true"
        class="w-5 h-5"
        fill="currentColor"
        viewBox="0 0 20 20"
        xmlns="http://www.w3.org/2000/svg"
      >
        <path
          fill-rule="evenodd"
          d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
          clip-rule="evenodd"
        />
      </svg>
    </button>
    {#if state === "success"}
      <div class="p-6 text-center">
        <svg
          class="mx-auto mb-4 fill-violet-400 text-gray-400 w-14 h-14 dark:text-gray-200"
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 24 24"
        >
          <path
            d="M12 2C6.5 2 2 6.5 2 12S6.5 22 12 22 22 17.5 22 12 17.5 2 12 2M12 20C7.59 20 4 16.41 4 12S7.59 4 12 4 20 7.59 20 12 16.41 20 12 20M16.59 7.58L10 14.17L7.41 11.59L6 13L10 17L18 9L16.59 7.58Z"
          />
        </svg>
        <h3 class="mb-5 text-lg font-normal text-gray-400">
          Username was changed successfully
        </h3>
      </div>
    {:else}
      <div class="text-center">
        <svg
          aria-hidden="false"
          class="mx-auto mb-4 fill-violet-400 text-gray-400 w-14 h-14 dark:text-gray-200"
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 24 24"
        >
          <path
            d="M20,11H23V13H20V11M1,11H4V13H1V11M13,1V4H11V1H13M4.92,3.5L7.05,5.64L5.63,7.05L3.5,4.93L4.92,3.5M16.95,5.63L19.07,3.5L20.5,4.93L18.37,7.05L16.95,5.63M12,6A6,6 0 0,1 18,12C18,14.22 16.79,16.16 15,17.2V19A1,1 0 0,1 14,20H10A1,1 0 0,1 9,19V17.2C7.21,16.16 6,14.22 6,12A6,6 0 0,1 12,6M14,21V22A1,1 0 0,1 13,23H11A1,1 0 0,1 10,22V21H14M11,18H13V15.87C14.73,15.43 16,13.86 16,12A4,4 0 0,0 12,8A4,4 0 0,0 8,12C8,13.86 9.27,15.43 11,15.87V18Z"
          />
        </svg>
        <h3 class="mb-5 text-lg font-normal text-gray-400">
          Make up a new name
        </h3>
        <form on:submit|preventDefault={submit}>
          <input
            class="form-control block h-10 mb-4 w-full input input-bordered"
            type="text"
            on:input={() => (errorMessage = "")}
            bind:value={newUsername}
            placeholder={$user?.username ?? ""}
          />
          <div class="form-control">
            <span class="text-red-500 mb-3">{errorMessage}</span>
            <button
              type="submit"
              class="flex justify-center items-center w-full mb-2 btn bg-violet-500 bg-opacity-60 border-transparent hover:bg-opacity-80
                          hover:bg-violet-500 hover:border-transparent text-gray-300 disabled:bg-violet-500 disabled:bg-opacity-40 disabled:text-gray-400"
              disabled={!canSubmit}
            >
              {#if state === "pending"}
                <span class="loading loading-spinner" />
              {/if}
              Submit
            </button>
          </div>
        </form>
      </div>
    {/if}
  </div>
  <form method="dialog" class="modal-backdrop">
    <button />
  </form>
</Modal>
