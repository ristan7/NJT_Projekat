// src/api/courses.js
import http from "./http";

// Ako http.baseURL već sadrži /api, nemoj duplirati /api u putanji
const __BASE = (http?.defaults?.baseURL || "");
const __HAS_API_PREFIX = /\/api\/?$/.test(__BASE);
const fixPath = (p) => (__HAS_API_PREFIX ? p.replace(/^\/api\//, "/") : p);


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

async function tryPaths({ method, primary, fallback, data, config, retryOn = [404] }) {
    const p1 = primary;
    const p2 = fallback || null;
    try {
        const r = await http[method](p1, data, config);
        return r.data;
    } catch (e) {
        const code = e?.response?.status;
        if (!p2 || !retryOn.includes(code)) throw e;
        const r2 = await http[method](p2, data, config);
        return r2.data;
    }
}


/* -------------------------------------------
   Courses (public/secured READ)
------------------------------------------- */

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

export async function getCourse(courseId) {
  if (courseId == null || String(courseId).toLowerCase() === "new") return null;
  const { data } = await http.get(`/courses/${courseId}`);
  return data;
}

/** teacher only – kursevi autora */
export async function getCoursesByAuthor(authorId) {
    const { data } = await http.get(`/courses/author/${authorId}`);
    return Array.isArray(data) ? data : [];
}

/* -------------------------------------------
   Courses (TEACHER WRITE)
------------------------------------------- */

function toCourseDto(form) {
    // pokupi user-a da popunimo authorId (dok backend ne uzme iz tokena)
    let me = null;
    try {
        me = JSON.parse(localStorage.getItem("user") || "null");
    } catch { }

    return {
        courseTitle: String(form.title ?? "").trim(),
        courseDescription: String(form.description ?? "").trim(),
        // nema više coursePrice
        courseLevelId: form.levelId != null ? Number(form.levelId) : null,
        courseStatusId: form.statusId != null ? Number(form.statusId) : null,
        authorId: me?.userId ?? me?.id ?? null,
    };
}

export async function createCourse(form) {
    const dto = toCourseDto(form);
    const { data } = await http.post("/courses", dto);
    return data;
}

export async function updateCourse(courseId, form) {
    const dto = toCourseDto(form);
    const { data } = await http.put(`/courses/${courseId}`, dto);
    return data;
}

export async function deleteCourse(courseId) {
    await http.delete(`/courses/${courseId}`);
    return true;
}

// PATCH helpers (mali, fokusirani endpointi) – šaljemo i body i params
export async function patchCourseTitle(courseId, value) {
    const data = await http.patch(
        `/courses/${courseId}/title`,
        { value },
        { params: { value } }
    );
    return data.data;
}
export async function patchCourseDescription(courseId, value) {
    const data = await http.patch(
        `/courses/${courseId}/description`,
        { value },
        { params: { value } }
    );
    return data.data;
}
export async function patchCourseLevel(courseId, levelId) {
    const data = await http.patch(
        `/courses/${courseId}/level`,
        { levelId },
        { params: { levelId } }
    );
    return data.data;
}
export async function patchCourseStatus(courseId, statusId) {
    const data = await http.patch(
        `/courses/${courseId}/status`,
        { statusId },
        { params: { statusId } }
    );
    return data.data;
}

// Koliko lekcija ima kurs
export async function getLessonsCount(courseId) {
    try {
        const { data } = await http.get(`/courses/${courseId}/lessons/count`);
        return Number(data ?? 0);
    } catch {
        // Fallback ako endpoint nije dostupan: prebrojimo listu
        const list = await getLessonsByCourse(courseId);
        return Array.isArray(list) ? list.length : 0;
    }
}

// Koliko materijala ima lekcija
export async function getMaterialsCount(lessonId) {
    try {
        const { data } = await http.get(`/lessons/${lessonId}/materials/count`);
        return Number(data ?? 0);
    } catch {
        const list = await getMaterialsByLesson(lessonId);
        return Array.isArray(list) ? list.length : 0;
    }
}

/* -------------------------------------------
   Lessons (READ)
   - Preferira ugnježdene rute: /courses/{cid}/lessons/{lid}
   - Fallback na /lessons/{lid} gde je moguće
------------------------------------------- */

export async function getLessonsByCourse(courseId) {
    const { data } = await http.get(`/courses/${courseId}/lessons`);
    return Array.isArray(data) ? data : [];
}

/** Overload:
 *  getLesson(courseId, lessonId)  -> nested ruta
 *  getLesson(lessonId)            -> fallback na /lessons/{id}
 */
export async function getLesson(a, b) {
    if (b != null) {
        const courseId = a, lessonId = b;
        return tryPaths({
            method: "get",
            primary: `/lessons/${lessonId}`,                      // FLAT PRVO
            fallback: `/courses/${courseId}/lessons/${lessonId}`, // NESTED POSLE
        });
    }
    const lessonId = a;
    const { data } = await http.get(`/lessons/${lessonId}`);
    return data;
}

/* -------------------------------------------
   Lessons (TEACHER WRITE)
------------------------------------------- */

export async function createLesson(courseId, data) {
    const dto = {
        // tolerantno prema starim/novim imenima polja:
        lessonTitle: (data.lessonTitle ?? data.title ?? "").trim(),
        lessonSummary: (data.lessonSummary ?? data.description ?? "").trim(),
        lessonAvailable: Boolean(
            data.lessonAvailable ?? data.available ?? true
        ),
        lessonTypeId:
            data.lessonTypeId != null
                ? Number(data.lessonTypeId)
                : data.typeId != null
                    ? Number(data.typeId)
                    : null,
        courseId: Number(courseId),
        lessonOrderIndex: Number(data.lessonOrderIndex ?? data.orderIndex ?? 1),
        freePreview: Boolean(data.freePreview ?? data.preview ?? false),
    };
    const { data: res } = await http.post(`/courses/${courseId}/lessons`, dto);
    return res;
}

export async function updateLesson(a, b, c) {
    // updateLesson(courseId, lessonId, data)  or  updateLesson(lessonId, data)
    let courseId, lessonId, payload;
    if (c !== undefined) { courseId = a; lessonId = b; payload = c; }
    else { lessonId = a; payload = b; }

    const title =
        String(payload.lessonTitle ?? payload.title ?? payload.lesson_name ?? "").trim();
    const summary =
        String(payload.lessonSummary ?? payload.description ?? payload.summary ?? "").trim();

    if (!title) {
        console.warn("[updateLesson] Empty title in payload:", payload);
        // vrati jasnu grešku ka UI da korisnik zna šta se dešava
        throw new Error("Lesson title is empty – check form state binding.");
    }

    const dto = {
        lessonTitle: title,
        lessonSummary: summary,
        lessonAvailable: Boolean(payload.lessonAvailable ?? payload.available),
        lessonTypeId:
            payload.lessonTypeId != null
                ? Number(payload.lessonTypeId)
                : payload.typeId != null
                    ? Number(payload.typeId)
                    : null,
        courseId: Number(payload.courseId ?? courseId ?? payload.course_id ?? payload.cid),
        lessonOrderIndex: Number(payload.lessonOrderIndex ?? payload.orderIndex ?? 1),
        freePreview: Boolean(payload.freePreview ?? payload.preview),
    };

    // backend ima flat PUT; nested nek ostane kao fallback za budućnost
    return tryPaths({
        method: "put",
        primary: `/lessons/${lessonId}`,                      // FLAT PRVO
        fallback: courseId != null ? `/courses/${courseId}/lessons/${lessonId}` : null,
        data: dto,
        retryOn: [404], // ovde je 405 malo verovatno, ali može ostati 404
    });
}

export async function deleteLesson(a, b) {
    // deleteLesson(courseId, lessonId) OR deleteLesson(lessonId)
    if (b != null) {
        const courseId = a;
        const lessonId = b;
        try {
            await http.delete(`/courses/${courseId}/lessons/${lessonId}`);
        } catch (e) {
            if (e?.response?.status !== 404) throw e;
            await http.delete(`/lessons/${lessonId}`);
        }
        return true;
    }
    const lessonId = a;
    await http.delete(`/lessons/${lessonId}`);
    return true;
}

/** PATCH toggles – šaljemo body i params; podrška za nested i flat rute */
export async function patchLessonAvailable(a, b, c) {
    let courseId, lessonId, value;
    if (c !== undefined) {
        courseId = a; lessonId = b; value = c;
    } else {
        lessonId = a; value = b;
    }
    const pathPrimary = courseId != null
        ? `/courses/${courseId}/lessons/${lessonId}/available`
        : `/lessons/${lessonId}/available`;
    try {
        const { data } = await http.patch(pathPrimary, { value }, { params: { value } });
        return data;
    } catch (e) {
        if (courseId == null || e?.response?.status !== 404) throw e;
        const { data } = await http.patch(`/lessons/${lessonId}/available`, { value }, { params: { value } });
        return data;
    }
}

export async function patchLessonType(a, b, c) {
    let courseId, lessonId, lessonTypeId;
    if (c !== undefined) {
        courseId = a; lessonId = b; lessonTypeId = c;
    } else {
        lessonId = a; lessonTypeId = b;
    }
    const pathPrimary = courseId != null
        ? `/courses/${courseId}/lessons/${lessonId}/type`
        : `/lessons/${lessonId}/type`;
    try {
        const { data } = await http.patch(
            pathPrimary,
            { lessonTypeId },
            { params: { lessonTypeId } }
        );
        return data;
    } catch (e) {
        if (courseId == null || e?.response?.status !== 404) throw e;
        const { data } = await http.patch(
            `/lessons/${lessonId}/type`,
            { lessonTypeId },
            { params: { lessonTypeId } }
        );
        return data;
    }
}

export async function patchLessonPreview(a, b, c) {
    let courseId, lessonId, value;
    if (c !== undefined) {
        courseId = a; lessonId = b; value = Boolean(c);
    } else {
        lessonId = a; value = Boolean(b);
    }
    const pathPrimary = courseId != null
        ? `/courses/${courseId}/lessons/${lessonId}/preview`
        : `/lessons/${lessonId}/preview`;
    try {
        const { data } = await http.patch(
            pathPrimary,
            { value },                // body
            { params: { value } }     // query param (backend čita "value")
        );
        return data;
    } catch (e) {
        if (courseId == null || e?.response?.status !== 404) throw e;
        const { data } = await http.patch(
            `/lessons/${lessonId}/preview`,
            { value },
            { params: { value } }
        );
        return data;
    }
}

/* -------------------------------------------
   Materials (READ/WRITE preko LessonController)
------------------------------------------- */

// getMaterialsByLesson — prvo flat, pa nested
export async function getMaterialsByLesson(a, b) {
    if (b != null) {
        const courseId = a, lessonId = b;
        return tryPaths({
            method: "get",
            primary: `/lessons/${lessonId}/materials`,                      // FLAT PRVO
            fallback: `/courses/${courseId}/lessons/${lessonId}/materials`, // NESTED POSLE
        });
    }
    const lessonId = a;
    const { data } = await http.get(`/lessons/${lessonId}/materials`);
    return Array.isArray(data) ? data : [];
}

export async function replaceMaterials(a, b, list) {
    let courseId, lessonId, payload;

    // Overload:
    //   replaceMaterials(courseId, lessonId, list)
    //   replaceMaterials(lessonId, list)
    if (Array.isArray(b) && list === undefined) {
        lessonId = a; payload = b;
    } else {
        courseId = a; lessonId = b; payload = list;
    }

    // Očisti ključeve koje backend ne očekuje (tempId, createdAt, updatedAt...)
    const cleaned = (payload || []).map(({ tempId, createdAt, updatedAt, ...m }, idx) => ({
        ...m,
        lessonId: Number(lessonId),
        // osiguraj order ako fali
        materialOrderIndex:
            m.materialOrderIndex != null ? Number(m.materialOrderIndex) : idx + 1,
        // normalizuj tipove
        materialId: m.materialId != null ? Number(m.materialId) : null,
        materialTypeId: m.materialTypeId != null ? Number(m.materialTypeId) : null,
        materialTitle: String(m.materialTitle ?? m.title ?? "").trim(),
        resourceUrl: String(m.resourceUrl ?? m.url ?? "").trim(),
        content: m.content ?? "",
    }));

    // Primarno gađamo FLAT rutu (kako je u backend-u), fallback na nested
    return tryPaths({
        method: "put",
        primary: `/lessons/${lessonId}/materials`,
        fallback: courseId != null ? `/courses/${courseId}/lessons/${lessonId}/materials` : null,
        data: cleaned,
        retryOn: [404, 405],
    });
}

// addMaterial — prvo flat, pa nested
export async function addMaterial(a, b, payload, currentCount = 0) {
    let courseId, lessonId, body;
    if (payload && b != null) { courseId = a; lessonId = b; body = payload; }
    else { lessonId = a; body = b; }

    const dto = {
        materialTitle: (body.materialTitle ?? body.title ?? "").trim(),
        resourceUrl: (body.resourceUrl ?? body.url ?? "").trim(),
        content: body.content ?? "",
        materialTypeId: body.materialTypeId != null ? Number(body.materialTypeId) : null,
        lessonId: Number(lessonId),
        materialOrderIndex: Number(body.materialOrderIndex ?? body.orderIndex ?? currentCount + 1),
    };

    // Primarno NESTED (jer flat POST vraća 405), fallback FLAT
    return tryPaths({
        method: "post",
        primary: courseId != null
            ? `/courses/${courseId}/lessons/${lessonId}/materials`
            : `/lessons/${lessonId}/materials`,
        fallback: courseId != null
            ? `/lessons/${lessonId}/materials`
            : null,
        data: dto,
        retryOn: [404, 405], // pokušaj i kod Method Not Allowed
    });
}

// updateMaterial — prvo flat, pa nested
export async function updateMaterial(a, b, c, d) {
    let courseId, lessonId, materialId, body;
    if (d !== undefined) { courseId = a; lessonId = b; materialId = c; body = d; }
    else { lessonId = a; materialId = b; body = c; }

    const dto = {
        materialId: Number(materialId),
        materialTitle: (body.materialTitle ?? body.title ?? "").trim(),
        resourceUrl: (body.resourceUrl ?? body.url ?? "").trim(),
        content: body.content ?? "",
        materialTypeId: body.materialTypeId != null ? Number(body.materialTypeId) : null,
        lessonId: Number(lessonId),
        materialOrderIndex: Number(body.materialOrderIndex ?? body.orderIndex ?? 1),
    };

    if (courseId != null) {
        return tryPaths({
            method: "put",
            primary: `/lessons/${lessonId}/materials/${materialId}`,                              // FLAT
            fallback: `/courses/${courseId}/lessons/${lessonId}/materials/${materialId}`,         // NESTED
            data: dto,
        });
    }
    const { data } = await http.put(`/lessons/${lessonId}/materials/${materialId}`, dto);
    return data;
}

// removeMaterial — prvo flat, pa nested
export async function removeMaterial(a, b, c) {
    if (c != null) {
        const courseId = a, lessonId = b, materialId = c;
        try {
            await http.delete(`/lessons/${lessonId}/materials/${materialId}`); // FLAT
        } catch {
            await http.delete(`/courses/${courseId}/lessons/${lessonId}/materials/${materialId}`); // NESTED
        }
        return true;
    }
    const lessonId = a, materialId = b;
    await http.delete(`/lessons/${lessonId}/materials/${materialId}`);
    return true;
}


/* -------------------------------------------
   Lookups (PUBLIC) – radi i sa /api/* i bez
------------------------------------------- */
function normalizeTypes(list, idKey, nameKey) {
    return (Array.isArray(list) ? list : []).map(x => ({
        id: x?.[idKey] ?? x?.id,
        name: x?.[nameKey] ?? x?.name,
    }));
}

async function getFromFirstAvailable(paths) {
    for (const raw of paths) {
        const p = fixPath(raw);
        try {
            const { data } = await http.get(p);
            if (Array.isArray(data) || data) return data;
        } catch { /* try next */ }
    }
    return [];
}

export async function getCourseLevels() {
    const raw = await getFromFirstAvailable([
        "/api/course-levels",
        "/lookups/course-levels",
        "/course-levels",
    ]);
    return normalizeTypes(raw, "courseLevelId", "courseLevelName");
}

export async function getCourseStatuses() {
    const raw = await getFromFirstAvailable([
        "/api/course-statuses",
        "/lookups/course-statuses",
        "/course-statuses",
    ]);
    return normalizeTypes(raw, "courseStatusId", "courseStatusName");
}

export async function getLessonTypes() {
    const raw = await getFromFirstAvailable([
        "/api/lesson-types",
        "/lookups/lesson-types",
        "/lesson-types",
    ]);
    return normalizeTypes(raw, "lessonTypeId", "lessonTypeName");
}

export async function getMaterialTypes() {
    const raw = await getFromFirstAvailable([
        "/api/material-types",
        "/lookups/material-types",
        "/material-types",
    ]);
    return normalizeTypes(raw, "materialTypeId", "materialTypeName");
}



/* -------------------------------------------
   Enrollments helpers (opciono)
------------------------------------------- */
export async function getMyEnrollments() {
    try {
        const { data } = await http.get("/enrollments/me");
        if (Array.isArray(data)) return data;
        if (Array.isArray(data?.items)) return data.items;
    } catch { }
    return [];
}

export async function hasCourseAccess(courseId) {
    if (courseId == null) return false;

    const normalizeStatus = (s) => String(s || "").toUpperCase();
    const hasAccessByStatus = (enr) => {
        const st = normalizeStatus(enr?.status || enr?.enrollmentStatus);
        // Pristup za ACTIVE i COMPLETED (nema više PAID)
        if (st === "ACTIVE" || st === "COMPLETED") {
            const exp = enr?.expiresAt || enr?.expires || enr?.validTo;
            if (!exp) return true;
            const t = Date.parse(exp);
            return Number.isFinite(t) ? t > Date.now() : true;
        }
        return false;
    };

    try {
        const { data } = await http.get(`/enrollments/me/${courseId}`);
        if (data && (hasAccessByStatus(data) || hasAccessByStatus(data?.enrollment))) {
            return true;
        }
    } catch { /* ignore */ }

    try {
        const list = await getMyEnrollments();
        for (const e of list) {
            const cid = e?.courseId ?? e?.course?.courseId ?? e?.course?.id ?? e?.course_id ?? null;
            if (String(cid) === String(courseId) && hasAccessByStatus(e)) {
                return true;
            }
        }
    } catch { /* ignore */ }

    return false;
}


