<script>
  import { fade } from "svelte/transition";
  import useApi from "../../../hooks/api";
  import user from "../../../stores/user";
  import USER_COLORS from "../../../utils/user-colors";

  const {
    authorized: { put },
  } = useApi();
  let settingColor;

  const setColor = async (color) => {
    settingColor = color;
    try {
      await put(`/api/users/me/color`, { color });
      $user = { ...$user, color };
    } catch (error) {
      console.error("could not change color");
    } finally {
      settingColor = null;
    }
  };
</script>

<div class="form-control rounded-xl bg-neutral-focus py-1 pb-3 px-3">
  <!-- svelte-ignore a11y-label-has-associated-control -->
  <label class="label">
    <span class="label-text">Your color</span>
  </label>
  <div class="flex justify-start items-center flex-wrap gap-2.5">
    {#each Object.entries(USER_COLORS) as [color, bg]}
      <button
        class={`flex justify-center items-center w-11 h-11 rounded-full ${bg} ${
          !settingColor && color === $user.color
            ? "border border-white border-opacity-30"
            : ""
        }`}
        on:click={() => setColor(color)}
      >
        {#if color === settingColor}
          <span class="loading loading-spinner text-white opacity-40" />
        {:else if color === $user.color}
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke-width="2"
            stroke="currentColor"
            class="w-6 h-6 text-white opacity-50"
            transition:fade
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M4.5 12.75l6 6 9-13.5"
            />
          </svg>
        {/if}
      </button>
    {/each}
  </div>
</div>
