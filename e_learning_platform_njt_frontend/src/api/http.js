// src/api/http.js
import axios from "axios";

const API_URL = process.env.REACT_APP_API_URL || "http://localhost:8080/api";

const http = axios.create({
    baseURL: API_URL,
    headers: { "Content-Type": "application/json" },
    withCredentials: false,
});

// REQUEST: dodaj Authorization osim za /auth/login|register i javne lookups
http.interceptors.request.use((config) => {
    const token = typeof window !== "undefined" ? localStorage.getItem("token") : null;

    let path = "";
    try {
        path = new URL(config.url || "", API_URL).pathname; // normalize
    } catch {
        path = config.url || "";
    }

    // src/api/http.js – promeni regex:
    const isAuthEndpoint = /^\/?auth\/(login|register)\b/.test(path);
    const isPublicLookup = /^\/?(notification-types|course-levels|course-statuses|lesson-types|material-types)(\/.*)?$/.test(path);

    if (token && !isAuthEndpoint && !isPublicLookup) {
        config.headers.Authorization = `Bearer ${token}`;
    } else {
        delete config.headers.Authorization;
    }

    return config;
});

// RESPONSE: prosledi grešku
http.interceptors.response.use(
    (res) => res,
    (error) => Promise.reject(error)
);

export default http;
