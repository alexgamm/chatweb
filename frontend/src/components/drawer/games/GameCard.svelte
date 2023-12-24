<script>
  export let title;
  export let description;
  export let className;
  export let onClick;
</script>

<a
  class={`rounded-xl shadow-md p-4 bg-opacity-10 cursor-pointer hover:bg-opacity-5 transition ${
    className ?? ""
  }`}
  on:click={onClick}
>
  <h2 class="text-lg font-bold mb-2">{title}</h2>
  <p class="text-gray-400 text-xs">{description}</p>
</a>
