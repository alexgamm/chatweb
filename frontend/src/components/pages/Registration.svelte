<script>
  import { navigate } from "svelte-routing";
  import useApi from "../../hooks/api";
  import GoogleOAuthButton from "../GoogleOAuthButton.svelte";
  import RepeatPasswordGroup from "../RepeatPasswordGroup.svelte";
  import TelegramOAuthButton from "../TelegramOAuthButton.svelte";

  let errorMessage = "";
  let username = "";
  let password = "";
  let repeatPassword = "";
  let email = "";
  let state = "idle";
  const { post } = useApi();
  let similarPasswords = false;
  $: errorMessage =
    repeatPassword && password && !similarPasswords
      ? "passwords don't match"
      : "";

  $: canSubmit =
    state === "idle" &&
    !!username &&
    !!password &&
    !!repeatPassword &&
    !!email &&
    similarPasswords;
  const submit = async () => {
    if (!canSubmit) {
      return;
    }
    state = "pending";
    try {
      await post("/api/registration", {
        username,
        password,
        email,
      });
    } catch (error) {
      errorMessage = error;
      return;
    } finally {
      state = "idle";
    }
    navigate("/verification?email=" + email);
  };
</script>

<main class="flex justify-center items-center h-screen flex-col">
  <h1 class="text-3xl mb-10">Please sign up</h1>
  <form
    class="card flex-shrink-0 w-full max-w-sm shadow-2xl bg-neutral-focus"
    on:submit|preventDefault={submit}
  >
    <div class="card-body">
      <div class="flex gap-2 flex-wrap mb-2">
        <h3 class="w-full text-center mb-2">Continue with</h3>
        <GoogleOAuthButton />
        <TelegramOAuthButton />
      </div>
      <div class="divider">or</div>
      <div class="form-control">
        <label class="label" for="username">
          <span class="label-text">username</span>
        </label>
        <input
          class="input input-bordered"
          type="text"
          id="username"
          bind:value={username}
          required
        />
      </div>
      <div class="form-control">
        <label class="label" for="email">
          <span class="label-text">email</span>
        </label>
        <input
          class="input input-bordered"
          type="email"
          id="email"
          bind:value={email}
          required
        />
      </div>
      <RepeatPasswordGroup
        error={errorMessage}
        bind:password
        bind:repeatPassword
        bind:similarPasswords
      />
      <span class="text-error h-4">{errorMessage}</span>
      <div class="form-control mt-2">
        <button
          type="submit"
          class="btn bg-violet-500 bg-opacity-60 border-transparent hover:bg-opacity-80 hover:bg-violet-500 hover:border-transparent mb-2 w-full text-gray-300"
          disabled={!canSubmit}
        >
          {#if state === "pending"}
            <span class="loading loading-spinner" />
          {/if}
          Sign up
        </button>
      </div>
    </div>
  </form>
</main>
