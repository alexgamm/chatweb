<script>
  import { onMount } from "svelte";
  import useApi from "../hooks/api";
  import user from "../stores/user";
  import useDebounce from "../utils/debounce";
  import { reactionsOrder, toggleReaction } from "../utils/reactions";
  import ChatMessageList from "./ChatMessageList.svelte";
  import MessageContextMenu from "./MessageContextMenu.svelte";
  import CloseIcon from "./icons/CloseIcon.svelte";
  import EditIcon from "./icons/EditIcon.svelte";
  import ReplyIcon from "./icons/ReplyIcon.svelte";

  export let room;
  export let addColor;

  const typingDebounce = useDebounce(300);

  let messages = [];
  let submitting = false;
  let contextMenuPosition = { x: 0, y: 0 };
  let showContextMenu = false;

  const {
    authorized: { post, patch, put, del },
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

  let messageTextInput;
  let messageText = "";
  let repliedMessage;
  let editedMessage;
  let selectedMessage;
  const submit = async () => {
    if (submitting || !messageText || editedMessage?.message === messageText) {
      return;
    }
    submitting = true;
    try {
      if (editedMessage) {
        await patch(`/api/messages/${editedMessage.id}`, {
          message: messageText,
        });
      } else {
        await post("/api/messages", {
          message: messageText,
          repliedMessageId: repliedMessage?.id,
          room: room === "global" ? null : room,
        });
      }
    } catch (error) {
      console.error("could not send/edit message", error); // TODO handle properly
      return;
    } finally {
      submitting = false;
    }
    messageText = "";
    repliedMessage = null;
    editedMessage = null;
    console.log(messageTextInput);
    requestAnimationFrame(() => messageTextInput.focus());
  };

  const onInput = () =>
    typingDebounce(async () => {
      try {
        await put(`/api/messages/typing?room=${room === "global" ? "" : room}`);
      } catch (error) {
        console.error("could not send typing", error);
      }
    });

  const deleteMessage = async (id) => {
    try {
      await del(`/api/messages/${id}`);
    } catch (error) {
      console.error("could not delete message", error); // TODO handle properly
    }
  };

  const sendReaction = async (messageId, reaction) => {
    try {
      messages = messages.map((msg) =>
        msg.id === messageId
          ? {
              ...msg,
              reactions: toggleReaction(msg.reactions, reaction).sort(
                reactionsOrder
              ),
            }
          : msg
      );
      await put(`/api/messages/${messageId}/reactions`, { reaction });
    } catch (error) {
      console.error("could not send reaction", error);
    }
  };
</script>

<ChatMessageList
  {room}
  {addColor}
  bind:messages
  onContextMenu={({ x, y, message }) => {
    showContextMenu = true;
    contextMenuPosition = { x, y };
    selectedMessage = message;
  }}
  onReaction={sendReaction}
  hideLoader={room?.startsWith("codenames")}
/>
{#if repliedMessage || editedMessage}
  <div class="alert shadow-lg bg-primary bg-opacity-20 mb-4">
    {#if repliedMessage}
      <ReplyIcon />
    {/if}
    {#if editedMessage}
      <EditIcon />
    {/if}
    <span>{repliedMessage?.message ?? editedMessage?.message}</span>
    {#if editedMessage}
      <button
        class="hover:opacity-70"
        on:click={() => {
          editedMessage = null;
          messageText = "";
        }}
      >
        <CloseIcon />
      </button>
    {/if}
  </div>
{/if}

<form class="p-2" on:submit|preventDefault={submit}>
  <input
    class="input input-bordered input-primary w-full"
    type="text"
    placeholder="type something..."
    autocomplete="off"
    bind:this={messageTextInput}
    bind:value={messageText}
    on:input={onInput}
    disabled={submitting}
  />
</form>
<MessageContextMenu
  show={showContextMenu}
  position={contextMenuPosition}
  onReply={() => (repliedMessage = selectedMessage)}
  canEdit={$user?.id === selectedMessage?.userId}
  onEdit={() => {
    editedMessage = selectedMessage;
    messageText = editedMessage.message;
  }}
  canDelete={$user?.id === selectedMessage?.userId}
  onDelete={() => deleteMessage(selectedMessage.id)}
  onReaction={(reaction) => sendReaction(selectedMessage.id, reaction)}
/>
