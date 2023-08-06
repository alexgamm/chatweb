import { writable } from "svelte/store";
import useApi from "../hooks/api";
import USER_COLORS from "../utils/user-colors";

const user = writable(null);
export const getUser = async () => {
  const {
    authorized: { get },
  } = useApi();

  try {
    const resp = await get("/api/users/me");
    user.set({ ...resp, color: Object.keys(USER_COLORS)[0] });
  } catch (error) {
    console.error("could not get user", error); // TODO handle properly
  }
};

export default user;
