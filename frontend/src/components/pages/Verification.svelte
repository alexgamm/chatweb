<script>
  import { navigate } from "svelte-routing";
  import useApi from "../../hooks/api";

  const codeLength = 6;
  const email = new URLSearchParams(window.location.search).get("email");
  const { post } = useApi();
  let errorMessage = "";
  const submit = async (code) => {
    let accessToken;
    try {
      const responseBody = await post("/api/verification", {
        code,
        email,
      });
      accessToken = responseBody.accessToken;
    } catch {
      errorMessage = "invalid code";
      return;
    }
    localStorage.setItem("token", accessToken);
    navigate("/");
  };
  const onInput = (event) => {
    errorMessage = "";
    if (event.target.value.length >= codeLength) {
      event.target.setSelectionRange(codeLength - 1, codeLength - 1);
      submit(event.target.value);
    }
  };
</script>

<main class="flex justify-center items-center h-screen flex-col">
  <h1 class="text-3xl mb-10">Verify your email</h1>
  <form
    method="post"
    class="card flex-shrink-0 w-80 shadow-2xl bg-neutral-focus"
  >
    <div class="card-body">
      <h2 class="text-sm mb-5 text-center">
        Enter the code you received at<br />
        <b>{email}</b>
      </h2>
      <div class="form-control relative">
        <input
          class="bg-transparent focus:outline-none text-2xl tracking-[1.48em]"
          autofocus
          type="text"
          maxlength={codeLength}
          on:input={onInput}
        />
        <div
          class="absolute flex w-[281px] -top-1 -left-3 pointer-events-none justify-between"
        >
          {#each Array(codeLength).fill("") as _}
            <div class="w-10 h-[45px] rounded-xl border border-primary" />
          {/each}
        </div>
      </div>
      <span class="text-error mt-4 text-center">{errorMessage}</span>
    </div>
  </form>
</main>
