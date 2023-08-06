<script>
  import { onMount } from "svelte";
  import { addEventHandler } from "../../contexts/events";
  import useApi from "../../hooks/api";
  import { fade } from "svelte/transition";
  import useHeavyList from "../../hooks/heavy-list";
  import useDebounce from "../../utils/debounce";
  const {
    authorized: { get },
  } = useApi();
  let loading = false;
  const [users, setUsers, updateUser] = useHeavyList();
  const typingDebounce = useDebounce();

  const loadUsers = async () => {
    loading = true;
    try {
      const { users } = await get("/api/users");
      setUsers(users);
    } catch (error) {
      console.error("could not get users", error); // TODO handle properly
    } finally {
      loading = false;
    }
  };
  onMount(() => {
    loadUsers();

    addEventHandler("USER_ONLINE", (event) => {
      if ($users.find((user) => user.id === event.userId)) {
        updateUser(
          (user) => user.id === event.userId,
          (user) => ({ ...user, online: event.online })
        );
      } else {
        loadUsers();
      }
    });
    addEventHandler("CHANGE_USERNAME", (event) => {
      setUsers(
        $users.map((user) =>
          user.id === event.userId
            ? { ...user, username: event.newUsername }
            : user
        )
      );
    });
    addEventHandler("USER_TYPING", (event) => {
      updateUser(
        (user) => user.id === event.userId,
        (user) => ({ ...user, typing: true })
      );
      setTimeout(() => {
        typingDebounce(() => {
          updateUser(
            (user) => user.id === event.userId,
            (user) => ({ ...user, typing: false })
          );
        });
      }, 3000);
    });
  });
</script>

{#if loading}
  <div class="flex justify-center items-center h-16">
    <span class="loading loading-spinner text-primary" />
  </div>
{:else}
  <ul class="menu p-4 w-full text-base-content" data-tab="user-list">
    {#each $users as user}
      <li class="mb-2" in:fade>
        <a
          class={`flex justify-between ${
            user.online ? "bg-primary bg-opacity-10" : "bg-base-100"
          }`}
          href="#"
        >
          <strong class={user.online ? "animate-pulse" : ""}>
            {user.username}
          </strong>
          <div class="flex justify-between items-center gap-4">
            {#if user.typing}
              <span class="loading loading-dots loading-md opacity-60" />
            {/if}
            <i
              class={`w-3 h-3 rounded-full ${
                user.online ? "bg-primary" : "bg-gray-400"
              }`}
            />
          </div>
        </a>
      </li>
    {/each}
  </ul>
{/if}
