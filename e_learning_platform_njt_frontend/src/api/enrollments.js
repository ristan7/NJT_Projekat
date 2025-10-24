// src/api/enrollments.js
import http from "./http";

/**
 * Admin kreira enrollment studentu za kurs.
 * default: status = ACTIVE, trajanje (dani) optional
 */
export async function adminCreateEnrollment({ userId, courseId, daysValid = 365, status = "ACTIVE" }) {
    const payload = {
        userId: Number(userId),
        courseId: Number(courseId),
        status,                   // "ACTIVE" | "PAID" | sl.
        daysValid: Number(daysValid),
    };

    // primarna ruta
    try {
        const { data } = await http.post("/enrollments", payload);
        return data;
    } catch (e1) {
        // fallback varijante
        try {
            const { data } = await http.post("/admin/enrollments", payload);
            return data;
        } catch (e2) {
            const { data } = await http.post("/enrollments/admin/create", payload);
            return data;
        }
    }
}

/** (opciono) lista svih kurseva radi dropdown-a u AdminEnrollments strani */
export async function getAllCoursesSimple() {
    try {
        const { data } = await http.get("/courses", { params: { page: 0, size: 999 } });
        const rows = Array.isArray(data) ? data : (data?.rows ?? data?.content ?? []);
        return Array.isArray(rows) ? rows : [];
    } catch {
        return [];
    }
}
