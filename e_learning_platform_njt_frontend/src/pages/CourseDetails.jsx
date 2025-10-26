// src/pages/CourseDetails.jsx
import React, { useEffect, useMemo, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { getCourse, hasCourseAccess } from "../api/courses";
import { requestCourseAccess } from "../api/api";
import "../css/CourseDetails.css";

function pick(obj, ...keys) {
    for (const k of keys) {
        const v = obj?.[k];
        if (v !== undefined && v !== null) return v;
    }
    return undefined;
}

export default function CourseDetails() {
    const { id } = useParams();

    const [course, setCourse] = useState(null);
    const [loading, setLoading] = useState(true);
    const [err, setErr] = useState("");

    const [hasAccessState, setHasAccessState] = useState(false);
    const [checkingAccess, setCheckingAccess] = useState(false);
    const [reqBusy, setReqBusy] = useState(false);

    useEffect(() => {
        let alive = true;
        (async () => {
            setLoading(true);
            setErr("");
            setHasAccessState(false);
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
        return () => { alive = false; };
    }, [id]);

    useEffect(() => {
        let alive = true;
        (async () => {
            const cid = course?.courseId ?? course?.id ?? id;
            if (cid == null) return;
            setCheckingAccess(true);
            try {
                const ok = await hasCourseAccess(cid);
                if (!alive) return;
                setHasAccessState(Boolean(ok));
            } catch {
                if (!alive) return;
                setHasAccessState(false);
            } finally {
                if (alive) setCheckingAccess(false);
            }
        })();
        return () => { alive = false; };
    }, [course, id]);

    const title = useMemo(() => pick(course, "title", "name") || "Untitled course", [course]);
    const description = useMemo(() => pick(course, "description", "shortDescription", "summary") || "", [course]);
    const level = useMemo(() => {
        const lv = pick(course, "level", "courseLevel", "levelName");
        if (!lv) return "";
        if (typeof lv === "string") return lv;
        return lv?.name || lv?.levelName || lv?.title || "";
    }, [course]);
    const status = useMemo(() => {
        const st = pick(course, "status", "courseStatus", "statusName");
        if (!st) return "";
        if (typeof st === "string") return st;
        return st?.name || st?.statusName || st?.title || "";
    }, [course]);

    const lessons = useMemo(() => {
        const ls = pick(course, "lessons", "lessonList");
        return Array.isArray(ls) ? ls : [];
    }, [course]);

    function lessonId(l) { return pick(l, "lessonId", "id", "uuid"); }
    function lessonTitle(l) { return pick(l, "title", "name") || "Lesson"; }
    function lessonDesc(l) { return pick(l, "description", "summary") || ""; }
    function isPreview(l) { return Boolean(pick(l, "freePreview", "isPreview", "preview")); }
    function materialsCount(l) {
        const arr = pick(l, "materials", "materialList");
        if (Array.isArray(arr)) return arr.length;
        const n = Number(pick(l, "materialsCount", "materialCount"));
        return Number.isFinite(n) ? n : undefined;
    }

    async function onRequestAccess() {
        const cid = course?.courseId ?? course?.id ?? id;
        try {
            setReqBusy(true);
            await requestCourseAccess(cid);
            alert("Request sent to admin.");
        } catch (e) {
            alert(e?.response?.data?.message || e?.message || "Failed to send request.");
        } finally {
            setReqBusy(false);
        }
    }

    return (
        <div className="course-details-wrap">
            {loading ? (
                <div className="cd-skeleton container">
                    <div className="s1" /><div className="s2" /><div className="s3" />
                </div>
            ) : err ? (
                <div className="container"><div className="alert error">{err}</div></div>
            ) : !course ? (
                <div className="container"><div className="empty muted">Course not found.</div></div>
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
                                {checkingAccess ? (
                                    <button className="btn" disabled>Checking access…</button>
                                ) : hasAccessState ? (
                                    <a className="btn primary" href={`/courses/${id}`}>Open lessons</a>
                                ) : (
                                    <button className="btn primary" onClick={onRequestAccess} disabled={reqBusy} title="Ask admin to grant you access">
                                        {reqBusy ? "Requesting…" : "Request access"}
                                    </button>
                                )}
                                <Link className="btn ghost" to="/courses">Back to catalog</Link>
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
                                    const lid = lessonId(l);
                                    const t = lessonTitle(l);
                                    const d = lessonDesc(l);
                                    const mc = materialsCount(l);
                                    const preview = isPreview(l);

                                    return (
                                        <article key={lid ?? idx} className="lesson-item">
                                            <div className="li-left">
                                                <div className="li-title">
                                                    {t}{preview && <span className="badge preview">Preview</span>}
                                                </div>
                                                {d && <div className="li-desc muted">{d}</div>}
                                                <div className="li-meta muted">
                                                    {Number.isFinite(mc) ? `${mc} material${mc === 1 ? "" : "s"}` : ""}
                                                </div>
                                            </div>
                                            <div className="li-actions">
                                                <Link className="btn ghost" to={`/lessons/${lid}`}>Open lesson</Link>
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
