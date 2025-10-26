// src/pages/CourseManage.jsx
import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import {
    getCourse,
    getLessonsByCourse,
    createLesson,
    deleteLesson,
    getLessonTypes,
    getMaterialsCount,
} from "../api/courses";
import "../css/CourseDetails.css";

const TEST_FAIL_ADD_LESSON = false; // set true da simulira neuspeh dodavanja



export default function CourseManage() {
    const { courseId } = useParams();

    const [title, setTitle] = useState("");
    const [desc, setDesc] = useState("");
    const [typeId, setTypeId] = useState("");
    const [available, setAvailable] = useState(true);
    const [preview, setPreview] = useState(false);

    const [course, setCourse] = useState(null);
    const [lessons, setLessons] = useState([]);
    const [types, setTypes] = useState([]);
    const [matCounts, setMatCounts] = useState({});

    const [loading, setLoading] = useState(true);
    const [err, setErr] = useState("");
    const [adding, setAdding] = useState(false);

    useEffect(() => {
        let alive = true;
        (async () => {
            try {
                const [c, ls, tps] = await Promise.all([
                    getCourse(courseId),
                    getLessonsByCourse(courseId),
                    getLessonTypes(),
                ]);
                if (!alive) return;
                const list = Array.isArray(ls) ? ls : [];

                setCourse(c || null);
                setLessons(list);
                setTypes(Array.isArray(tps) ? tps : []);

                const pairs = await Promise.all(
                    list.map(async (l) => {
                        const lid = l?.lessonId ?? l?.id;
                        if (!lid) return [null, 0];
                        try {
                            const n = await getMaterialsCount(lid);
                            return [String(lid), Number(n) || 0];
                        } catch {
                            return [String(lid), Array.isArray(l?.materials) ? l.materials.length : 0];
                        }
                    })
                );
                if (alive) setMatCounts(Object.fromEntries(pairs.filter(([k]) => k != null)));
            } catch (e) {
                if (!alive) return;
                setErr(e?.response?.data?.message || e?.message || "Failed to load.");
                setMatCounts({});
            } finally {
                if (alive) setLoading(false);
            }
        })();
        return () => { alive = false; };
    }, [courseId]);

    const onAddLesson = async (e) => {
        e.preventDefault();
        if (!title.trim()) return;
        setAdding(true);
        try {
            // ALT klik na "Add lesson" ili TEST_FAIL_ADD_LESSON simulira neuspeh
            const altFail = e?.nativeEvent?.altKey === true;
            if (TEST_FAIL_ADD_LESSON || altFail) {
                throw new Error("Simulated lesson add error");
            }

            const payload = {
                title,
                description: desc,
                lessonTypeId: typeId ? Number(typeId) : null,
                available,
                freePreview: preview,
                orderIndex: (lessons?.length ?? 0) + 1,
            };

            const created = await createLesson(courseId, payload);
            setLessons((prev) => [...prev, created]);
            const lid = created?.lessonId ?? created?.id;
            if (lid) setMatCounts((m) => ({ ...m, [String(lid)]: 0 }));

            // reset polja
            setTitle(""); setDesc(""); setTypeId(""); setPreview(false); setAvailable(true);

            // ✅ TRAŽENA PORUKA
            alert("✅ Lesson successfully added !");
        } catch (e2) {
            // ❌ TRAŽENA NEGATIVNA PORUKA
            alert("❌ The system cannot add the lesson.");
        } finally {
            setAdding(false);
        }
    };




    const onDeleteLesson = async (lessonId) => {
        if (!window.confirm("Delete this lesson?")) return;
        try {
            await deleteLesson(lessonId);
            setLessons((prev) => prev.filter((l) => String(l?.lessonId ?? l?.id) !== String(lessonId)));
            setMatCounts((m) => {
                const copy = { ...m };
                delete copy[String(lessonId)];
                return copy;
            });
        } catch (e) {
            alert(e?.response?.data?.message || e?.message || "Delete failed.");
        }
    };

    return (
        <div className="course-details-wrap">
            <header className="cd-head container cd-head--clean">
                <div className="cd-meta">
                    <h1 className="cd-title">Manage: {course?.courseTitle ?? course?.title ?? course?.name ?? "Course"}</h1>
                    <div className="cd-actions">
                        <a className="btn" href={`/teacher/courses/${courseId}/edit`}>Edit course</a>
                        <Link className="btn ghost" to="/teacher/courses">Back</Link>
                        <a className="btn" href={`/courses/${courseId}`}>Open public view</a>
                    </div>
                </div>
            </header>

            <section className="container">
                {loading ? (
                    <div className="cd-skeleton"><div className="s1" /><div className="s2" /><div className="s3" /></div>
                ) : err ? (
                    <div className="alert error">{err}</div>
                ) : (
                    <>
                        <h3 className="lsn-title">Lessons</h3>

                        {/* Quick add form */}
                        <form onSubmit={onAddLesson} className="card mf-card">
                            <div className="mf-row">
                                <div className="mf-col">
                                    <label className="mf-label" htmlFor="mf-title">Title</label>
                                    <input id="mf-title" className="mf-input" placeholder="Begin class"
                                        value={title} onChange={(e) => setTitle(e.target.value)} required />
                                </div>

                                <div className="mf-col mf-col--type">
                                    <label className="mf-label" htmlFor="mf-type">Type</label>
                                    <select id="mf-type" className="mf-input" value={typeId} onChange={(e) => setTypeId(e.target.value)}>
                                        <option value="">Select type…</option>
                                        {(types || []).map((t, i) => {
                                            const id = t?.lessonTypeId ?? t?.id ?? i;
                                            const name = t?.lessonTypeName ?? t?.name ?? t?.typeName ?? t?.title ?? String(id);
                                            return <option key={id} value={id}>{name}</option>;
                                        })}
                                    </select>
                                </div>
                            </div>

                            <div className="mf-field">
                                <label className="mf-label" htmlFor="mf-desc">Description</label>
                                <textarea id="mf-desc" className="mf-input mf-textarea" rows={4}
                                    placeholder="Short lesson overview..." value={desc} onChange={(e) => setDesc(e.target.value)} />
                            </div>

                            <div className="mf-opts">
                                <label className="mf-check">
                                    <input type="checkbox" checked={preview} onChange={(e) => setPreview(e.target.checked)} />
                                    <span>Preview</span>
                                </label>
                                <label className="mf-check">
                                    <input type="checkbox" checked={available} onChange={(e) => setAvailable(e.target.checked)} />
                                    <span>Available</span>
                                </label>

                                <div className="mf-actions">
                                    <button className="btn primary" type="submit" disabled={adding || !title.trim()}>
                                        {adding ? "Adding…" : "Add lesson"}
                                    </button>
                                </div>
                            </div>
                        </form>

                        {/* List */}
                        {lessons.length === 0 ? (
                            <div className="empty muted">No lessons yet.</div>
                        ) : (
                            <div className="lesson-list">
                                {lessons.map((l, i) => {
                                    const id = l?.lessonId ?? l?.id ?? i;
                                    const lt = l?.lessonTitle ?? l?.title ?? l?.name ?? "Lesson";
                                    const pv = Boolean(l?.freePreview || l?.preview || l?.isPreview);
                                    const matCount = matCounts[String(id)] ?? 0;

                                    return (
                                        <article key={id} className="lesson-item">
                                            <div className="li-left">
                                                <div className="li-title">
                                                    {lt} {pv && <span className="badge preview">Preview</span>}
                                                </div>
                                                <div className="li-desc muted">{l?.lessonSummary ?? l?.description ?? ""}</div>
                                                <div className="li-meta muted">
                                                    {matCount} material{matCount === 1 ? "" : "s"}
                                                </div>
                                            </div>
                                            <div className="li-actions">
                                                <a className="btn" href={`/teacher/courses/${courseId}/lessons/${id}`}>Edit</a>
                                                <button className="btn ghost" onClick={() => onDeleteLesson(id)}>Delete</button>
                                            </div>
                                        </article>
                                    );
                                })}
                            </div>
                        )}
                    </>
                )}
            </section>
        </div>
    );
}


