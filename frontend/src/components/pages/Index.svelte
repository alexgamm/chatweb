<script>
  import { onMount } from "svelte";
  import Chat from "../Chat.svelte";
  import DrawerTabs from "../DrawerTabs.svelte";
  import UsernameModal from "../modals/UsernameModal.svelte";
  import { getUser } from "../../stores/user";
  onMount(() => {
    getUser();
  });
</script>

<div class="drawer lg:drawer-open h-[100dvh]">
  <input id="drawer-toggle" type="checkbox" class="drawer-toggle" />
  <div class="drawer-content flex flex-col p-2 overflow-auto">
    <Chat />
  </div>
  <div class="drawer-side">
    <label for="drawer-toggle" class="drawer-overlay" />
    <DrawerTabs />
  </div>
</div>
<UsernameModal />
