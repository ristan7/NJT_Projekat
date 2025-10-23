// src/pages/CourseDetails.jsx
import React, { useEffect, useMemo, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { getCourse } from "../api/courses";
import "../css/CourseDetails.css";

function val(obj, ...keys) {
    for (const k of keys) {
        const v = obj?.[k];
        if (v !== undefined && v !== null) return v;
    }
    return undefined;
}

export default function CourseDetails() {
    const { id } = useParams(); // /courses/:id
    const [course, setCourse] = useState(null);
    const [loading, setLoading] = useState(true);
    const [err, setErr] = useState("");

    useEffect(() => {
        let alive = true;
        (async () => {
            setLoading(true);
            setErr("");
            try {
                const data = await getCourse(id);
                if (!alive) return;
                setCourse(data || null);
            } catch (e) {
                if (!alive) return;
                setErr(e?.response?.data?.message || e?.message || "Failed to load course.");
                setCourse(null);
            } finally {
                if (alive) setLoading(false);
            }
        })();
        return () => {
            alive = false;
        };
    }, [id]);

    const title = useMemo(
        () => val(course, "title", "name") || "Untitled course",
        [course]
    );
    const description = useMemo(
        () => val(course, "description", "shortDescription", "summary") || "",
        [course]
    );
    const level = useMemo(() => {
        const lv = val(course, "level", "courseLevel", "levelName");
        if (!lv) return "";
        if (typeof lv === "string") return lv;
        return lv?.name || lv?.levelName || lv?.title || "";
    }, [course]);
    const status = useMemo(() => {
        const st = val(course, "status", "courseStatus", "statusName");
        if (!st) return "";
        if (typeof st === "string") return st;
        return st?.name || st?.statusName || st?.title || "";
    }, [course]);

    const lessons = useMemo(() => {
        const ls = val(course, "lessons", "lessonList");
        return Array.isArray(ls) ? ls : [];
    }, [course]);

    function lessonId(l) {
        return val(l, "lessonId", "id", "uuid");
    }
    function lessonTitle(l) {
        return val(l, "title", "name") || "Lesson";
    }
    function lessonDesc(l) {
        return val(l, "description", "summary") || "";
    }
    function isPreview(l) {
        const v = val(l, "freePreview", "isPreview", "preview");
        return Boolean(v);
    }
    function materialsCount(l) {
        const arr = val(l, "materials", "materialList");
        if (Array.isArray(arr)) return arr.length;
        const n = Number(val(l, "materialsCount", "materialCount"));
        return Number.isFinite(n) ? n : undefined;
    }

    return (
        <div className="course-details-wrap">
            {loading ? (
                <div className="cd-skeleton container">
                    <div className="s1" />
                    <div className="s2" />
                    <div className="s3" />
                </div>
            ) : err ? (
                <div className="container">
                    <div className="alert error">{err}</div>
                </div>
            ) : !course ? (
                <div className="container">
                    <div className="empty muted">Course not found.</div>
                </div>
            ) : (
                <>
                    <header className="cd-head container">
                        <div className="thumb" />
                        <div className="cd-meta">
                            <h1 className="cd-title">{title}</h1>
                            <div className="cd-pills">
                                {level && <span className="pill">{level}</span>}
                                {status && <span className="pill ghost">{status}</span>}
                                {Array.isArray(lessons) && (
                                    <span className="muted">{lessons.length} lesson{lessons.length === 1 ? "" : "s"}</span>
                                )}
                            </div>
                            {description && <p className="muted">{description}</p>}

                            <div className="cd-actions">
                                {/* CTA za kasnije: Enrollment/Payment */}
                                <button
                                    className="btn primary"
                                    onClick={() => alert("Enrollment flow coming next.")}
                                >
                                    Enroll / Buy
                                </button>
                                <Link className="btn ghost" to="/courses">
                                    Back to catalog
                                </Link>
                            </div>
                        </div>
                    </header>

                    <section className="container">
                        <h3 className="lsn-title">Lessons</h3>
                        {lessons.length === 0 ? (
                            <div className="empty muted">No lessons yet.</div>
                        ) : (
                            <div className="lesson-list">
                                {lessons.map((l, idx) => {
                                    const id = lessonId(l);
                                    const t = lessonTitle(l);
                                    const d = lessonDesc(l);
                                    const mc = materialsCount(l);
                                    const preview = isPreview(l);

                                    return (
                                        <article key={id ?? idx} className="lesson-item">
                                            <div className="li-left">
                                                <div className="li-title">
                                                    {t}
                                                    {preview && <span className="badge preview">Preview</span>}
                                                </div>
                                                {d && <div className="li-desc muted">{d}</div>}
                                                <div className="li-meta muted">
                                                    {Number.isFinite(mc) ? `${mc} material${mc === 1 ? "" : "s"}` : ""}
                                                </div>
                                            </div>

                                            <div className="li-actions">
                                                <Link className="btn ghost" to={`/lessons/${id}`}>
                                                    Open lesson
                                                </Link>
                                            </div>
                                        </article>
                                    );
                                })}
                            </div>
                        )}
                    </section>
                </>
            )}
        </div>
    );
}
