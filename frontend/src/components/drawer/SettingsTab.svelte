<script>
  import user from "../../stores/user";
  import ColorPanel from "./settings/ColorPanel.svelte";
  import LogoutButton from "./settings/LogoutButton.svelte";
  import PasswordPanel from "./settings/PasswordPanel.svelte";
  import UsernamePanel from "./settings/UsernamePanel.svelte";
</script>

<div class="flex flex-col justify-between h-full">
  <div class="flex flex-col gap-3 p-4 w-full">
    <UsernamePanel />
    {#if $user.hasPassword}
      <PasswordPanel />
    {/if}
    <ColorPanel />
  </div>
  <div class="flex flex-col gap-3 p-4 w-full">
    <LogoutButton />
  </div>
</div>
