<script>
  import { navigate } from "svelte-routing";
  import useApi from "../../../hooks/api";
  import RepeatPasswordGroup from "../../RepeatPasswordGroup.svelte";

  export let sessionId;

  const { post } = useApi();
  let state = "idle";
  let newPassword = "";
  let repeatPassword = "";
  let error;
  let similarPasswords;

  $: canSubmit = !!newPassword && state === "idle";

  const submit = async () => {
    if (!canSubmit) {
      return;
    }
    if (!similarPasswords) {
      error = "passwords don't match";
      return;
    }
    state = "pending";
    let responseBody;
    try {
      responseBody = await post("/api/reset-password", {
        sessionId,
        newPassword,
      });
    } catch ({ message }) {
      error = message;
      return;
    } finally {
      state = "idle";
    }
    const { accessToken } = responseBody;
    localStorage.setItem("token", accessToken);
    navigate("/");
  };
</script>

<main class="flex justify-center items-center h-screen flex-col">
  <h1 class="text-3xl mb-10">Make up a new password</h1>
  <form
    class="card flex-shrink-0 w-full max-w-sm shadow-2xl bg-neutral-focus"
    on:submit|preventDefault={submit}
  >
    <div class="card-body">
      <RepeatPasswordGroup
        bind:password={newPassword}
        bind:repeatPassword
        bind:similarPasswords
        bind:error
        onInput={() => (error = "")}
      />
      <span class="text-error h-4">
        {error ?? ""}
      </span>
      <div class="form-control mt-6">
        <button
          type="submit"
          class="btn bg-violet-500 bg-opacity-60 border-transparent hover:bg-opacity-80 hover:bg-violet-500 hover:border-transparent mb-2 w-full text-gray-300"
          disabled={!canSubmit}
        >
          {#if state === "pending"}
            <span class="loading loading-spinner" />
          {/if}
          Submit
        </button>
      </div>
    </div>
  </form>
</main>
