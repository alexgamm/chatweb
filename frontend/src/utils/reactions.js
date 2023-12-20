const REACTIONS = ["❤️", "🚑", "🤔", "😂", "🤬"];

export const reactionsOrder = (a, b) =>
  REACTIONS.indexOf(a.reaction) - REACTIONS.indexOf(b.reaction);

export const toggleReaction = (reactions, reaction) => {
  if (reactions?.find((r) => r.reaction === reaction)) {
    return reactions
      .map((r) =>
        r.reaction === reaction
          ? { ...r, count: r.count + (r.hasOwn ? -1 : 1), hasOwn: !r.hasOwn }
          : r
      )
      .filter(({ count }) => count > 0);
  } else {
    return [{ reaction, count: 1, hasOwn: true }, ...reactions];
  }
};

export default REACTIONS;
