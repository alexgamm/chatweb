<script>
  import useApi from "../../../hooks/api";
  import RepeatPasswordGroup from "../../RepeatPasswordGroup.svelte";

  const FAKE_PASSWORD = "************";

  const {
    authorized: { put },
  } = useApi();
  let state = "idle";
  let errorMessage = "";
  let oldPasswordInput;
  let oldPassword = FAKE_PASSWORD;
  let password;
  let repeatPassword;
  let similarPasswords;

  $: canSubmit = oldPassword && password && repeatPassword;

  const submit = async () => {
    if (state === "submitting" || !canSubmit) return;
    if (!similarPasswords) {
      errorMessage = "passwords don't match";
      return;
    }
    state = "submitting";
    try {
      await put("/api/users/me/password", {
        oldPassword,
        newPassword: password,
      });
    } catch ({ message }) {
      errorMessage = message;
      state = "edit";
      return;
    }
    state = "idle";
  };

  const reset = () => {
    oldPassword = FAKE_PASSWORD;
    password = "";
    repeatPassword = "";
    errorMessage = "";
    state = "idle";
  };
</script>

<form
  on:submit|preventDefault={submit}
  class="rounded-xl bg-neutral-focus pt-1 pb-3 px-3"
>
  <div class="form-control">
    <!-- svelte-ignore a11y-label-has-associated-control -->
    <label class="label">
      <span class="label-text">Your password</span>
    </label>
    <div class="relative">
      <input
        type="password"
        bind:this={oldPasswordInput}
        bind:value={oldPassword}
        on:input={() => (errorMessage = "")}
        class="input input-sm input-bordered w-full pr-8 max-w-xs disabled:bg-base-100 focus:input-primary"
        disabled={state !== "edit"}
      />
      <button
        class="absolute top-2 right-3"
        type="button"
        on:click={() => {
          if (state === "edit") {
            reset();
          } else {
            state = "edit";
            oldPassword = "";
            requestAnimationFrame(() => oldPasswordInput.focus());
          }
        }}
        tabindex="-1"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke-width="1.5"
          stroke="currentColor"
          class="w-3.5 pt-0.5"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L6.832 19.82a4.5 4.5 0 01-1.897 1.13l-2.685.8.8-2.685a4.5 4.5 0 011.13-1.897L16.863 4.487zm0 0L19.5 7.125"
          />
        </svg>
      </button>
    </div>
  </div>
  {#if state !== "idle"}
    <RepeatPasswordGroup
      bind:password
      bind:repeatPassword
      bind:similarPasswords
      onInput={() => (errorMessage = "")}
      error={false}
      labels={["New password", "Repeat new password"]}
      inputClass="input-sm focus:input-primary"
    />
    <!-- svelte-ignore a11y-label-has-associated-control -->
    <label class="label h-4">
      <span class="label-text text-red-400">{errorMessage}</span>
    </label>
    {#if canSubmit}
      <button
        class="btn btn-sm w-full mt-3 mb-1 btn-primary"
        disabled={state === "submitting"}
      >
        {#if state === "submitting"}
          <span class="loading loading-spinner loading-xs" />
        {:else}
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke-width="2"
            stroke="currentColor"
            class="h-4"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M16.5 10.5V6.75a4.5 4.5 0 1 0-9 0v3.75m-.75 11.25h10.5a2.25 2.25 0 0 0 2.25-2.25v-6.75a2.25 2.25 0 0 0-2.25-2.25H6.75a2.25 2.25 0 0 0-2.25 2.25v6.75a2.25 2.25 0 0 0 2.25 2.25Z"
            />
          </svg>
          Save password
        {/if}
      </button>
    {:else}
      <button
        class="btn btn-sm w-full mt-3 mb-1"
        type="button"
        on:click={reset}
      >
        Cancel
      </button>
    {/if}
  {/if}
</form>
