import axios from "axios";

const http = axios.create({
    baseURL: "http://localhost:8080/api",
    headers: { "Content-Type": "application/json" },
});

http.interceptors.request.use((config) => {
    const token = typeof window !== "undefined" ? localStorage.getItem("token") : null;
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
});

// 401/403 -> logout + redirect  (IZMENI)
http.interceptors.response.use(
    (res) => res,
    (error) => {
        const status = error?.response?.status;

        // 401 = neautentifikovan -> očisti i ka /login
        if (status === 401) {
            try {
                localStorage.removeItem("token");
                localStorage.removeItem("user");
            } catch { }
            if (
                typeof window !== "undefined" &&
                window.location.pathname !== "/login"
            ) {
                window.location.href = "/login";
            }
        }

        // 403 = zabranjeno (nema dozvolu) -> NE odjavljuj korisnika
        // samo prosledi grešku da je UI prikaže
        return Promise.reject(error);
    }
);


export default http;
