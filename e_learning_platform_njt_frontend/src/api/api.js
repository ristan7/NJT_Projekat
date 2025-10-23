import http from "./http";

// ---------- AUTH ----------
export async function login(username, password) {
  const { data } = await http.post("/auth/login", { username, password });
  // očekuješ { token, user }
  return data;
}

export async function register(payload) {
  // payload: { username, password, ... }
  const { data } = await http.post("/auth/register", payload);
  return data;
}

export function saveAuth({ token, user }) {
  if (token) localStorage.setItem("token", token);
  if (user) localStorage.setItem("user", JSON.stringify(user));
}

export function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
}

export async function getMe() {
  const t = typeof window !== "undefined" ? localStorage.getItem("token") : null;
  if (!t) return null;   // ⬅️ nema tokena, nema poziva ka serveru
  const { data } = await http.get("/auth/me");
  return data;
}


// ---------- NOTIFICATIONS ----------
export async function getUnreadCount(userId) {
  if (!userId) return 0;
  try {
    const { data } = await http.get("/notifications/unread/count", {
      params: { userId },
    });
    return Number(data || 0);
  } catch {
    return 0;
  }
}

export async function getNotifications({ userId, unread = false, limit = 50 } = {}) {
  const params = {};
  if (userId != null) params.userId = userId;
  if (unread) params.unread = true;
  if (limit != null) params.limit = limit;

  const { data } = await http.get("/notifications", { params });
  return data;
}

export async function markNotificationRead(id) {
  await http.patch(`/notifications/${id}/read`);
  return true;
}

export async function markAllNotificationsRead(userId) {
  await http.patch(`/notifications/read-all`, null, { params: { userId } });
  return true;
}

export async function getNotificationTypes() {
  const { data } = await http.get("/notification-types");
  return data;
}

export async function deleteNotification(id) {
  await http.delete(`/notifications/${id}`);
  return true;
}

// ---------- USERS (za formu „new notification“) ----------
export async function getUsers() {
  const { data } = await http.get("/auth/users");
  return data;
}

// ---------- STUB KURSEVA (Home) ----------
export async function getRecommendedCourses() {
  return Promise.resolve([
    { id: 1, title: "Java for Beginners", meta: "8h • 24 lessons" },
    { id: 2, title: "Spring Boot REST API", meta: "6h • 18 lessons" },
    { id: 3, title: "React Basics", meta: "5h • 15 lessons" },
  ]);
}
