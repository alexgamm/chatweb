<script>
  import { Link, navigate } from "svelte-routing";
  import useApi from "../../hooks/api";
  import GoogleOAuthButton from "../GoogleOAuthButton.svelte";
  import TelegramOAuthButton from "../TelegramOAuthButton.svelte";

  const { post } = useApi();
  let state = "idle";
  let email = "";
  let password = "";
  let errorMessage = "";
  $: canSubmit = !!email && !!password && state === "idle" && !errorMessage;
  const submit = async () => {
    if (!canSubmit) {
      return;
    }
    state = "pending";
    let responseBody;
    try {
      responseBody = await post("/api/login", {
        email,
        password,
      });
    } catch (error) {
      errorMessage = error;
      return;
    } finally {
      state = "idle";
    }
    const { needVerification, accessToken } = responseBody;
    if (needVerification) {
      navigate("/verification?email=" + email);
      return;
    }
    localStorage.setItem("token", accessToken);
    navigate("/");
  };
</script>

<main class="flex justify-center items-center h-screen flex-col">
  <h1 class="text-3xl mb-10">Please sign in</h1>
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
        <label class="label" for="email">
          <span class="label-text">email</span>
        </label>
        <input
          class="input input-bordered"
          type="text"
          id="email"
          bind:value={email}
          on:input={() => (errorMessage = "")}
          required
        />
      </div>
      <div class="form-control">
        <label class="label" for="password">
          <span class="label-text">password</span>
        </label>
        <input
          class="input input-bordered"
          type="password"
          id="password"
          bind:value={password}
          on:input={() => (errorMessage = "")}
          required
        />
      </div>
      <span class="text-error">
        {errorMessage}
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
          Sign in
        </button>
      </div>
      <div class="divider">or</div>
      <Link
        to="/registration"
        class="btn gap-2 bg-white bg-opacity-10 border-transparent hover:bg-opacity-5 hover:bg-white hover:border-transparent w-80"
      >
        <svg
          class="fill-violet-400"
          xmlns="http://www.w3.org/2000/svg"
          height="1.8em"
          viewBox="0 0 512 512"
        >
          <path
            d="M47.6 300.4L228.3 469.1c7.5 7 17.4 10.9 27.7 10.9s20.2-3.9 27.7-10.9L464.4 300.4c30.4-28.3 47.6-68 47.6-109.5v-5.8c0-69.9-50.5-129.5-119.4-141C347 36.5 300.6 51.4 268 84L256 96 244 84c-32.6-32.6-79-47.5-124.6-39.9C50.5 55.6 0 115.2 0 185.1v5.8c0 41.5 17.2 81.2 47.6 109.5z"
          />
        </svg>
        Sign up
      </Link>
    </div>
  </form>
</main>
