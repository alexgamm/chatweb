const useDebounce = (delay = 300) => {
  let timeout;
  return (fn) => {
    if (timeout) {
      clearTimeout(timeout);
      timeout = setTimeout(fn, delay);
    } else {
      fn();
      timeout = setTimeout(() => {}, delay);
    }
  };
};

export default useDebounce;
