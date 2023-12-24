<script>
  import { onMount } from "svelte";
  import { getUser } from "../../stores/user";
  import Chat from "../Chat.svelte";
  import CodenamesBoard from "../codenames/CodenamesBoard.svelte";
  import CodenamesTab from "../codenames/CodenamesTab.svelte";
  import DrawerTabs from "../drawer/DrawerTabs.svelte";
  import GamesTab from "../drawer/GamesTab.svelte";
  import SettingsTab from "../drawer/SettingsTab.svelte";
  import UsersTab from "../drawer/UsersTab.svelte";
  import GamesIcon from "../icons/GamesIcon.svelte";
  import SettingsIcon from "../icons/SettingsIcon.svelte";
  import UsersIcon from "../icons/UsersIcon.svelte";
  import ChangePasswordModal from "../modals/ChangePasswordModal.svelte";
  import ChangeUsernameModal from "../modals/ChangeUsernameModal.svelte";

  export let room = "global";

  $: roomPrefix = room.split("-")[0];

  const tabs = {
    global: [
      {
        icon: UsersIcon,
        caption: "Users",
        component: UsersTab,
      },
      {
        icon: GamesIcon,
        caption: "Games",
        component: GamesTab,
      },
      {
        icon: SettingsIcon,
        caption: "Settings",
        component: SettingsTab,
      },
    ],
    codenames: [
      {
        icon: UsersIcon,
        caption: "Codenames",
        component: CodenamesTab,
      },
    ],
  };

  onMount(() => {
    getUser();
  });
</script>

<div class="drawer lg:drawer-open h-[100dvh]">
  <input id="drawer-toggle" type="checkbox" class="drawer-toggle" />
  <div class="drawer-content flex flex-col overflow-auto">
    {#if roomPrefix === "codenames"}
      <CodenamesBoard
        game={{
          words: [
            ["яблоко", "банан", "вишня", "дата", "ежевика"],
            ["инжир", "виноград", "дыня", "айсберг", "джекфрут"],
            ["киви", "лимон", "манго", "нектарин", "апельсин"],
            ["папайя", "айва", "малина", "клубника", "мандарин"],
            ["угли", "ваниль", "арбуз", "хурма", "желтый"],
          ],
        }}
      />
    {/if}
    <Chat />
  </div>
  <div class="drawer-side overflow-hidden h-[100dvh]">
    <label for="drawer-toggle" class="drawer-overlay" />
    <DrawerTabs tabs={tabs[roomPrefix]} hidden={roomPrefix === "codenames"} />
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
