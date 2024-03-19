<script>
  import { COLORS } from "../../codenames/codenames-colors";
  import SpectatorIcon from "../icons/SpectatorIcon.svelte";
  import UserIcon from "../icons/UserIcon.svelte";

  export let teams;
  export let selectedTeamId;
  export let onSelect;

  $: selectItems = [...teams, { id: "viewers", color: "GRAY" }];

  let selectingTeam;

  const selectTeam = async (team) => {
    selectingTeam = team;
    try {
      await onSelect(team);
    } catch (error) {
      console.error("could not select team");
    } finally {
      selectingTeam = null;
    }
  };
</script>

<h2 class="mb-2">Select your team</h2>
<div class="flex justify-start items-center flex-wrap gap-3">
  {#each selectItems as team}
    <button
      class={`flex justify-center items-center w-12 h-12 rounded-full ${
        COLORS[team.color].text
      } ${COLORS[team.color].bg} ${
        !selectingTeam && team.id === selectedTeamId
          ? "border-white border-opacity-50 bg-opacity-80"
          : "bg-opacity-50 opacity-60"
      }`}
      on:click={() => selectTeam(team)}
    >
      {#if team === selectingTeam}
        <span class="loading loading-spinner text-white opacity-40" />
      {:else if team.id === selectedTeamId}
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke-width="2"
          stroke="currentColor"
          class="w-6 h-6"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M4.5 12.75l6 6 9-13.5"
          />
        </svg>
      {:else if team.id === "viewers"}
        <SpectatorIcon size="lg" />
      {:else}
        <UserIcon />
      {/if}
    </button>
  {/each}
</div>
