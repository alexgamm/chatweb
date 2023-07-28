<script>
  import { onMount, tick } from "svelte";
  import ChatMessage from "./ChatMessage.svelte";
  import useMessageColor from "../hooks/message-color";
  import { addEventHandler } from "../contexts/events";
  import messages from "../stores/messages"; //TODO remove
  import useApi from "../hooks/api";
  const addColor = useMessageColor();

  let contextMenuPosition = { x: 100, y: 20 };
  let showContextMenu = false;
  let messagesContainerWrapper;
  const {
    authorized: { get, post },
  } = useApi();

  onMount(() => {
    document.addEventListener("click", () => {
      showContextMenu = false;
    });
    document.addEventListener("contextmenu", (e) => {
      if (
        !e.target.classList.contains(".chat-bubble") &&
        // check parentness
        !e.target.closest(".chat-bubble")
      ) {
        showContextMenu = false;
      }
    });
  });

  let messageText = "";
  let repliedMessage;
  let selectedMessage;
  let loading = false;
  const sortMessages = () => {
    $messages = $messages.sort((a, b) => a.sendDate - b.sendDate);
  };
  const loadMessages = async (count, from) => {
    if (loading) {
      return;
    }
    const params = { count };
    if (from) {
      params.from = from;
    }
    loading = true;
    let responseBody;
    try {
      responseBody = await get(
        `/api/messages?${new URLSearchParams(params).toString()}`
      );
    } catch (error){
      console.error("could not get messages", error); // TODO handle properly
      return;
    } finally {
      loading = false;
    }
    $messages = [...$messages, ...responseBody.messages.map(addColor)];
    sortMessages();
    //map сам вызывает addColor
    await tick();
    const scrollTop = $messages
      .slice(0, responseBody.messages.length)
      .reduce((acc, curr) => acc + curr.elemHeight, 0);
    messagesContainerWrapper.scrollTop = scrollTop;
  };
  const sendMessage = async () => {
    if (!messageText) {
      return;
    }
    try {
      await post("/api/messages", {
        message: messageText,
        repliedMessageId: repliedMessage?.id,
      });
    } catch(error) {
      console.error("could not send message", error); // TODO handle properly
      return;
    }
    messageText = "";
    repliedMessage = null;
  };
  const scrollBottom = () =>
    requestAnimationFrame(() =>
      messagesContainerWrapper.scrollTo({
        top: messagesContainerWrapper.scrollHeight,
        behavior: "smooth",
      })
    );
  onMount(async () => {
    await loadMessages(20);
    scrollBottom();
    addEventHandler("NEW_MESSAGE", (event) => {
      if ($messages.find((message) => message.id === event.message.id)) {
        return;
      }
      $messages = [...$messages, addColor(event.message)];
      sortMessages();
      scrollBottom();
    });
    addEventHandler("CHANGE_USERNAME", (event) => {
      $messages = $messages.map((message) =>
        message.userId === event.userId
          ? { ...message, username: event.newUsername }
          : message
      );
    });
  });
</script>

<div
  class="messages-container-wrapper flex-1 min-h-0 overflow-auto py-2"
  bind:this={messagesContainerWrapper}
  on:scroll={(scrollEvent) => {
    if (scrollEvent.target.scrollTop === 0) {
      //target is messagesContWrapper

      const from = $messages[0]?.sendDate;
      if (from) {
        loadMessages(20, from);
      }
    }
  }}
>
  <div class="flex justify-center min-h-16">
    {#if loading}
      <span class="loading loading-spinner text-primary" />
    {/if}
  </div>

  <div class="messages-container flex flex-col justify-end min-h-full">
    {#each $messages as message (message.id)}
      <ChatMessage
        {message}
        onContextMenu={(event) => {
          showContextMenu = true;
          contextMenuPosition = { x: event.pageX, y: event.pageY };
          selectedMessage = message;
        }}
      />
    {/each}
  </div>
</div>
{#if repliedMessage}
  <div class="alert shadow-lg">
    <svg
      class="h-5 w-5 text-gray-500"
      fill="none"
      viewBox="0 0 24 24"
      stroke="currentColor"
    >
      <path
        stroke-linecap="round"
        stroke-linejoin="round"
        stroke-width="2"
        d="M3 10h10a8 8 0 018 8v2M3 10l6 6m-6-6l6-6"
      />
    </svg>
    <span>{repliedMessage.message}</span>
  </div>
{/if}

<form on:submit|preventDefault={sendMessage}>
  <input
    class="input input-bordered input-primary w-full"
    type="text"
    placeholder="type something..."
    autocomplete="off"
    bind:value={messageText}
  />
</form>

<div
  class={`bg-base-100 absolute rounded-lg ${!showContextMenu ? "hidden" : ""}`}
  style={`left: ${contextMenuPosition.x}px; top: ${contextMenuPosition.y}px`}
>
  <button
    class="btn bg-violet-900 hover:bg-opacity-70 border-none hover:bg-violet-900 normal-case pr-9 text-gray-300 text-base w-40 z-10"
    on:click={() => {
      repliedMessage = selectedMessage;
    }}
  >
    <svg
      class="h-5 w-5 text-gray-300"
      fill="none"
      viewBox="0 0 24 24"
      stroke="currentColor"
    >
      <path
        stroke-linecap="round"
        stroke-linejoin="round"
        stroke-width="2"
        d="M3 10h10a8 8 0 018 8v2M3 10l6 6m-6-6l6-6"
      />
    </svg>
    Reply
  </button>
</div>
