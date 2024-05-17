<script>
  import { onMount } from "svelte";
  import { addEventHandler } from "../../contexts/events";
  import useApi from "../../hooks/api";
  import useMessageColor from "../../hooks/message-color";
  import { fetchGame, game, joinGame } from "../../stores/codenames";
  import roomPasswordModal from "../../stores/room-password-modal";
  import user, { getUser } from "../../stores/user";
  import { userColors } from "../../stores/users";
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
  import RoomPasswordModal from "../modals/RoomPasswordModal.svelte";

  export let room = "global";

  const {
    authorized: { post },
  } = useApi();

  $: roomPrefix = room.split("-")[0];
  $: addColor = useMessageColor($userColors, $game);

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

  const joinRoom = async (password) =>
    post(`/api/rooms/${room}/join`, password ? { password } : {});

  const onPasswordSubmit = async (password) => {
    await joinRoom(password);
    afterJoin();
  };

  const afterJoin = async () => {
    await getUser();
    if (roomPrefix === "codenames") {
      const fetched = await fetchGame(room);
      if (
        !fetched.teams.find(({ players }) =>
          players.find(({ userId }) => userId === $user.id)
        ) &&
        !fetched.viewers.find(({ userId }) => userId === $user.id)
      ) {
        joinGame(room).then(() => fetchGame(room));
      }
      addEventHandler("GameUpdatedEvent", (event) => {
        if (event.game.id === room) {
          game.set(event.game);
        }
      });
    }
  };

  onMount(async () => {
    if (room !== "global") {
      try {
        await post(`/api/rooms/${room}/join`, {});
      } catch (error) {
        if (error.statusCode === 400) {
          $roomPasswordModal.showModal();
          return;
        }
      }
    }
    afterJoin();
  });
</script>

<div class="drawer lg:drawer-open h-[100dvh]">
  <input id="drawer-toggle" type="checkbox" class="drawer-toggle" />
  <div class="drawer-content flex flex-col overflow-auto">
    {#if roomPrefix === "codenames"}
      <CodenamesBoard />
    {/if}
    <Chat {room} {addColor} />
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
<RoomPasswordModal onSubmit={onPasswordSubmit} />
