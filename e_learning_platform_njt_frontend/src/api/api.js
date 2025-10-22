import http from "./http";

// ---------- AUTH / USER ----------
export async function getMe() {
  const { data } = await http.get(`/auth/me`);
  return data; // { id, username, email, firstName, lastName, role }
}

// ---------- NOTIFICATIONS ----------
export async function getUnreadCount(userId) {
  if (!userId) return 0;
  try {
    const { data } = await http.get(`/notifications/unread/count`, { params: { userId } });
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

  const { data } = await http.get(`/notifications`, { params });
  return data;
}

export async function markNotificationRead(id) {
  await http.post(`/notifications/${id}/read`);
  return true;
}

export async function markAllNotificationsRead(userId) {
  await http.post(`/notifications/read-all`, null, { params: { userId } });
  return true;
}

export async function getNotificationTypes() {
  const { data } = await http.get(`/notification-types`);
  return data;
}

export async function deleteNotification(id) {
  await http.delete(`/notifications/${id}`);
  return true;
}

// ---------- USERS (za formu „new notification“) ----------
// ---------- USERS (za formu „new notification“) ----------
export async function getUsers() {
  const { data } = await http.get(`/auth/users`);
  return data;
}


// ---------- STUB KURSEVA ZA HOME ----------
export async function getRecommendedCourses() {
  return Promise.resolve([
    { id: 1, title: "Java for Beginners", meta: "8h • 24 lessons" },
    { id: 2, title: "Spring Boot REST API", meta: "6h • 18 lessons" },
    { id: 3, title: "React Basics", meta: "5h • 15 lessons" },
  ]);
}
