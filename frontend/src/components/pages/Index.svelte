<script>
  import { onMount } from "svelte";
  import { getUser } from "../../stores/user";
  import Chat from "../Chat.svelte";
  import DrawerTabs from "../DrawerTabs.svelte";
  import ChangePasswordModal from "../modals/ChangePasswordModal.svelte";
  import ChangeUsernameModal from "../modals/ChangeUsernameModal.svelte";
  onMount(() => {
    getUser();
  });
</script>

<div class="drawer lg:drawer-open h-[100dvh]">
  <input id="drawer-toggle" type="checkbox" class="drawer-toggle" />
  <div class="drawer-content flex flex-col p-2 overflow-auto">
    <Chat />
  </div>
  <div class="drawer-side overflow-hidden h-[100dvh]">
    <label for="drawer-toggle" class="drawer-overlay" />
    <DrawerTabs />
  </div>
</div>
<label
  for="drawer-toggle"
  class="fixed top-4 right-4 btn btn-sm bg-opacity-30 btn-square hover:bg-opacity-30 bg-black hover:bg-black border-none lg:hidden"
>
  <svg
    xmlns="http://www.w3.org/2000/svg"
    fill="none"
    viewBox="0 0 24 24"
    class="inline-block w-5 h-5 stroke-current"
  >
    <path
      stroke-linecap="round"
      stroke-linejoin="round"
      stroke-width="2"
      d="M4 6h16M4 12h16M4 18h16"
    />
  </svg>
</label>
<ChangeUsernameModal />
<ChangePasswordModal />
