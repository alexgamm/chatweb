<script>
  import SettingsTab from "./drawer/SettingsTab.svelte";
  import UsersTab from "./drawer/UsersTab.svelte";
  const tabs = [
    {
      caption: "Users",
      component: UsersTab,
    },
    {
      caption: "Settings",
      component: SettingsTab,
    },
  ];
  let activeTab = tabs[0];
</script>

<div class="flex flex-col h-full bg-base-200 pt-10 w-full lg:pt-0 lg:w-80">
  <div class="tabs tabs-boxed p-4 w-full flex">
    {#each tabs as tab}
      <a
        class={`tab flex-1 ${tab === activeTab ? "tab-active" : ""}`}
        on:click={() => {
          activeTab = tab;
        }}>{tab.caption}</a
      >
    {/each}
  </div>
  <svelte:component this={activeTab.component} />
</div>
