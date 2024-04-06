<script>
  import chunk from "lodash/chunk";
  import { COLORS } from "../../codenames/codenames-colors";

  import useApi from "../../hooks/api";
  import { game } from "../../stores/codenames";
  import Countdown from "../common/Countdown.svelte";

  let turnProgress;

  const {
    authorized: { post },
  } = useApi();

  $: cardRows = $game ? chunk($game.state.cards, 5) : null;
  $: turnTeam = $game?.teams?.find(
    ({ id }) => id === $game?.state?.turn?.teamId
  );
  $: turnStartedAt = $game?.state?.turn?.startedAt
    ? new Date($game.state.turn.startedAt)
    : null;

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
    await post(`/api/codenames/game/${$game.id}/cards/pick`, {
      word: card.word,
    });
  };
</script>

{#if $game}
  <div class="bg-black bg-opacity-30 pb-6">
    <div class="text-center p-5 pt-8">
      <h1 class="text-4xl font-bold">🔤 Codenames</h1>
    </div>
    <div class="flex flex-col items-center">
      <div
        class="w-full grid grid-cols-5 gap-1 md:gap-4 max-w-5xl flex-1 p-4 justify-center"
      >
        {#each cardRows as row}
          {#each row as card}
            <button
              class={`rounded-lg text-gray-300 md:p-4 md:text-base py-2 break-words text-xs text-center hover:bg-opacity-60 transition cursor-pointer font-semibold ${cardColor(card)} bg-opacity-40`}
              on:click={() => pickCard(card)}
            >
              {card.word}
            </button>
          {/each}
        {/each}
      </div>
      <div
        class="w-full grid grid-cols-3 gap-1 md:gap-4 max-w-5xl flex-1 p-4 justify-center mt-4"
      >
        <div
          class={`rounded-xl md:p-4 md:text-3xl py-2 break-words text-2xl text-center font-bold bg-opacity-30 ${COLORS[$game.teams[0].color].text} ${COLORS[$game.teams[0].color].bg}`}
        >
          5
        </div>
        <div
          class={`relative rounded-xl md:p-4 md:text-3xl py-2 break-words text-2xl text-center font-semibold bg-opacity-30 bg-gray-700 overflow-hidden ${COLORS[turnTeam.color].text}`}
        >
          {#if $game.state.turn}
            <span style={`opacity: ${Math.max(0.3, turnProgress * 2)}`}>
              <Countdown
                start={turnStartedAt}
                durationSeconds={$game.state.turn.durationSeconds}
                bind:progress={turnProgress}
              />
            </span>
            <div
              class={`absolute top-0 left-0 h-full bg-opacity-30 duration-200 ${COLORS[turnTeam.color].bg}`}
              style={`width: ${(turnProgress * 100).toFixed(10)}%;`}
            ></div>
          {/if}
        </div>
        <div
          class={`rounded-xl md:p-4 md:text-3xl py-2 break-words text-2xl text-center font-bold bg-opacity-30 ${COLORS[$game.teams[1].color].text} ${COLORS[$game.teams[1].color].bg}`}
        >
          2
        </div>
      </div>
    </div>
  </div>
{/if}
