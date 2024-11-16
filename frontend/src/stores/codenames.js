import { writable } from "svelte/store";
import useApi from "../hooks/api";

export const game = writable(null);

export const joinGame = async (gameId) => {
  const {
    authorized: { post },
  } = useApi();
  try {
    await post(`/api/codenames/game/${gameId}/join`);
  } catch (err) {
    console.error("could not join game", err);
  }
};

export const fetchGame = async (gameId) => {
  const {
    authorized: { get },
  } = useApi();
  let response;
  try {
    response = await get(`/api/codenames/game/${gameId}`);
  } catch (err) {
    console.error("could not fetch game", err);
    return;
  }
  game.set(response);
  return response;
};

export const fetchGameState = async (gameId) => {
  const {
    authorized: { get },
  } = useApi();
  let response;
  try {
    response = await get(`/api/codenames/game/${gameId}/state`);
  } catch (err) {
    console.error("could not fetch game", err);
    return;
  }
  game.update((old) => ({ ...old, state: response }));
  return response;
};

export const dropGame = () => game.set(null);
