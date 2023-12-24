<script>
  import { navigate } from "svelte-routing";
  import { fade } from "svelte/transition";
  import LeaveIcon from "../icons/LeaveIcon.svelte";
  import PauseIcon from "../icons/PauseIcon.svelte";
  import PlayIcon from "../icons/PlayIcon.svelte";
  import RefreshIcon from "../icons/RefreshIcon.svelte";
  import SpectatorIcon from "../icons/SpectatorIcon.svelte";
  import StarIcon from "../icons/StarIcon.svelte";
  import { COLORS } from "../../codenames/codenames-colors";
  import TeamSelect from "./TeamSelect.svelte";
  import te from "date-fns/locale/te";

  const game = {
    teams: [
      {
        name: "blue",
        color: "BLUE",
        players: [
          {
            username: "Vlad",
            host: true,
          },
          {
            username: "Heehehehe",
          },
        ],
      },
      {
        name: "fuchsia",
        color: "FUCHSIA",
        players: [
          {
            username: "Sasha",
            host: true,
          },
          {
            username: "ASdgdf234dsfg",
          },
        ],
      },
      {
        name: "spectators",
        color: "GRAY",
        players: [
          {
            username: "Someone",
          },
        ],
      },
    ],
  };

  let selectedTeam = game.teams[0];
</script>

<ul class="p-4 w-full text-base-content overflow-y-auto">
  <button class="btn btn-ghost btn-sm w-42 mb-2" on:click={() => navigate("/")}>
    <LeaveIcon />
    Leave room
  </button>
  <div class="flex gap-2 mt-2 mb-6">
    <button
      class={`btn flex flex-col h-20 rounded-xl gap-3 flex-1 ${
        game.state === "live" ? "btn-neutral" : "btn-primary"
      }`}
      on:click={() => {
        game.state = game.state === "live" ? "paused" : "live";
      }}
    >
      {#if game.state !== "live"}
        <PlayIcon />
        Play
      {:else}
        <PauseIcon />
        Pause
      {/if}
    </button>
    <button class="btn flex flex-col h-20 rounded-xl gap-3 flex-1 btn-neutral">
      <RefreshIcon /> Change cards
    </button>
  </div>
  {#each game.teams as team}
    {#each team.players as player}
      <li class="mb-2" in:fade>
        <a
          class={`flex justify-between items-center py-2 pl-4 pr-3 rounded-lg text-sm ${
            COLORS[team.color].bg
          } bg-opacity-10`}
          href="#"
        >
          <div class="flex justify-between items-center gap-2 h-4">
            <strong class={player.typing ? "animate-pulse" : ""}>
              {player.username}
            </strong>
            {#if player.typing}
              <span
                class="loading loading-dots loading-md text-gray-300 animate-pulse opacity-75"
              />
            {/if}
          </div>
          <span class={`${COLORS[team.color].text}`}>
            {#if team.name === "spectators"}
              <SpectatorIcon />
            {:else}
              <StarIcon variant={player.host ? "solid" : ""} />
            {/if}
          </span>
        </a>
      </li>
    {/each}
  {/each}
</ul>
<div class="px-4">
  <TeamSelect
    teams={game.teams}
    {selectedTeam}
    onSelect={(team) => (selectedTeam = team)}
  />
</div>
