<script>
  import useApi from "../../hooks/api";
  import changePasswordModal from "../../stores/change-password-modal";
  import wait from "../../utils/wait";
  import RepeatPasswordGroup from "../RepeatPasswordGroup.svelte";
  import Modal from "./Modal.svelte";

  const {
    authorized: { put },
  } = useApi();
  let state = "idle";
  let oldPassword = "";
  let newPassword = "";
  let repeatPassword = "";
  let errorMessage = "";
  let similarPasswords = false;

  $: errorMessage =
    repeatPassword && newPassword && !similarPasswords
      ? "passwords don't match"
      : "";

  $: canSubmit =
    state === "idle" &&
    !!oldPassword &&
    !!repeatPassword &&
    !!newPassword &&
    similarPasswords;

  const submit = async () => {
    if (!canSubmit) {
      return;
    }
    state = "pending";
    try {
      await put("/api/users/me/password", {
        oldPassword,
        newPassword,
      });
    } catch ({ message }) {
      errorMessage = message;
      state = "idle";
      return;
    }
    state = "success";
    await wait(3000);
    $changePasswordModal.close();
    await wait(300);
    state = "idle";
    oldPassword = "";
    newPassword = "";
  };
</script>

<Modal
  modal={changePasswordModal}
  onClose={() => {
    errorMessage = "";
    newPassword = "";
  }}
>
  <div class="relative rounded-lg shadow bg-violet-400 bg-opacity-20 modal-box w-80">
    <button
      type="button"
      class="absolute top-3 right-2.5 text-gray-400 bg-transparent
                    rounded-lg text-sm p-1.5 ml-auto inline-flex items-center
                    hover:bg-gray-800 hover:text-white"
      on:click={() => $changePasswordModal.close()}
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
          Password was changed successfully
        </h3>
      </div>
    {:else}
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
          Make up a new password
        </h3>
        <form on:submit|preventDefault={submit}>
          <label class="label" for="old-assword">
            <span class="label-text">old password</span>
          </label>
          <input
            id="old-password"
            class="form-control block h-10 w-full input input-bordered"
            type="password"
            on:input={() => (errorMessage = "")}
            bind:value={oldPassword}
          />
          <RepeatPasswordGroup
            error={errorMessage}
            bind:password={newPassword}
            bind:repeatPassword
            bind:similarPasswords
          />
          <div class="form-control">
            <span class="text-red-500 mb-3 mt-1 h-4 text-sm"
              >{errorMessage}</span
            >
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
