import axios from "axios";

export const api = axios.create({ baseURL: "/api" });

api.interceptors.request.use((config) => {
  const token = sessionStorage.getItem("token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const url: string = error.config?.url ?? "";
    const isLogin = url.includes("/auth/") || url.includes("/dev/");
    if (error.response?.status === 401 && !isLogin) {
      sessionStorage.clear();
      window.location.href = "/login";
    }
    return Promise.reject(error);
  },
);
