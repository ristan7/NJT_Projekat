// src/pages/TeacherCourses.jsx
import React, { useEffect, useMemo, useState } from "react";
import { getCoursesByAuthor, deleteCourse, getLessonsByCourse } from "../api/courses";
import { getMe } from "../api/api";
import "../css/Courses.css";

/* ------------------ TEST PREKIDAČI ------------------ */
// 3.1 – simuliraj da stranica "My courses" ne može da se učita
const TEST_FAIL_LOAD_PAGE = false;
// 6.1 – blokiraj otvaranje create forme (+ New course)
const TEST_BLOCK_CREATE_FORM = false;
// 6.1 (varijanta) – blokiraj otvaranje edit forme
const TEST_BLOCK_EDIT_FORM = false;
// 7.1 – blokiraj otvaranje forme za dodavanje lekcija (Manage)
const TEST_BLOCK_MANAGE_FORM = false;

/* ---------------------------------------------------- */

/* ---------- tolerantni helperi ---------- */
const courseIdOf = (c) => c?.courseId ?? c?.id;
const courseTitleOf = (c) => c?.courseTitle ?? c?.title ?? c?.name ?? c?.courseName ?? "Untitled";
const courseDescOf = (c) => c?.courseDescription ?? c?.description ?? c?.summary ?? "";

/** Pokuša da “pročita” count iz DTO-a bez dodatnog API poziva */
function lessonCountFromDto(c) {
    if (!c) return 0;
    if (Array.isArray(c.lessons)) return c.lessons.length;
    if (Array.isArray(c.lessonList)) return c.lessonList.length;
    if (Array.isArray(c.courseLessons)) return c.courseLessons.length;
    if (Array.isArray(c.lessonDtos)) return c.lessonDtos.length;
    return (
        c.lessonCount ??
        c.lessonsCount ??
        c.numberOfLessons ??
        c.totalLessons ??
        0
    );
}

