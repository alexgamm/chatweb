<script>
  export let game;
</script>

<div class="flex justify-center flex-wrap bg-black bg-opacity-30 pb-6">
  <div class="w-full text-center p-5 pt-8">
    <h1 class="text-4xl font-bold">🔤 Codenames</h1>
  </div>
  <div class="grid grid-cols-5 gap-1 md:gap-4 max-w-5xl flex-1 p-4 justify-center">
    {#each game.words as row}
      {#each row as word}
        <div class="rounded-lg bg-gray-800 md:p-4 md:text-base py-2 break-words text-xs text-center hover:bg-opacity-50 transition cursor-pointer font-bold">{word}</div>
      {/each}
    {/each}
  </div>
</div>
