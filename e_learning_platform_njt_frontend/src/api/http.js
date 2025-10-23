import axios from "axios";

const API_URL = process.env.REACT_APP_API_URL || "http://localhost:8080/api";

const http = axios.create({
    baseURL: API_URL,
    headers: { "Content-Type": "application/json" },
    withCredentials: false,
});

http.interceptors.request.use((config) => {
    const token = typeof window !== "undefined" ? localStorage.getItem("token") : null;

    const url = config.url || "";
    const skipAuthHeader = /^\/auth\/(login|register)\b/.test(url);
    if (token && !skipAuthHeader) {
        config.headers.Authorization = `Bearer ${token}`;
    } else {
        delete config.headers.Authorization;
    }
    return config;
});

// 401/403 handler
http.interceptors.response.use(
    (res) => res,
    (error) => {
        const status = error?.response?.status;
        const url = error?.config?.url || "";

        // Ako je 401 na login/register -> ne radi logout ni redirect
        const isLoginOrRegister = /^\/auth\/(login|register)\b/.test(url);

        if (status === 401 && !isLoginOrRegister) {
            try {
                localStorage.removeItem("token");
                localStorage.removeItem("user");
            } catch { }
            if (typeof window !== "undefined" && window.location.pathname !== "/login") {
                window.location.href = "/login";
            }
        }
        return Promise.reject(error);
    }
);

export default http;
