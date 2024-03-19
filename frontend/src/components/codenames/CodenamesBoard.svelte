<script>
  import chunk from "lodash/chunk";
  import { COLORS } from "../../codenames/codenames-colors";

  import { game } from "../../stores/codenames";

  $: cardRows = $game ? chunk($game.state.cards, 5) : null;

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
</script>

{#if $game}
  <div class="flex justify-center flex-wrap bg-black bg-opacity-30 pb-6">
    <div class="w-full text-center p-5 pt-8">
      <h1 class="text-4xl font-bold">🔤 Codenames</h1>
    </div>
    <div
      class="grid grid-cols-5 gap-1 md:gap-4 max-w-5xl flex-1 p-4 justify-center"
    >
      {#each cardRows as row}
        {#each row as card}
          <div
            class={`rounded-lg text-gray-300 md:p-4 md:text-base py-2 break-words text-xs text-center hover:bg-opacity-60 transition cursor-pointer font-semibold ${cardColor(card)} bg-opacity-40`}
          >
            {card.word}
          </div>
        {/each}
      {/each}
    </div>
  </div>
{/if}
