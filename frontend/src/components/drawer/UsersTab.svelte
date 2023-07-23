<script>
  import { onMount } from "svelte";
  import { addEventHandler } from "../../contexts/events";
  import useApi from "../../hooks/api";
  const {
    authorized: { get },
  } = useApi();
  let users = [];

  const loadUsers = async () => {
    try {
      const responseBody = await get("/api/users");
      users = responseBody.users;
    } catch {
      alert("could not get users"); // TODO handle properly
    }
  };
  onMount(() => {
    loadUsers();

    addEventHandler("USER_ONLINE", (event) => {
      if (users.find((user) => user.id === event.userId)) {
        users = users.map((user) =>
          user.id === event.userId ? { ...user, online: event.online } : user
        );
      } else {
        loadUsers();
      }
    });
  });
</script>

<ul class="menu p-4 w-full text-base-content" data-tab="user-list">
  {#each users as user}
    <li class="mb-2">
      <a
        class={`flex justify-between ${
          user.online ? "bg-primary bg-opacity-10" : "bg-base-100"
        }`}
      >
        <strong>{user.username}</strong>
        <i
          class={`w-3 h-3 rounded-full ${
            user.online ? "bg-primary" : "bg-gray-400"
          }`}
        />
      </a>
    </li>
  {/each}
</ul>
