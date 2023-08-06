<script>
  import { onMount, tick } from "svelte";
  import ChatMessage from "./ChatMessage.svelte";
  import useMessageColor from "../hooks/message-color";
  import { addEventHandler } from "../contexts/events";
  import useApi from "../hooks/api";
  import user from "../stores/user";

  export let onContextMenu;
  export let onReaction;

  const {
    authorized: { get },
  } = useApi();
  const addColor = useMessageColor();
  let messages = [];
  let messagesContainerWrapper;
  let loading = false;

  $: if ($user?.color) {
    messages = messages.map(addColor);
  }

  const sortMessages = () => {
    messages = messages.sort((a, b) => a.sendDate - b.sendDate);
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
    } catch (error) {
      console.error("could not get messages", error); // TODO handle properly
      return;
    } finally {
      loading = false;
    }
    messages = [...messages, ...responseBody.messages.map(addColor)];
    sortMessages();
    //map сам вызывает addColor
    await tick();
    const scrollTop = messages
      .slice(0, responseBody.messages.length)
      .reduce((acc, curr) => acc + curr.elemHeight, 0);
    messagesContainerWrapper.scrollTop = scrollTop;
  };
  const scrollBottom = async () => {
    await tick();
    messagesContainerWrapper.scrollTo({
      top: messagesContainerWrapper.scrollHeight,
      behavior: "smooth",
    });
  };
  onMount(async () => {
    await loadMessages(20);
    scrollBottom();
    addEventHandler("NEW_MESSAGE", (event) => {
      if (messages.find((message) => message.id === event.message.id)) {
        return;
      }
      messages = [...messages, addColor(event.message)];
      sortMessages();
      scrollBottom();
    });
    addEventHandler("CHANGE_USERNAME", (event) => {
      messages = messages.map((message) =>
        message.userId === event.userId
          ? { ...message, username: event.newUsername }
          : message
      );
    });
    addEventHandler("EDITED_MESSAGE", (event) => {
      messages = messages.map((message) =>
        message.id === event.message.id
          ? { ...message, ...event.message }
          : message
      );
    });
    addEventHandler("DELETED_MESSAGE", (event) => {
      messages = messages.filter(({ id }) => id !== event.messageId);
    });
    addEventHandler("REACTION", (event) => {
      messages = messages.map((message) =>
        message.id === event.messageId
          ? {
              ...message,
              reactions: event.reactions.sort((a, b) => b.count - a.count),
            }
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

      const from = messages[0]?.sendDate;
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
    {#each messages as message (message.id)}
      <ChatMessage
        {message}
        onContextMenu={({ pageX, pageY }) =>
          onContextMenu({
            x: pageX,
            y: pageY,
            message,
          })}
        onReaction={(reaction) => onReaction(message.id, reaction)}
      />
    {/each}
  </div>
</div>
