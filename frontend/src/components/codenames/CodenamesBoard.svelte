<script>
  import chunk from "lodash/chunk";
  import { COLORS, TEAM_NAMES } from "../../codenames/codenames-utils";

  import useApi from "../../hooks/api";
  import { game } from "../../stores/codenames";
  import { pushToast } from "../../stores/toast";
  import user from "../../stores/user";
  import Countdown from "../common/Countdown.svelte";
  import SparksIcon from "../icons/SparksIcon.svelte";
  import TapIcon from "../icons/TapIcon.svelte";

  let turnProgress;

  const {
    authorized: { post },
  } = useApi();

  $: cardRows = $game ? chunk($game.state.cards, 5) : null;
  $: turnTeam = $game?.teams?.find(
    ({ id }) => id === $game?.state?.turn?.teamId
  );

  $: currentTeam = $game?.teams?.find(({ players }) =>
    players.find(({ userId }) => userId === $user?.id)
  );

  $: isLeader = currentTeam?.leader?.userId === $user?.id;

  $: isUserTurn =
    $game?.state?.turn?.teamId === currentTeam?.id &&
    isLeader === $game?.state?.turn?.leader;

  $: wonTeam =
    $game?.state?.status === "FINISHED" &&
    $game.teams.find(({ id }) => !$game.state.lostTeamIds?.includes(id));

  const cardColor = (card) => {
    switch (card.type) {
      case "NEUTRAL":
        return "bg-gray-400";
      case "BLACK":
        return "bg-gray-700";
      case "COLOR":
        const team = $game.teams.find(({ id }) => id === card.teamId);
        return COLORS[team.color].bg;
      default:
        return "bg-gray-800";
    }
  };

  const pickCard = async (card) => {
    try {
      await post(`/api/codenames/game/${$game.id}/cards/pick`, {
        word: card.word,
      });
    } catch (error) {
      pushToast({ type: "error", message: error.message });
    }
  };

  const endTurn = async () => {
    try {
      await post(`/api/codenames/game/${$game.id}/turn/end`);
    } catch (error) {
      pushToast({ type: "error", message: error.message });
    }
  };
</script>

{#if $game}
  <div class="bg-black bg-opacity-30 pb-6">
    <div class="flex flex-col items-center">
      <div
        class="w-full mt-6 grid grid-cols-5 gap-1 md:gap-4 max-w-5xl flex-1 p-4 justify-center"
      >
        {#each cardRows as row}
          {#each row as card}
            <button
              class={`word-card ${cardColor(card)} ${card.pickedByTeamId ? "bg-opacity-60" : "bg-opacity-30"} ${$game?.settings?.dictionaryId === "emoji" ? "word-card--emoji" : ""}`}
              on:click={() => pickCard(card)}
            >
              {card.word}
            </button>
          {/each}
        {/each}
      </div>
      <div
        class="w-full grid grid-cols-3 gap-1 md:gap-4 max-w-5xl flex-1 p-4 justify-center"
      >
        <div
          class={`team-remaining-cards-counter ${COLORS[$game.teams[0].color].text} ${COLORS[$game.teams[0].color].bg}`}
        >
          {$game?.state?.remainingCards?.[$game.teams[0].id] ?? ""}
        </div>
        {#if wonTeam}
          <div
            class={`won-team ${COLORS[wonTeam.color].bg} ${COLORS[wonTeam.color].text}`}
          >
            <SparksIcon />
            {TEAM_NAMES[wonTeam.color]} won!
          </div>
        {:else}
          <button
            class={`turn-progress ${COLORS[turnTeam.color].text} ${isUserTurn ? "hover:bg-opacity-10" : "cursor-default"}`}
            on:click={() => isUserTurn && endTurn()}
          >
            {#if $game.state.turn}
              <div
                class="pointer-events-none"
                style={`opacity: ${Math.max(0.3, turnProgress * 2)}`}
              >
                <Countdown
                  endsAt={$game.state.turn.timeoutAt}
                  pausedAt={$game.state.turn.pausedAt}
                  durationSeconds={$game.state.turn.durationSeconds}
                  bind:progress={turnProgress}
                />
              </div>
              <div
                class="flex justify-center gap-1 md:text-base text-xs opacity-70"
              >
                {#if isUserTurn}
                  <TapIcon />
                  Tap to end the turn
                {:else if $game.state.turn.leader}
                  It's {TEAM_NAMES[turnTeam.color]} leader's turn
                {:else}
                  It's {TEAM_NAMES[turnTeam.color]} players' turn
                {/if}
              </div>
              <div
                class={`absolute top-0 left-0 h-full bg-opacity-30 duration-200 ${COLORS[turnTeam.color].bg}`}
                style={`width: ${(turnProgress * 100).toFixed(10)}%;`}
              ></div>
            {/if}
          </button>
        {/if}
        <div
          class={`team-remaining-cards-counter ${COLORS[$game.teams[1].color].text} ${COLORS[$game.teams[1].color].bg}`}
        >
          {$game?.state?.remainingCards?.[$game.teams[1].id] ?? ""}
        </div>
      </div>
    </div>
  </div>
{/if}

<style lang="postcss">
  .word-card {
    @apply rounded-lg text-gray-300 md:p-4 md:text-base py-2 break-words text-xs text-center hover:bg-opacity-70 transition cursor-pointer font-semibold;
  }
  .word-card--emoji {
    @apply text-2xl;
  }
  .won-team {
    @apply flex justify-center items-center gap-2 rounded-xl md:p-3 md:py-1 md:text-2xl py-1 break-words text-xl text-center duration-100 font-bold bg-opacity-30 overflow-hidden;
  }
  .turn-progress {
    @apply relative flex-col items-center gap-0 rounded-xl md:p-3 md:py-1 md:text-3xl py-1 break-words text-xl text-center duration-100 font-bold bg-opacity-30 bg-gray-700 overflow-hidden;
  }
  .team-remaining-cards-counter {
    @apply flex justify-center items-center rounded-xl md:p-3 md:text-3xl py-1 break-words text-xl text-center font-bold bg-opacity-30;
  }
</style>
