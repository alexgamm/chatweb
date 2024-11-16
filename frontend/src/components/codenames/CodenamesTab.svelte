<script>
  import { navigate } from "svelte-routing";
  import { fade } from "svelte/transition";
  import { COLORS, TEAM_NAMES } from "../../codenames/codenames-utils";
  import useApi from "../../hooks/api";
  import { game } from "../../stores/codenames";
  import { pushToast } from "../../stores/toast";
  import user from "../../stores/user";
  import LeaveIcon from "../icons/LeaveIcon.svelte";
  import PauseIcon from "../icons/PauseIcon.svelte";
  import PlayIcon from "../icons/PlayIcon.svelte";
  import RefreshIcon from "../icons/RefreshIcon.svelte";
  import SpectatorIcon from "../icons/SpectatorIcon.svelte";
  import StarIcon from "../icons/StarIcon.svelte";
  import TeamSelect from "./TeamSelect.svelte";

  const {
    authorized: { post },
  } = useApi();

  $: currentTeam = $game?.teams?.find(({ players }) =>
    players.find(({ userId }) => userId === $user?.id)
  );

  $: isLeader = currentTeam?.leader?.userId === $user?.id;
  $: isHost = $user?.id === $game?.host?.userId;
  $: canBecomeLeader = !currentTeam?.leader;

  let teams;

  $: if ($game) {
    teams = [...$game.teams, { color: "GRAY", players: $game.viewers }];
  }

  const isTeamLeader = (team, player) => player.userId === team?.leader?.userId;
  const sortTeamPlayers = (team) => (a, b) =>
    +isTeamLeader(team, b) - +isTeamLeader(team, a);

  const toggleGame = async () => {
    try {
      await post(
        `/api/codenames/game/${$game.id}/${$game.state.status === "ACTIVE" ? "pause" : "start"}`
      );
    } catch (error) {
      pushToast({ type: "error", message: error.message });
    }
  };

  const resetGame = async () => {
    try {
      await post(`/api/codenames/game/${$game.id}/reset`);
    } catch (error) {
      pushToast({ type: "error", message: error.message });
    }
  };

  const joinTeam = async (teamId, leader) => {
    await post(`/api/codenames/team/${teamId}/join`, { leader });
  };

  const leaveTeam = async () => {
    await post(`/api/codenames/team/${currentTeam.id}/leave`);
  };

  const toggleLeader = async () => {
    await joinTeam(currentTeam?.id, !isLeader);
  };
</script>

{#if $game && teams}
  <ul class="p-4 w-full text-base-content overflow-y-auto">
    <button
      class="btn btn-ghost btn-sm w-42 mb-2"
      on:click={() => navigate("/")}
    >
      <LeaveIcon />
      Leave room
    </button>
    {#if isHost}
      <div class="flex gap-2 mt-2 mb-6">
        <button
          class={`btn flex flex-col h-20 rounded-xl gap-3 flex-1 ${
            $game.state?.status === "ACTIVE" ? "btn-neutral" : "btn-primary"
          }`}
          on:click={() => toggleGame()}
        >
          {#if $game.state?.status !== "ACTIVE"}
            <PlayIcon />
            Play
          {:else}
            <PauseIcon />
            Pause
          {/if}
        </button>
        <button
          class="btn flex flex-col h-20 rounded-xl gap-3 flex-1 btn-neutral"
          on:click={() => resetGame()}
        >
          <RefreshIcon /> Change cards
        </button>
      </div>
    {/if}
    {#each teams as team}
      {#if team.players.length}
        <li class="mb-2">
          <h2 class={`text font-bold text-sm ${COLORS[team.color].text}`}>
            {TEAM_NAMES[team.color]}
          </h2>
        </li>
      {/if}
      {#each team.players.sort(sortTeamPlayers(team)) as player}
        <li class="mb-2" in:fade>
          <div
            class={`flex justify-between items-center py-2 pl-4 pr-3 rounded-lg text-sm ${
              COLORS[team.color].bg
            } bg-opacity-10`}
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
            <span class={`h-5 ${COLORS[team.color].text}`}>
              {#if team.color === "GRAY"}
                <SpectatorIcon />
              {:else if player.userId === team?.leader?.userId || canBecomeLeader}
                <button on:click={() => toggleLeader()}>
                  <StarIcon
                    variant={player.userId === team?.leader?.userId
                      ? "solid"
                      : ""}
                  />
                </button>
              {/if}
            </span>
          </div>
        </li>
      {/each}
    {/each}
  </ul>
  <div class="px-4">
    <TeamSelect
      teams={$game.teams}
      selectedTeamId={currentTeam?.id ?? "viewers"}
      onSelect={(team) => {
        if (team.id === "viewers") {
          leaveTeam();
        } else {
          joinTeam(team.id, false);
        }
      }}
    />
  </div>
{/if}
