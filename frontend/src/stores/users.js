import { derived, writable } from "svelte/store";

const users = writable([]);

export const userColors = derived(users, ($users) =>
  $users.reduce((acc, { id, color }) => ({ ...acc, [id]: color }), {})
);

export default users;
