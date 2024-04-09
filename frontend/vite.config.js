import { defineConfig } from "vite";
import { svelte } from "@sveltejs/vite-plugin-svelte";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [svelte()],
  server: {
    host: "0.0.0.0",
    proxy: {
      "/api/login": "http://localhost:23456",
      "/api/registration": "http://localhost:23456",
      "/api/verification": "http://localhost:23456",
      "/api/users": "http://localhost:23456",
      "/api/tg/oauth": "http://localhost:23456",
      "/api/google/oauth": "http://localhost:23456",
      "/api/messages": "http://localhost:23457",
      "/api/ws/events": {
        target: "http://localhost:23458",
        ws: true
      },
    },
  },
});
