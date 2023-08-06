import { writable } from "svelte/store";
import wait from "../utils/wait";

const useHeavyList = (initial = [], delay = 50) => {
  const value = writable(initial);
  const set = async (list) => {
    const newValue = [];
    for (let idx = 0; idx < list.length; idx++) {
      if (idx) {
        await wait(delay);
      }
      newValue.push(list[idx]);
      value.set(newValue);
    }
  };
  const update = (filter, updater) =>
    value.update((list) => list.map((e) => (filter(e) ? updater(e) : e)));
  return [value, set, update];
};

export default useHeavyList;
