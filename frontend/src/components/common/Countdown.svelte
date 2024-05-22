<script>
  import { differenceInSeconds } from "date-fns";
  import { onMount } from "svelte";

  export let endsAt;
  export let pausedAt;
  export let durationSeconds;
  export let progress;

  let view = null;

  const formatDuration = (durationSeconds) => {
    const minutes = Math.floor(durationSeconds / 60);
    const seconds = durationSeconds % 60;
    return `${minutes}:${seconds < 10 ? "0" : ""}${seconds}`;
  };

  onMount(() => {
    const interval = setInterval(() => {
      let remainingSeconds;
      if (endsAt) {
        const now = Date.now();
        if (pausedAt) {
          remainingSeconds = differenceInSeconds(
            new Date(endsAt),
            new Date(pausedAt)
          );
        } else if (now > endsAt) {
          remainingSeconds = 0;
        } else {
          remainingSeconds = differenceInSeconds(new Date(endsAt), now);
        }
      } else {
        remainingSeconds = durationSeconds;
      }
      progress = remainingSeconds / durationSeconds;
      view = formatDuration(remainingSeconds);
    }, 200);

    return () => {
      clearInterval(interval);
    };
  });
</script>

{#if view !== null}
  {view}
{/if}
