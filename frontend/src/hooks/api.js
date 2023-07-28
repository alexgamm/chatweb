import { navigate } from "svelte-routing";

const useApi = () => {
  const accessToken = localStorage.getItem("token");
  const request = async (url, { method, body, useToken }) => {
    const headers = { "Content-Type": "application/json;charset=UTF8" };
    if (useToken) {
      headers["Authorization"] = `Bearer ${accessToken}`;
    }
    const fetchParams = { method, headers };
    if (body) {
      fetchParams["body"] = JSON.stringify(body);
    }
    const response = await fetch(url, fetchParams);
    const responseBody = await response.json();
    if (!response.ok) {
      if(response.status === 401){
        navigate("/login");
      }
      throw responseBody.message;
    }
    return responseBody;
  };
  return {
    post: (url, body) =>
      request(url, { method: "POST", body, useToken: false }),
    authorized: {
      get: (url) => request(url, { method: "GET", body: null, useToken: true }),
      post: (url, body) =>
        request(url, { method: "POST", body, useToken: true }),
      patch: (url, body) =>
        request(url, { method: "PATCH", body, useToken: true }),
    },
  };
};
export default useApi;
