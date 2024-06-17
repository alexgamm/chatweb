let socket;
const handlers = {};
const baseUrl = `${location.protocol === 'https:' ? 'wss' : 'ws'}://${location.host}`;

export const addEventHandler = (eventType, handler) => {
  if (!socket) {
    const accessToken = localStorage.getItem("token");
    if (!accessToken) {
      return;
    }
    // TODO Handle expired token
    socket = new WebSocket(`${baseUrl}/api/ws/events?access_token=${accessToken}`);
    socket.addEventListener("message", (event) => {
      if (event.data === "PING") return;
      const eventData = JSON.parse(event.data);
      const eventHandlers = handlers[eventData.type?.slice(1)] ?? [];
      eventHandlers.forEach(handler => handler(eventData));
    });
  }
  handlers[eventType] = [...(handlers[eventType] ?? []), handler];
};