export default function TeacherCourses() {
    const [rows, setRows] = useState([]);
    const [counts, setCounts] = useState({}); // { [courseId]: number }
    const [loading, setLoading] = useState(true);
    const [err, setErr] = useState("");

    // pagination
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(5);

    useEffect(() => {
        (async () => {
            try {
                setLoading(true);
                setErr("");

                // ALT 3.1 – simulacija kvara pri učitavanju stranice
                if (TEST_FAIL_LOAD_PAGE) {
                    throw new Error("Simulated: cannot open my courses page");
                }

                const u = await getMe();
                const authorId = u?.id ?? u?.userId ?? u?.uid;
                if (!authorId) throw new Error("Missing user id.");

                const list = await getCoursesByAuthor(authorId);
                const arr = Array.isArray(list) ? list : [];
                setRows(arr);

                // inicijalni count iz DTO-a
                const firstPass = {};
                for (const c of arr) {
                    const id = courseIdOf(c);
                    if (id == null) continue;
                    firstPass[id] = lessonCountFromDto(c);
                }
                setCounts(firstPass);

                // fallback: za one koji i dalje imaju 0, povuci lekcije i izračunaj .length
                const toFetch = arr
                    .map((c) => courseIdOf(c))
                    .filter((id) => id != null && Number(firstPass[id] || 0) === 0);

                if (toFetch.length > 0) {
                    const results = await Promise.allSettled(
                        toFetch.map((id) => getLessonsByCourse(id))
                    );
                    const patch = {};
                    results.forEach((res, idx) => {
                        const id = toFetch[idx];
                        if (res.status === "fulfilled") {
                            const list = Array.isArray(res.value) ? res.value : [];
                            patch[id] = list.length;
                        }
                    });
                    if (Object.keys(patch).length > 0) {
                        setCounts((prev) => ({ ...prev, ...patch }));
                    }
                }
            } catch (e) {
                // Negativna poruka za "My courses" page
                window.alert("⚠️ The system cannot open my courses page.");
                setErr(e?.response?.data?.message || e?.message || "Failed to load.");
            } finally {
                setLoading(false);
            }
        })();
    }, []);

    useEffect(() => {
        setPage(0);
    }, [rowsPerPage]);

    const total = rows.length;
    const paged = useMemo(() => {
        const start = page * rowsPerPage;
        const end = start + rowsPerPage;
        return rows.slice(start, end);
    }, [rows, page, rowsPerPage]);

    const onDelete = async (id) => {
        if (!window.confirm("Delete this course? This cannot be undone.")) return;
        try {
            await deleteCourse(id);
            setRows((prev) => prev.filter((x) => String(courseIdOf(x)) !== String(id)));
            setCounts((prev) => {
                const { [id]: _, ...rest } = prev;
                return rest;
            });
            // (Opcionalno) uspešna poruka za delete? Nije traženo pa preskačemo.
        } catch (e) {
            alert(e?.response?.data?.message || e?.message || "Delete failed.");
        }
    };

    // Negativna poruka za EDIT formu
    function onEditClick(e) {
        if (TEST_BLOCK_EDIT_FORM || e?.altKey === true) {
            e.preventDefault();
            window.alert("⚠️ The system cannot open the course form.");
        }
    }

    function onManageClick(e) {
        if (TEST_BLOCK_MANAGE_FORM || e?.altKey === true) {
            e.preventDefault();
            window.alert("⚠️ The system cannot open the lesson management form.");
        }
    }


    // Negativna poruka za CREATE formu
    function onNewCourseClick(e) {
        if (TEST_BLOCK_CREATE_FORM || e?.altKey === true) {
            e.preventDefault();
            window.alert("⚠️ The system cannot open the create course form.");
        }
    }

    return (
        <div className="courses-wrap">
            <header className="courses-head container">
                <div className="title-side">
                    <h2>My courses</h2>
                    <p className="muted">Manage your courses, lessons and materials.</p>
                </div>
                <div className="filters">
                    {/* Blokada + ALT simulacija */}
                    <a className="btn primary" href="/teacher/courses/new" onClick={onNewCourseClick}>
                        + New course
                    </a>
                </div>
            </header>

            <section className="container">
                {loading ? (
                    <div className="skeleton-grid">
                        {Array.from({ length: 6 }).map((_, i) => (
                            <div className="skeleton-card" key={i} />
                        ))}
                    </div>
                ) : err ? (
                    <div className="alert error">{err}</div>
                ) : total === 0 ? (
                    <div className="empty muted">You have no courses yet.</div>
                ) : (
                    <>
                        <div className="course-grid">
                            {paged.map((c) => {
                                const id = courseIdOf(c);
                                const title = courseTitleOf(c);
                                const desc = courseDescOf(c);
                                const lessons = Number(counts[id] ?? 0);

                                return (
                                    <article key={id} className="course-card">
                                        <div className="thumb" />
                                        <div className="body">
                                            <h3 className="course-title">{title}</h3>
                                            {desc && <p className="muted clamp-2">{desc}</p>}
                                            <div className="muted">
                                                {lessons} lesson{lessons === 1 ? "" : "s"}
                                            </div>

                                            <div className="actions" style={{ marginTop: 8 }}>
                                                <a
                                                    className="btn"
                                                    href={`/teacher/courses/${id}/manage`}
                                                    onClick={onManageClick}
                                                >
                                                    Manage
                                                </a>

                                                {/* Povezano na blokator */}
                                                <a className="btn" href={`/teacher/courses/${id}/edit`} onClick={onEditClick}>Edit</a>
                                                <button className="btn ghost" onClick={() => onDelete(id)}>Delete</button>
                                            </div>
                                        </div>
                                    </article>
                                );
                            })}
                        </div>

                        {/* Pagination footer */}
                        <div className="n-pagination" style={{ marginTop: 16 }}>
                            <div className="n-rows">
                                Rows per page:&nbsp;
                                <select
                                    className="n-select sm"
                                    value={rowsPerPage}
                                    onChange={(e) => setRowsPerPage(Number(e.target.value))}
                                >
                                    {[5, 10, 20, 50].map((n) => (
                                        <option key={n} value={n}>{n}</option>
                                    ))}
                                </select>
                                <span className="muted">
                                    &nbsp;&nbsp;
                                    {total === 0
                                        ? "0–0 of 0"
                                        : `${page * rowsPerPage + 1}–${Math.min((page + 1) * rowsPerPage, total)} of ${total}`}
                                </span>
                            </div>

                            <div className="n-pager">
                                <button
                                    className="pager-btn"
                                    disabled={page === 0}
                                    onClick={() => setPage((p) => Math.max(p - 1, 0))}
                                    aria-label="Prev"
                                >
                                    ◀
                                </button>
                                <button
                                    className="pager-btn"
                                    disabled={(page + 1) * rowsPerPage >= total}
                                    onClick={() =>
                                        setPage((p) =>
                                            (p + 1) * rowsPerPage >= total ? p : p + 1
                                        )
                                    }
                                    aria-label="Next"
                                >
                                    ▶
                                </button>
                            </div>
                        </div>
                    </>
                )}
            </section>
        </div>
    );
}
