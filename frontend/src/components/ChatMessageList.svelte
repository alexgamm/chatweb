<script>
  import { onMount, tick } from "svelte";
  import useApi from "../hooks/api";
  import setSendDate from "../utils/message-send-date";
  import { reactionsOrder } from "../utils/reactions";
  import ChatMessage from "./ChatMessage.svelte";
  import useEvents from "../contexts/events";

  export let room;
  export let onContextMenu;
  export let onReaction;
  export let messages;
  export let addColor;
  export let hideLoader = false;

  const {
    authorized: { get },
  } = useApi();
  const { addEventHandler } = useEvents(room);
  let messagesContainerWrapper;
  let loading = false;

  $: if (addColor) {
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
      params.from = new Date(from).getTime();
    }
    if (room && room !== "global") {
      params.room = room;
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
    messages = [
      ...messages,
      ...responseBody.messages
        .map(addColor)
        .map(setSendDate)
        .map((msg) => {
          msg.reactions = msg.reactions?.sort(reactionsOrder);
          return msg;
        }),
    ];
    sortMessages();
    //map сам вызывает addColor
    await tick();
    const scrollTop = messages
      .slice(0, responseBody.messages.length)
      .reduce((acc, curr) => acc + curr.elem.clientHeight, 0);
    messagesContainerWrapper.scrollTop = scrollTop;
  };
  const scrollBottom = async () => {
    await tick();
    if (!messagesContainerWrapper) return;
    messagesContainerWrapper.scrollTo({
      top: messagesContainerWrapper.scrollHeight,
      behavior: "smooth",
    });
  };

  const isLastMessageVisible = () => {
    const { scrollHeight, clientHeight, scrollTop } = messagesContainerWrapper;
    return (
      scrollHeight - clientHeight - scrollTop <
      messages.at(-1).elem.clientHeight
    );
  };

  onMount(() => {
    loadMessages(20).then(scrollBottom);
    addEventHandler("NewMessageEvent", (event) => {
      if (messages.find((message) => message.id === event.message.id)) {
        return;
      }
      messages = [...messages, setSendDate(addColor(event.message))];
      sortMessages();
      scrollBottom();
    });
    addEventHandler("ChangeUsernameEvent", (event) => {
      messages = messages.map((message) =>
        message.userId === event.userId
          ? { ...message, username: event.newUsername }
          : message
      );
    });
    addEventHandler("EditedMessageEvent", (event) => {
      messages = messages.map((message) =>
        message.id === event.message.id
          ? { ...message, ...event.message, reactions: message.reactions }
          : message
      );
    });
    addEventHandler("DeletedMessageEvent", (event) => {
      messages = messages.filter(({ id }) => id !== event.messageId);
    });
    addEventHandler("ReactionEvent", async (event) => {
      const foundMessage = messages.find(({ id }) => id === event.messageId);
      if (!foundMessage) return;
      messages = messages.map((message) =>
        message === foundMessage
          ? {
              ...message,
              reactions: event.reactions.sort(reactionsOrder),
            }
          : message
      );
      if (messages.at(-1)?.id === foundMessage.id && isLastMessageVisible()) {
        await tick();
        scrollBottom();
      }
    });
    const setSendDateInterval = setInterval(() => {
      messages = messages.map(setSendDate);
    }, 15_000);
    return () => clearInterval(setSendDateInterval);
  });
</script>

<div
  class="messages-container-wrapper flex-1 min-h-0 overflow-auto p-2"
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
    {#if loading && !hideLoader}
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
