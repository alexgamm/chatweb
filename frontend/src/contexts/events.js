import SockJS from "sockjs-client/dist/sockjs";
import Stomp from "stompjs";

let socket;
const handlers = {};
const subscribedTo = [];

const initSocket = () => {
  socket = new SockJS("/api/ws/events");
};

const connect = () =>
  new Promise((resolve, reject) => {
    const accessToken = localStorage.getItem("token");
    const headers = {};
    if (accessToken) {
      // TODO Handle expired token
      headers["Authorization"] = `Bearer ${accessToken}`;
    }
    const stomp = Stomp.over(socket);
    stomp.connect(
      headers,
      () => {
        resolve(stomp);
      },
      reject
    );
  });

const useEvents = (room) => {
  return {
    addEventHandler(eventType, handler) {
      const destination = `/events${room ? `/${room}` : ""}`;
      if (!socket) {
        initSocket();
      }
      if (!subscribedTo.includes(destination)) {
        subscribedTo.push(destination);
        connect().then((stomp) => {
          stomp.subscribe(destination, (message) => {
            const eventData = JSON.parse(message.body);
            const eventHandlers = handlers[eventData.type?.slice(1)] ?? [];
            eventHandlers.forEach((handler) => handler(eventData));
          });
        });
      }
      handlers[eventType] = [...(handlers[eventType] ?? []), handler];
    },
  };
};

export default useEvents;
