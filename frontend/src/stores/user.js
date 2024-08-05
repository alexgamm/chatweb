import { writable, get as getValue } from "svelte/store";
import useApi from "../hooks/api";
import wait from "../utils/wait";

const user = writable(null);
export const getUser = async () => {
  const {
    authorized: { get },
  } = useApi();

  try {
    const resp = await get("/api/users/me");
    if (!getValue(user) && !localStorage.getItem("disableSplash")) {
      await wait(1000);
    }
    user.set(resp);
  } catch (error) {
    console.error("could not get user", error); // TODO handle properly
  }
};

export default user;
