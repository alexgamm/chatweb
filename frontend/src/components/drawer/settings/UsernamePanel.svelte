<script>
  import { onMount } from "svelte";
  import useApi from "../../../hooks/api";
  import { pushToast } from "../../../stores/toast";
  import user, { getUser } from "../../../stores/user";

  const {
    authorized: { put },
  } = useApi();

  let usernameInput;
  let username;
  let editing = false;
  let submitting = false;
  let errorMessage = "";

  const submit = async () => {
    if (submitting) return;
    submitting = true;
    try {
      if (username !== $user.username) {
        await put("/api/users/me/username", {
          username,
        });
        await getUser();
        pushToast({ type: "success", message: "Username saved" });
      }
      editing = false;
      usernameInput.blur();
    } catch (error) {
      errorMessage = error.message;
      return;
    } finally {
      submitting = false;
    }
  };

  onMount(() => {
    username = $user.username;
  });
</script>

<form on:submit|preventDefault={submit}>
  <div class="form-control rounded-xl bg-neutral-focus py-1 px-3">
    <!-- svelte-ignore a11y-label-has-associated-control -->
    <label class="label">
      <span class="label-text">Your username</span>
    </label>
    <div class="relative">
      <input
        type="text"
        placeholder={$user.username}
        bind:this={usernameInput}
        bind:value={username}
        class={`input input-sm input-bordered w-full pr-8 max-w-xs ${editing ? "input-primary text-gray-300" : "text-gray-400 cursor-pointer"}`}
        on:input={() => (errorMessage = "")}
        on:focus={() => (editing = true)}
        on:focusout={(e) => {
          if (e.target?.parentElement === e.relatedTarget?.parentElement) {
            return;
          }
          username = $user.username;
          editing = false;
        }}
      />
      <button
        class="absolute top-2 right-3"
        type="button"
        on:click={() => {
          if (editing) {
            submit();
          } else {
            editing = true;
            usernameInput.focus();
          }
        }}
      >
        {#if submitting}
          <span class="loading loading-spinner loading-xs mb-4" />
        {:else if editing}
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="-2 2 24 24"
            stroke-width="1.5"
            stroke="currentColor"
            class="h-5 pt-0.5 stroke-gray-300"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="m4.5 12.75 6 6 9-13.5"
            />
          </svg>
        {:else}
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
        {/if}
      </button>
    </div>
    <!-- svelte-ignore a11y-label-has-associated-control -->
    <label class="label">
      <span class="label-text text-red-400">{errorMessage}</span>
    </label>
  </div>
</form>
