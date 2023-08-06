<script>
  import { onMount } from "svelte";
  import { navigate } from "svelte-routing";

  const windowWidth = 500;
  const windowHeight = 700;

  const openOAuthWindow = () => {
    const left = screen.width / 2 - windowWidth / 2;
    const top = screen.height / 2 - windowHeight / 2;
    window.open(
      "/api/google/oauth",
      "googleOAuth",
      `popup, width=${windowWidth}, height=${windowHeight}, left=${left}, top=${top}`
    );
  };
  onMount(() => {
    const listener = (event) => {
      if (event.data === "OAUTH_SUCCESS") {
        navigate("/");
      } else if (event.data === "OAUTH_FAILED") {
        console.error("google oauth failed");
      }
    };
    window.addEventListener("message", listener);
    return () => window.removeEventListener("message", listener);
  });
</script>

<button
  class="btn bg-white bg-opacity-10 border-transparent hover:bg-opacity-5
  hover:bg-white hover:border-transparent mb-2 w-full"
  type="button"
  on:click={openOAuthWindow}
>
  <svg
    class="fill-violet-400 mr-3"
    xmlns="http://www.w3.org/2000/svg"
    height="1.8em"
    viewBox="0 0 488 512"
  >
    <path
      d="M488 261.8C488 403.3 391.1 504 248 504 110.8 504 0 393.2 0 256S110.8 8 248 8c66.8 0 123 24.5 166.3 64.9l-67.5 64.9C258.5 52.6 94.3 116.6 94.3 256c0 86.5 69.1 156.6 153.7 156.6 98.2 0 135-70.4 140.8-106.9H248v-85.3h236.1c2.3 12.7 3.9 24.9 3.9 41.4z"
    />
  </svg>
  Continue with Google
</button>
