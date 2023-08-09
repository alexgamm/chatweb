<script>
  import ChangePasswordButton from "./settings/ChangePasswordButton.svelte";
  import ChangeUsernameButton from "./settings/ChangeUsernameButton.svelte";
  import UserColorSelect from "./settings/UserColorSelect.svelte";
</script>

<div class="flex flex-col gap-3 p-4 w-full" data-tab="settings">
  <ChangeUsernameButton />
  <ChangePasswordButton />
  <UserColorSelect />
</div>
