let eventSource;
export const addEventHandler = (eventType, handler) => {
  if (!eventSource) {
    const accessToken = localStorage.getItem("token");
    if (!accessToken) {
      return;
    }
    eventSource = new EventSource(
      `/api/events/stream?access_token=${accessToken}`
    );
  }
  eventSource.addEventListener(eventType, (event) => {
    const eventData = JSON.parse(event.data);
    handler(eventData);
  });
};
