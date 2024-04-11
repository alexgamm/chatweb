<script>
  import { navigate } from "svelte-routing";
  import { fade } from "svelte/transition";
  import { COLORS } from "../../codenames/codenames-colors";
  import useApi from "../../hooks/api";
  import { game } from "../../stores/codenames";
  import user from "../../stores/user";
  import LeaveIcon from "../icons/LeaveIcon.svelte";
  import PauseIcon from "../icons/PauseIcon.svelte";
  import PlayIcon from "../icons/PlayIcon.svelte";
  import RefreshIcon from "../icons/RefreshIcon.svelte";
  import SpectatorIcon from "../icons/SpectatorIcon.svelte";
  import StarIcon from "../icons/StarIcon.svelte";
  import TeamSelect from "./TeamSelect.svelte";
  import ca from "date-fns/locale/ca";
  import { pushToast } from "../../stores/toast";

  const {
    authorized: { post },
  } = useApi();

  $: currentTeam = $game?.teams?.find(({ players }) =>
    players.find(({ userId }) => userId === $user?.id)
  );

  $: isLeader = currentTeam?.leader?.userId === $user?.id;

  let teams;

  $: if ($game) {
    teams = [...$game.teams, { color: "GRAY", players: $game.viewers }];
  }

  const toggleGame = async () => {
    try {
      await post(
        `/api/codenames/game/${$game.id}/${$game.state.status === "ACTIVE" ? "pause" : "start"}`
      );
    } catch (error) {
      pushToast({ type: "error", message: error.message });
    }
  };

  const restartGame = async () => {
    await post(`/api/codenames/game/${$game.id}/restart`);
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
    <div class="flex gap-2 mt-2 mb-6">
      <button
        class={`btn flex flex-col h-20 rounded-xl gap-3 flex-1 ${
          $game.state.status === "ACTIVE" ? "btn-neutral" : "btn-primary"
        }`}
        on:click={() => toggleGame()}
      >
        {#if $game.state.status !== "ACTIVE"}
          <PlayIcon />
          Play
        {:else}
          <PauseIcon />
          Pause
        {/if}
      </button>
      <button
        class="btn flex flex-col h-20 rounded-xl gap-3 flex-1 btn-neutral"
        on:click={() => restartGame()}
      >
        <RefreshIcon /> Change cards
      </button>
    </div>
    {#each teams as team}
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
            <span class={`h-5 ${COLORS[team.color].text}`}>
              {#if team.name === "spectators"}
                <SpectatorIcon />
              {:else}
                <button on:click={() => toggleLeader()}>
                  <StarIcon
                    variant={team.leader?.userId === player.userId
                      ? "solid"
                      : ""}
                  />
                </button>
              {/if}
            </span>
          </a>
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
