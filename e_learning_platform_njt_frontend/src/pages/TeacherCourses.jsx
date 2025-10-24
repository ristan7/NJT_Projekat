// src/pages/TeacherCourses.jsx
import React, { useEffect, useState } from "react";
import { getCoursesByAuthor, deleteCourse } from "../api/courses";
import { getMe } from "../api/api";
import "../css/Courses.css";

export default function TeacherCourses() {
    //const [me, setMe] = useState(null);
    const [rows, setRows] = useState([]);
    const [loading, setLoading] = useState(true);
    const [err, setErr] = useState("");

    useEffect(() => {
        (async () => {
            try {
                const u = await getMe();
                //setMe(u);
                const authorId = u?.id ?? u?.userId ?? u?.uid ?? null;
                if (!authorId) throw new Error("Missing user id.");
                const list = await getCoursesByAuthor(authorId);
                setRows(Array.isArray(list) ? list : []);
            } catch (e) {
                setErr(e?.response?.data?.message || e?.message || "Failed to load.");
            } finally {
                setLoading(false);
            }
        })();
    }, []);

    const onDelete = async (id) => {
        if (!window.confirm("Delete this course? This cannot be undone.")) return;
        try {
            await deleteCourse(id);
            setRows((prev) => prev.filter((x) => String(x?.courseId ?? x?.id) !== String(id)));
        } catch (e) {
            alert(e?.response?.data?.message || e?.message || "Delete failed.");
        }
    };

    return (
        <div className="courses-wrap">
            <header className="courses-head container">
                <div className="title-side">
                    <h2>My courses</h2>
                    <p className="muted">Manage your courses, lessons and materials.</p>
                </div>
                <div className="filters">
                    <a className="btn primary" href="/teacher/courses/new">+ New course</a>
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
                ) : rows.length === 0 ? (
                    <div className="empty muted">You have no courses yet.</div>
                ) : (
                    <div className="course-grid">
                        {rows.map((c) => {
                            const id = c?.courseId ?? c?.id;
                            const title = c?.title || c?.name || "Untitled";
                            const desc = c?.description || "";
                            const lessons = Array.isArray(c?.lessons) ? c.lessons.length : (c?.lessonCount ?? 0);

                            return (
                                <article key={id} className="course-card">
                                    <div className="thumb" />
                                    <div className="body">
                                        <h3 className="course-title">{title}</h3>
                                        {desc && <p className="muted clamp-2">{desc}</p>}
                                        <div className="muted">{lessons} lesson{lessons === 1 ? "" : "s"}</div>

                                        <div className="actions" style={{ marginTop: 8 }}>
                                            <a className="btn" href={`/teacher/courses/${id}/manage`}>Manage</a>
                                            <a className="btn" href={`/teacher/courses/${id}/edit`}>Edit</a>
                                            <button className="btn ghost" onClick={() => onDelete(id)}>Delete</button>
                                        </div>
                                    </div>
                                </article>
                            );
                        })}
                    </div>
                )}
            </section>
        </div>
    );
}
