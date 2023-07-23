const useMessageColor = () => {
  const MESSAGES_COLORS = [
    "bg-primary",
    "bg-indigo-700",
    "bg-purple-800",
    "bg-indigo-800",
  ];
  const randomMessageColor = () => {
    return MESSAGES_COLORS[Math.floor(Math.random() * MESSAGES_COLORS.length)];
  };

  const rainbowMessageColor = () => {
    return "bg-gradient-to-r from-orange-500 from-1% via-amber-400-accent via-50% to-purple-700 to-100%";
  };
  const addColor = (message) => {
    return {
      ...message,
      color:
        message.message.indexOf("гей") !== -1 ||
        message.message.indexOf("gay") !== -1
          ? rainbowMessageColor()
          : randomMessageColor(),
    };
  };
  return addColor;
};
export default useMessageColor;
