<script>
  import user from "../../stores/user";
  import ChangePasswordButton from "./settings/ChangePasswordButton.svelte";
  import ChangeUsernameButton from "./settings/ChangeUsernameButton.svelte";
  import UserColorSelect from "./settings/UserColorSelect.svelte";
</script>

<div class="flex flex-col gap-3 p-4 w-full">
  <ChangeUsernameButton />
  {#if $user.hasPassword}
    <ChangePasswordButton />
  {/if}
  <UserColorSelect />
</div>
