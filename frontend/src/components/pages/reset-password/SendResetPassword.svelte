<script>
  import useApi from "../../../hooks/api";

  const { post } = useApi();
  let state = "idle";
  export let email = "";

  $: canSubmit = !!email && state === "idle";

  const submit = async () => {
    if (!canSubmit) {
      return;
    }
    state = "pending";
    try {
      await post("/api/reset-password/send", {
        email,
      });
    } catch (err) {
      state = "idle";
      return;
    }
    state = "sent";
  };
</script>

<main class="flex justify-center items-center h-screen flex-col">
  <h1 class="text-3xl mb-10">Reset password</h1>
  <form
    class="card flex-shrink-0 w-full max-w-sm shadow-2xl bg-neutral-focus"
    on:submit|preventDefault={submit}
  >
    <div class="card-body">
      {#if state === "sent"}
        <div
          class="flex text-center flex-col gap-4 items-center justify-center"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke-width="1"
            class="w-20 stroke-violet-400"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M9 12.75 11.25 15 15 9.75M21 12c0 1.268-.63 2.39-1.593 3.068a3.745 3.745 0 0 1-1.043 3.296 3.745 3.745 0 0 1-3.296 1.043A3.745 3.745 0 0 1 12 21c-1.268 0-2.39-.63-3.068-1.593a3.746 3.746 0 0 1-3.296-1.043 3.745 3.745 0 0 1-1.043-3.296A3.745 3.745 0 0 1 3 12c0-1.268.63-2.39 1.593-3.068a3.745 3.745 0 0 1 1.043-3.296 3.746 3.746 0 0 1 3.296-1.043A3.746 3.746 0 0 1 12 3c1.268 0 2.39.63 3.068 1.593a3.746 3.746 0 0 1 3.296 1.043 3.746 3.746 0 0 1 1.043 3.296A3.745 3.745 0 0 1 21 12Z"
            />
          </svg>
          The password reset link has been sent to <b>{email}</b>
        </div>
      {:else}
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
      {/if}
    </div>
  </form>
</main>
