// src/api/courses.js
import http from "./http";

/* -------------------------------------------
   Helpers
------------------------------------------- */
function cleanParams(obj = {}) {
    const p = {};
    for (const [k, v] of Object.entries(obj)) {
        if (v === undefined || v === null || v === "") continue;
        p[k] = v;
    }
    return p;
}

function normListResponse(data, fallbackPageSize = 12) {
    // Backend ti za /api/courses trenutno vraća List<CourseDto>.
    // Ali ostavljamo podršku i za paginirane oblike.
    if (Array.isArray(data)) {
        return { rows: data, total: data.length, page: 0, size: data.length };
    }
    return {
        rows: data?.rows ?? data?.content ?? data?.items ?? [],
        total: data?.total ?? data?.totalElements ?? 0,
        page: data?.page ?? data?.number ?? 0,
        size: data?.size ?? fallbackPageSize,
    };
}

/* -------------------------------------------
   Courses
------------------------------------------- */

/**
 * GET /api/courses
 * Query params su opcioni; backend koji ignoriše ih neće puknuti.
 * UI očekuje {rows,total,page,size} — normalizujemo.
 */
export async function getCourses({
    q,
    level,
    status,
    category,
    sort,
    page = 0,
    size = 12,
} = {}) {
    const params = cleanParams({ q, level, status, category, sort, page, size });
    const { data } = await http.get("/courses", { params });
    return normListResponse(data, size);
}

/** GET /api/courses/{id} */
export async function getCourse(courseId) {
    const { data } = await http.get(`/courses/${courseId}`);
    return data;
}

/* -------------------------------------------
   Lessons
------------------------------------------- */

/** GET /api/lessons/{id} */
export async function getLesson(lessonId) {
    const { data } = await http.get(`/lessons/${lessonId}`);
    return data;
}

/** (korisno u CourseDetails) GET /api/courses/{courseId}/lessons */
export async function getLessonsByCourse(courseId) {
    const { data } = await http.get(`/courses/${courseId}/lessons`);
    return Array.isArray(data) ? data : [];
}

/* -------------------------------------------
   Lookups (secured)
------------------------------------------- */

async function safeLookup(url) {
    try {
        const { data } = await http.get(url);
        return Array.isArray(data) ? data : [];
    } catch {
        return [];
    }
}

// NOVO: direktno na javne rute iz backend-a
export const getCourseLevels = () => safeLookup("/course-levels");
export const getCourseStatuses = () => safeLookup("/course-statuses");
export const getLessonTypes = () => safeLookup("/lesson-types");
export const getMaterialTypes = () => safeLookup("/material-types");

/* -------------------------------------------
   Enrollments / Access helpers (opciono)
------------------------------------------- */

/** GET /api/enrollments/me → vraća [] ako endpoint ne postoji ili greška */
export async function getMyEnrollments() {
    try {
        const { data } = await http.get("/enrollments/me");
        if (Array.isArray(data)) return data;
        if (Array.isArray(data?.items)) return data.items;
    } catch {
        // ignorisi grešku i vrati prazan niz da UI ne puca
    }
    return [];
}

/**
 * Proveri da li korisnik ima aktivan pristup kursu
 * Pokušaj /enrollments/me/{courseId}, pa fallback na /enrollments/me
 */
export async function hasCourseAccess(courseId) {
    if (courseId == null) return false;

    const normalizeStatus = (s) => String(s || "").toUpperCase();
    const isActive = (enr) => {
        const st = normalizeStatus(enr?.status || enr?.enrollmentStatus);
        if (st === "ACTIVE" || st === "PAID") {
            const exp = enr?.expiresAt || enr?.expires || enr?.validTo;
            if (!exp) return true;
            const t = Date.parse(exp);
            return Number.isFinite(t) ? t > Date.now() : true;
        }
        return false;
    };

    // 1) specifični endpoint (ako postoji)
    try {
        const { data } = await http.get(`/enrollments/me/${courseId}`);
        if (data && (isActive(data) || isActive(data?.enrollment))) return true;
    } catch {
        // ignore → fallback
    }

    // 2) lista kao fallback
    try {
        const list = await getMyEnrollments();
        for (const e of list) {
            const cid =
                e?.courseId ??
                e?.course?.courseId ??
                e?.course?.id ??
                e?.course_id ??
                null;
            if (String(cid) === String(courseId) && isActive(e)) return true;
        }
    } catch {
        // ignore
    }

    return false;
}
