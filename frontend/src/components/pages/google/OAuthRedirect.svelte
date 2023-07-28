<script>
  import { onMount } from "svelte";
  import useApi from "../../../hooks/api";
  const code = new URLSearchParams(window.location.search).get("code");

  const { post } = useApi();
  onMount(async () => {
    if (!code) {
      return;
    }
    let accessToken;
    try {
      const responseBody = await post("/api/google/oauth/token", { code });
      accessToken = responseBody.accessToken;
    } catch {
      window.opener.postMessage("OAUTH_FAILED", location.origin);
      window.close();
      return;
    }
    localStorage.setItem("token", accessToken);
    window.opener.postMessage("OAUTH_SUCCESS", location.origin);
    window.close();
  });
</script>

<div class="flex justify-center items-center h-screen">
  <span class="loading loading-spinner text-primary h-10 w-10" />
</div>
