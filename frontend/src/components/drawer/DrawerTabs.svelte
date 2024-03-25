<script>
  export let tabs;
  export let hidden;

  let activeTab = tabs[0];
</script>

<div class="flex flex-col h-full bg-base-200 pt-10 w-full lg:pt-0 lg:w-80">
  {#if !hidden}
    <div class="tabs tabs-boxed p-4 w-full flex">
      {#each tabs as tab}
        <a
          class={`tab flex-1 gap-1 px-1 ${
            tab === activeTab ? "tab-active" : ""
          }`}
          on:click={() => {
            activeTab = tab;
          }}
        >
          <svelte:component this={tab.icon} />
          {tab.caption}
        </a>
      {/each}
    </div>
  {/if}
  <svelte:component this={activeTab.component} />
</div>
