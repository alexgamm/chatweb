<script>
  import { navigate } from "svelte-routing";

  const logout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };
</script>

<button
  class="btn btn-neutral btn-sm w-full mt-3 mb-1"
  type="button"
  on:click={logout}
>
  Logout
</button>
