import { writable } from "svelte/store";
import useApi from "../hooks/api";

const user = writable(null);
export const getUser = async () => {
  const {
    authorized: { get },
  } = useApi();

  try {
    user.set(await get("/api/users/me"));
  } catch {
    alert("could not get user"); // TODO handle properly
  }
};

export default user;
