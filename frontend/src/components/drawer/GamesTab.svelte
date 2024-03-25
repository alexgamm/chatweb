<script>
  import BackIcon from "../icons/BackIcon.svelte";
  import GameCard from "./games/GameCard.svelte";
  import PlayGameForm from "./games/PlayGameForm.svelte";

  const GAMES = [
    {
      title: "🔤 Codenames",
      description: `Word game for two teams of at least two players. 
    Players take turns giving single word clues that can point to multiple words on the board. 
    Their teammates try to guess words of the right color while avoiding those that belong to the opposing team.`,
      className: "bg-blue-500",
    },
  ];

  let selectedGame;
</script>

<div class="flex flex-col gap-3 p-4 w-full">
  {#if selectedGame}
    <button
      class="btn btn-ghost btn-sm w-24"
      on:click={() => (selectedGame = null)}
    >
      <BackIcon />
      Back
    </button>
    <PlayGameForm title={selectedGame.title} />
  {:else}
    {#each GAMES as game}
      <GameCard
        title={game.title}
        description={game.description}
        className={game.className}
        onClick={() => (selectedGame = game)}
      />
    {/each}
  {/if}
</div>
