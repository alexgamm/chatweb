<script>
  import { onMount } from "svelte";
  import useApi from "../../../hooks/api";
  import { navigate } from "svelte-routing";
  const authResult = window.location.hash.split("=")[1];

  const { post } = useApi();
  onMount(async () => {
    if (!authResult) {
      return;
    }
    let accessToken;
    try {
      const responseBody = await post("/api/tg/oauth/token", { authResult });
      accessToken = responseBody.accessToken;
    } catch {
      navigate("/login#tg-oauth-failed", { replace: true });
      return;
    }
    localStorage.setItem("token", accessToken);
    navigate("/", { replace: true });
  });
</script>

<div class="flex justify-center items-center h-screen">
  <span class="loading loading-spinner text-primary h-10 w-10" />
</div>
