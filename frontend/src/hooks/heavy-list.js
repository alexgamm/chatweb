import wait from "../utils/wait";

const useHeavyPush = (delay = 50) => {
  async function* heavyPush(list, elements) {
    for (let idx = 0; idx < elements.length; idx++) {
      if (idx) {
        await wait(delay);
      }
      list.push(elements[idx]);
      yield;
    }
  }
  return heavyPush;
};

export default useHeavyPush;
