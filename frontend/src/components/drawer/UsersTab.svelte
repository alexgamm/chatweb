<script>
  import { onMount } from "svelte";
  import { fade } from "svelte/transition";
  import { addEventHandler } from "../../contexts/events";
  import useApi from "../../hooks/api";
  import USER_COLORS from "../../utils/user-colors";
  import users from "../../stores/users";
  import useHeavyPush from "../../hooks/heavy-list";
  const {
    authorized: { get },
  } = useApi();
  let loading = false;
  const heavyPush = useHeavyPush();

  const loadUsers = async () => {
    loading = true;
    try {
      const { users: list } = await get("/api/users");
      $users = list;
      sortUsers();
    } catch (error) {
      console.error("could not get users", error); // TODO handle properly
    } finally {
      loading = false;
    }
  };
  const sortUsers = () => {
    $users = $users.sort((a, b) => +b.online - +a.online);
  };
  onMount(() => {
    loadUsers();

    addEventHandler("UserOnlineEvent", (event) => {
      if ($users.find((user) => user.id === event.userId)) {
        $users = $users.map((user) =>
          user.id === event.userId ? { ...user, online: event.online } : user
        );
        sortUsers();
      } else {
        loadUsers();
      }
    });
    addEventHandler("ChangeUsernameEvent", (event) => {
      $users = $users.map((user) =>
        user.id === event.userId
          ? { ...user, username: event.newUsername }
          : user
      );
    });
    addEventHandler("ChangeUserColorEvent", (event) => {
      $users = $users.map((user) =>
        user.id === event.userId ? { ...user, color: event.color } : user
      );
    });
    addEventHandler("UserTypingEvent", (event) => {
      const user = $users.find(({ id }) => id === event.userId);
      if (!user) {
        return;
      }
      if (user.typingTimeout) {
        clearTimeout(user.typingTimeout);
      }
      const typingTimeout = setTimeout(() => {
        $users = $users.map((user) =>
          user.id === event.userId ? { ...user, typing: false } : user
        );
      }, 3000);
      $users = $users.map((user) =>
        user.id === event.userId
          ? { ...user, typing: true, typingTimeout }
          : user
      );
    });
  });
</script>

{#if loading}
  <div class="flex justify-center items-center h-16">
    <span class="loading loading-spinner text-primary" />
  </div>
{:else}
  <ul class="p-4 w-full text-base-content overflow-y-auto">
    {#each $users as user}
      <li class="mb-2" in:fade>
        <a
          class={`flex justify-between items-center py-2 px-4 rounded-lg text-sm ${
            user.online
              ? `${USER_COLORS[user.color]} bg-opacity-10`
              : "bg-base-100"
          }`}
          href="#"
        >
          <div class="flex justify-between items-center gap-2 h-4">
            <strong class={user.typing ? "animate-pulse" : ""}>
              {user.username}
            </strong>
            {#if user.typing}
              <span
                class="loading loading-dots loading-md text-gray-300 animate-pulse opacity-75"
              />
            {/if}
          </div>

          <i
            class={`w-3 h-3 rounded-full ${
              user.online ? USER_COLORS[user.color] : "bg-gray-400"
            }`}
          />
        </a>
      </li>
    {/each}
  </ul>
{/if}
