import user from "../stores/user";
import USER_COLORS from "../utils/user-colors";

const useMessageColor = () => {
  const userColors = {};
  user.subscribe((user) => {
    if (user) {
      userColors[user.id] = USER_COLORS[user.color];
    }
  });

  const randomMessageColor = () => {
    const colors = Object.values(USER_COLORS);
    return colors[Math.floor(Math.random() * colors.length)];
  };

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
      if (!userColors[message.userId]) {
        userColors[message.userId] = randomMessageColor();
      }
      return userColors[message.userId];
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
