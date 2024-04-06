<script>
  import { intervalToDuration } from "date-fns";
  import { onMount } from "svelte";

  export let start;
  export let durationSeconds;
  export let progress;

  let view = null;

  onMount(() => {
    const interval = setInterval(() => {
      let duration;
      if (start) {
        const now = Date.now();
        const end = start.getTime() + durationSeconds * 1000;
        if (now > end) {
          duration = {
            minutes: 0,
            seconds: 0,
          };
          progress = 0;
        } else {
          progress = Math.max(
            0,
            Math.min(1, (end - now) / durationSeconds / 1000)
          );
          duration = intervalToDuration({ start: now, end });
        }
      } else {
        duration = {
          minutes: Math.floor(durationSeconds / 60),
          seconds: durationSeconds % 60,
        };
        progress = 1;
      }
      view = `${duration.minutes}:${duration.seconds < 10 ? "0" : ""}${duration.seconds}`;
    }, 200);

    return () => {
      clearInterval(interval);
    };
  });
</script>

{#if view !== null}
  {view}
{/if}
