import SockJS from "sockjs-client/dist/sockjs";
import Stomp from "stompjs";

let socket;
let connectedStomp;
const handlers = {};
const subscriptions = {};

const initSocket = () => {
  socket = new SockJS("/api/ws/events");
};

const connect = () =>
  new Promise((resolve, reject) => {
    if (connectedStomp) {
      resolve(connectedStomp);
      return;
    }
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
        connectedStomp = stomp;
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
      if (!subscriptions[destination]) {
        subscriptions[destination] = (message) => {
          const eventData = JSON.parse(message.body);
          const eventHandlers = handlers[eventData.type?.slice(1)] ?? [];
          eventHandlers.forEach((handler) => handler(eventData));
        };
        connect().then((stomp) => {
          stomp.subscribe(destination, subscriptions[destination]);
        });
      }
      handlers[eventType] = [...(handlers[eventType] ?? []), handler];
    },
  };
};

export default useEvents;
