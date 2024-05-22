import { COLORS } from "../codenames/codenames-utils";
import USER_COLORS from "../utils/user-colors";

const useMessageColor = (userColors, game) => {
  const rainbowMessageColor = () => {
    return "bg-gradient-to-r from-orange-500 from-1% via-amber-400-accent via-50% to-purple-700 to-100%";
  };
  const isGay = (message) =>
    message.message.toLowerCase().includes("гей") ||
    message.message.toLowerCase().includes("gay");
  const color = (message) => {
    if (isGay(message)) {
      return rainbowMessageColor();
    } else {
      if (game && game.id === message.room) {
        return COLORS[
          game.teams.find(({ players }) =>
            players.find(({ userId }) => userId === message.userId)
          )?.color ?? "GRAY"
        ].bg;
      } else {
        return USER_COLORS[userColors[message.userId]];
      }
    }
  };
  const addColor = (message) => {
    return {
      ...message,
      color: color(message),
    };
  };
  return addColor;
};
export default useMessageColor;
