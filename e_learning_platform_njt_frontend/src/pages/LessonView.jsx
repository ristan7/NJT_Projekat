// src/pages/LessonView.jsx
import React, { useEffect, useMemo, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getLesson, hasCourseAccess } from "../api/courses";
import "../css/LessonView.css";

function pick(obj, ...keys) {
    for (const k of keys) {
        const v = obj?.[k];
        if (v !== undefined && v !== null) return v;
    }
    return undefined;
}

function titleOf(x) {
    if (!x) return "";
    if (typeof x === "string") return x;
    return x.name || x.title || x.typeName || x.materialTypeName || "";
}

function materialType(m) {
    const raw =
        m?.type ??
        m?.materialType ??
        m?.materialTypeName ??
        m?.typeName ??
        m?.materialTypeCode ??
        "";
    return titleOf(raw) || String(raw || "");
}

function iconForType(t) {
    const s = String(t || "").toLowerCase();
    if (s.includes("pdf")) return "📄";
    if (s.includes("video")) return "🎥";
    if (s.includes("link") || s.includes("url") || s.includes("web")) return "🔗";
    if (s.includes("image") || s.includes("img") || s.includes("png") || s.includes("jpg")) return "🖼️";
    if (s.includes("doc") || s.includes("msword")) return "📝";
    return "📦";
}

export default function LessonView() {
    const { id } = useParams(); // /lessons/:id
    const [lesson, setLesson] = useState(null);
    const [loading, setLoading] = useState(true);
    const [err, setErr] = useState("");

    // access
    const [accessChecked, setAccessChecked] = useState(false);
    const [hasAccessState, setHasAccessState] = useState(false);

    // 1) Učitaj lekciju
    useEffect(() => {
        let alive = true;
        (async () => {
            setLoading(true);
            setErr("");
            setAccessChecked(false);
            setHasAccessState(false);

            try {
                const data = await getLesson(id);
                if (!alive) return;
                setLesson(data || null);
            } catch (e) {
                if (!alive) return;
                setErr(e?.response?.data?.message || e?.message || "Failed to load lesson.");
                setLesson(null);
            } finally {
                if (alive) setLoading(false);
            }
        })();
        return () => {
            alive = false;
        };
    }, [id]);

    const title = useMemo(
        () => pick(lesson, "title", "name") || "Lesson",
        [lesson]
    );
    const description = useMemo(
        () => pick(lesson, "description", "summary", "text") || "",
        [lesson]
    );
    const isPreview = useMemo(() => {
        const v = pick(lesson, "freePreview", "isPreview", "preview");
        return Boolean(v);
    }, [lesson]);
    const courseId = useMemo(
        () =>
            pick(lesson, "courseId") ??
            pick(lesson?.course, "courseId", "id"),
        [lesson]
    );
    const materials = useMemo(() => {
        const list = pick(lesson, "materials", "materialList") || [];
        return Array.isArray(list) ? list : [];
    }, [lesson]);

    function mid(m) {
        return pick(m, "materialId", "id", "uuid");
    }
    function mtitle(m) {
        return pick(m, "title", "name") || "Material";
    }
    function murl(m) {
        return pick(m, "url", "link", "href", "path") || "";
    }

    // 2) Ako NIJE preview, proveri enrollment (tanki guard)
    useEffect(() => {
        let alive = true;
        (async () => {
            // Ako je preview, ne treba guard
            if (!lesson || isPreview) {
                setAccessChecked(true);
                setHasAccessState(true);
                return;
            }
            // Ako nemamo courseId, ne možemo da proverimo — tretiraj kao zaključano
            if (courseId == null) {
                setAccessChecked(true);
                setHasAccessState(false);
                return;
            }
            try {
                const ok = await hasCourseAccess(courseId);
                if (!alive) return;
                setHasAccessState(Boolean(ok));
            } catch {
                if (!alive) return;
                setHasAccessState(false);
            } finally {
                if (alive) setAccessChecked(true);
            }
        })();
        return () => {
            alive = false;
        };
    }, [lesson, isPreview, courseId]);

    const locked = !hasAccessState;

    return (
        <div className="lesson-wrap">
            {loading ? (
                <div className="container">
                    <div className="lv-skeleton">
                        <div className="s1" />
                        <div className="s2" />
                        <div className="s3" />
                    </div>
                </div>
            ) : err ? (
                <div className="container">
                    <div className="alert error">{err}</div>
                </div>
            ) : !lesson ? (
                <div className="container">
                    <div className="empty muted">Lesson not found.</div>
                </div>
            ) : (
                <>
                    <header className="lesson-head container">
                        <div>
                            <div className="breadcrumbs muted">
                                <Link to="/courses">Courses</Link>
                                {courseId != null && (
                                    <>
                                        <span> / </span>
                                        <Link to={`/courses/${courseId}`}>Course</Link>
                                    </>
                                )}
                                <span> / </span>
                                <span className="muted">Lesson</span>
                            </div>

                            <h1 className="lesson-title">{title}</h1>
                            {isPreview && <span className="badge preview">Preview</span>}
                        </div>

                        <div className="lesson-actions">
                            {courseId != null && (
                                <Link className="btn ghost" to={`/courses/${courseId}`}>
                                    Back to course
                                </Link>
                            )}
                            <Link className="btn" to="/courses">
                                Catalog
                            </Link>
                        </div>
                    </header>

                    {description && (
                        <section className="container">
                            <p className="muted">{description}</p>
                        </section>
                    )}

                    <section className="container">
                        <h3 className="sec-title">Materials</h3>

                        {!accessChecked ? (
                            <div className="empty muted">Checking access…</div>
                        ) : locked ? (
                            <div className="locked">
                                <div className="lock-emoji">🔒</div>
                                <div className="lock-title">Content locked</div>
                                <p className="muted">
                                    This lesson isn’t available without enrollment. Enroll to unlock all materials.
                                </p>
                                {courseId != null && (
                                    <Link className="btn primary" to={`/courses/${courseId}`}>
                                        Go to course
                                    </Link>
                                )}
                            </div>
                        ) : materials.length === 0 ? (
                            <div className="empty muted">No materials provided.</div>
                        ) : (
                            <ul className="mat-list" role="list">
                                {materials.map((m, i) => {
                                    const id = mid(m) ?? i;
                                    const mt = materialType(m);
                                    const ic = iconForType(mt);
                                    const name = mtitle(m);
                                    const url = murl(m);

                                    return (
                                        <li className="mat-item" key={id} role="listitem">
                                            <div className="mat-left">
                                                <span className="mat-ic" aria-hidden>{ic}</span>
                                                <div className="mat-main">
                                                    <div className="mat-title">{name}</div>
                                                    <div className="mat-meta muted">{mt || "Material"}</div>
                                                </div>
                                            </div>

                                            <div className="mat-actions">
                                                {url ? (
                                                    <a className="btn" href={url} target="_blank" rel="noreferrer">
                                                        Open
                                                    </a>
                                                ) : (
                                                    <button className="btn" disabled title="No URL provided">
                                                        Open
                                                    </button>
                                                )}
                                            </div>
                                        </li>
                                    );
                                })}
                            </ul>
                        )}
                    </section>
                </>
            )}
        </div>
    );
}
