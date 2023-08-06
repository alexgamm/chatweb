<script>
  import user from "../../../stores/user";
  import USER_COLORS from "../../../utils/user-colors";

  const setColor = (color) => {
    $user = { ...$user, color };
  };
</script>

<h2 class="mt-4 mb-2">Choose your color</h2>
<div class="flex justify-start items-center flex-wrap gap-2">
  {#each Object.entries(USER_COLORS) as [color, bg]}
    <button
      class={`w-12 h-12 rounded-full ${bg} ${
        color === $user.color ? "border border-gray-200" : ""
      }`}
      on:click={() => setColor(color)}
    />
  {/each}
</div>
