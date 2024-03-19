let eventSource;
export const listen = () => {
  if (!eventSource) {
    const accessToken = localStorage.getItem("token");
    if (!accessToken) {
      return;
    }
    eventSource = new EventSource(
      `/api/events/stream?access_token=${accessToken}`
    );
  }
};
export const addEventHandler = (eventType, handler) => {
  listen();
  if (!eventSource) return;
  eventSource.addEventListener(eventType, (event) => {
    if (!event.data) return;
    const eventData = JSON.parse(event.data);
    handler(eventData);
  });
};

addEventHandler("error", ({ statusCode }) => {
  if (statusCode === 401) {
    localStorage.removeItem("token");
    eventSource.close();
    eventSource = null;
  }
});
