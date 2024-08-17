import { defineConfig } from "vite";
import { svelte } from "@sveltejs/vite-plugin-svelte";

const PROXY_CONFIGS = {
  development: {
    "/api/login": "http://localhost:23456",
    "/api/registration": "http://localhost:23456",
    "/api/verification": "http://localhost:23456",
    "/api/reset-password": "http://localhost:23456",
    "/api/users": "http://localhost:23456",
    "/api/tg/oauth": "http://localhost:23456",
    "/api/google/oauth": "http://localhost:23456",
    "/api/messages": "http://localhost:23457",
    "/api/rooms": "http://localhost:23457",
    "/api/ws/events": {
      target: "http://localhost:23458",
      ws: true,
    },
    "/api/codenames": "http://localhost:23459",
  },
  production: {
    "/api": "https://chilldrill.fun",
    "/api/ws/events": {
      target: "https://chilldrill.fun",
      changeOrigin: true,
      secure: false,
      ws: true,
    },
  },
};

// https://vitejs.dev/config/
export default defineConfig(({ command, mode }) => {
  return {
    plugins: [svelte()],
    server: {
      host: "0.0.0.0",
      proxy: PROXY_CONFIGS[mode] ?? PROXY_CONFIGS["development"],
    },
  };
});
