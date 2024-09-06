import SockJS from "sockjs-client/dist/sockjs";
import Stomp from "stompjs"

let socket;
const handlers = {};

export const addEventHandler = (eventType, handler) => {
  if (!socket) {
    // TODO Handle expired token
    const accessToken = localStorage.getItem("token");
    if (!accessToken) {
      return;
    }
    socket = new SockJS('/api/ws/events');
    const stomp = Stomp.over(socket)
    stomp.connect({
      Authorization: `Bearer ${accessToken}`
    }, () => {
      stomp.subscribe('/events', message => {
        console.log('Event', message);
        const eventData = JSON.parse(message.body);
        const eventHandlers = handlers[eventData.type?.slice(1)] ?? [];
        eventHandlers.forEach(handler => handler(eventData));
      })
    })
  }
  handlers[eventType] = [...(handlers[eventType] ?? []), handler];
};