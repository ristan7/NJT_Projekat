import React, { useEffect, useMemo, useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import {
    getLesson,
    updateLesson,
    patchLessonAvailable,
    patchLessonPreview,
    patchLessonType,
    getLessonTypes,
    getMaterialsByLesson,
    getMaterialTypes,
    replaceMaterials, // bulk save
} from "../api/courses";
import "../css/LessonView.css";

/* ---------- helpers ---------- */
const mapLessonDtoToForm = (dto = {}) => ({
    title: dto.lessonTitle ?? dto.title ?? "",
    description: dto.lessonSummary ?? dto.description ?? "",
    lessonTypeId: dto.lessonTypeId ?? dto.lessonType?.lessonTypeId ?? dto.typeId ?? "",
    preview: Boolean(dto.freePreview ?? dto.preview),
    available: Boolean(dto.lessonAvailable ?? dto.available ?? true),
});

const optIdOf = (x, i) => x?.id ?? x?.lessonTypeId ?? i;
const optNameOf = (x, id) => x?.name ?? x?.lessonTypeName ?? `Type ${id}`;

const matIdOf = (x, i) => x?.id ?? x?.materialId ?? i;
const matNameOf = (x, id) => x?.name ?? x?.materialTypeName ?? `Type ${id}`;

export default function LessonEditor() {
    const { courseId, lessonId } = useParams();
    const navigate = useNavigate();

    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");

    // lookups
    const [lessonTypes, setLessonTypes] = useState([]);
    const [materialTypes, setMaterialTypes] = useState([]);

    // form state
    const [title, setTitle] = useState("");
    const [desc, setDesc] = useState("");
    const [typeId, setTypeId] = useState("");
    const [preview, setPreview] = useState(false);
    const [available, setAvailable] = useState(true);

    // materials (OFFLINE)
    const [materials, setMaterials] = useState([]);
    const [mTitle, setMTitle] = useState("");
    const [mUrl, setMUrl] = useState("");
    const [mTypeId, setMTypeId] = useState("");
    const [mContent, setMContent] = useState("");

    const pageTitle = useMemo(() => title || "Lesson editor", [title]);

    useEffect(() => {
        let mounted = true;
        (async () => {
            try {
                setLoading(true);
                setError("");

                const [lt, mt] = await Promise.all([getLessonTypes(), getMaterialTypes()]);
                if (!mounted) return;
                setLessonTypes(Array.isArray(lt) ? lt : []);
                setMaterialTypes(Array.isArray(mt) ? mt : []);

                const ls = await getLesson(lessonId);
                if (!mounted) return;
                const mapped = mapLessonDtoToForm(ls);
                setTitle(mapped.title);
                setDesc(mapped.description);
                setTypeId(mapped.lessonTypeId || "");
                setPreview(mapped.preview);
                setAvailable(mapped.available);

                const mats = await getMaterialsByLesson(lessonId);
                if (!mounted) return;

                const list = (Array.isArray(mats) ? mats : [])
                    .sort((a, b) => (a.materialOrderIndex ?? 0) - (b.materialOrderIndex ?? 0))
                    .map((m) => ({
                        tempId: crypto.randomUUID(),
                        materialId: m.materialId ?? null,
                        materialTitle: m.materialTitle ?? "",
                        resourceUrl: m.resourceUrl ?? "",
                        content: m.content ?? "",
                        materialTypeId: m.materialTypeId ?? "",
                        materialOrderIndex: m.materialOrderIndex ?? 1,
                        lessonId: Number(lessonId),
                    }));

                setMaterials(list);
            } catch (e) {
                setError(e?.response?.data?.message || "Greška pri učitavanju lekcije.");
            } finally {
                if (mounted) setLoading(false);
            }
        })();
        return () => {
            mounted = false;
        };
    }, [courseId, lessonId]);

    async function handleSaveLesson(e) {
        e.preventDefault();
        try {
            setSaving(true);
            setError("");

            // 1) update lekcije
            const payload = {
                title: String(title || "").trim(),
                description: String(desc || "").trim(),
                lessonTypeId: typeId ? Number(typeId) : null,
                preview: Boolean(preview),
                available: Boolean(available),
                courseId: Number(courseId),
            };
            await updateLesson(lessonId, payload);

            // 2) bulk materials
            const cleaned = materials.map(({ tempId, createdAt, updatedAt, ...m }, idx) => ({
                ...m,
                lessonId: Number(lessonId),
                materialOrderIndex: m.materialOrderIndex != null ? Number(m.materialOrderIndex) : idx + 1,
                materialId: m.materialId != null ? Number(m.materialId) : null,
                materialTypeId: m.materialTypeId != null ? Number(m.materialTypeId) : null,
                materialTitle: String(m.materialTitle ?? "").trim(),
                resourceUrl: String(m.resourceUrl ?? "").trim(),
                content: m.content ?? "",
            }));
            await replaceMaterials(Number(lessonId), cleaned);

            // 3) close → My courses
            navigate("/teacher/courses", { replace: true });
            return;
        } catch (e) {
            setError(e?.response?.data?.message || "Nije moguće sačuvati lekciju.");
        } finally {
            setSaving(false);
        }
    }

    // toggles
    async function onTogglePreview() {
        try {
            const next = !preview;
            setPreview(next);
            await patchLessonPreview(courseId, lessonId, next);
        } catch {
            setPreview((v) => !v);
        }
    }
    async function onToggleAvailable() {
        try {
            const next = !available;
            setAvailable(next);
            await patchLessonAvailable(courseId, lessonId, next);
        } catch {
            setAvailable((v) => !v);
        }
    }
    async function onChangeType(e) {
        const newId = e.target.value || "";
        setTypeId(newId);
        if (!newId) return;
        try {
            await patchLessonType(courseId, lessonId, Number(newId));
        } catch {
            /* ignore */
        }
    }

    // offline add/delete
    const canAdd = useMemo(() => {
        const t = (mTitle || "").trim();
        const hasPayload = !!(mUrl?.trim() || mContent?.trim());
        return !!t && !!mTypeId && hasPayload;
    }, [mTitle, mUrl, mContent, mTypeId]);

    function handleAddMaterial(e) {
        e.preventDefault();
        if (!canAdd) return;

        setMaterials((prev) => [
            ...prev,
            {
                tempId: crypto.randomUUID(),
                materialId: null,
                materialTitle: mTitle.trim(),
                resourceUrl: (mUrl || "").trim(),
                content: mContent || "",
                materialTypeId: Number(mTypeId),
                materialOrderIndex: prev.length + 1,
                lessonId: Number(lessonId),
            },
        ]);

        setMTitle("");
        setMUrl("");
        setMTypeId("");
        setMContent("");
    }

    function handleDeleteMaterial(tempKey) {
        setMaterials((prev) =>
            prev.filter((m) => m.tempId !== tempKey).map((m, idx) => ({ ...m, materialOrderIndex: idx + 1 }))
        );
    }

    if (loading)
        return (
            <div className="container narrow">
                <p>Učitavanje…</p>
            </div>
        );

    return (
        <div className="container narrow">
            <div className="top-bar">
                <h1>{pageTitle}</h1>
                <div className="actions">
                    <Link to={`/courses/${courseId}/lessons/${lessonId}/public`} className="link">
                        Open public view
                    </Link>
                    <button className="link" onClick={() => navigate(-1)}>
                        Back
                    </button>
                </div>
            </div>

            {error && <div className="alert error">{error}</div>}

            {/* LESSON */}
            <section className="card">
                <h3>Lesson</h3>
                <form onSubmit={handleSaveLesson} className="form-grid">
                    <label>
                        <span>Title</span>
                        <input value={title} onChange={(e) => setTitle(e.target.value)} placeholder="Title" />
                    </label>

                    <label className="col-2">
                        <span>Description</span>
                        <textarea value={desc} onChange={(e) => setDesc(e.target.value)} placeholder="Basics..." rows={3} />
                    </label>

                    <label>
                        <span>Type</span>
                        <select value={typeId || ""} onChange={onChangeType}>
                            <option value="">—</option>
                            {lessonTypes.map((t, i) => {
                                const id = optIdOf(t, i);
                                return (
                                    <option key={id} value={id}>
                                        {optNameOf(t, id)}
                                    </option>
                                );
                            })}
                        </select>
                    </label>

                    <label className="checkbox">
                        <input type="checkbox" checked={preview} onChange={onTogglePreview} />
                        <span>Preview</span>
                    </label>
                    <label className="checkbox">
                        <input type="checkbox" checked={available} onChange={onToggleAvailable} />
                        <span>Available</span>
                    </label>

                    <div />
                    <button className="btn primary" disabled={saving || !title.trim()}>
                        {saving ? "Saving..." : "Save lesson"}
                    </button>
                </form>
            </section>

            {/* MATERIALS (OFFLINE) */}
            <section className="card">
                <h3>Materials</h3>

                <form onSubmit={handleAddMaterial} className="form-grid">
                    <label>
                        <span>Title</span>
                        <input value={mTitle} onChange={(e) => setMTitle(e.target.value)} placeholder="Title" />
                    </label>
                    <label className="col-2">
                        <span>URL</span>
                        <input value={mUrl} onChange={(e) => setMUrl(e.target.value)} placeholder="https://…" />
                    </label>
                    <label>
                        <span>Material type</span>
                        <select value={mTypeId} onChange={(e) => setMTypeId(e.target.value)}>
                            <option value="">—</option>
                            {materialTypes.map((t, i) => {
                                const id = matIdOf(t, i);
                                return (
                                    <option key={id} value={id}>
                                        {matNameOf(t, id)}
                                    </option>
                                );
                            })}
                        </select>
                    </label>
                    <label className="col-2">
                        <span>Content (optional)</span>
                        <textarea
                            value={mContent}
                            onChange={(e) => setMContent(e.target.value)}
                            placeholder="Optional text content…"
                            rows={2}
                        />
                    </label>
                    <div />
                    <button className="btn" disabled={!canAdd}>
                        Add material
                    </button>
                </form>

                {materials.length === 0 ? (
                    <p className="muted center">No materials.</p>
                ) : (
                    <div className="table">
                        <div className="thead">
                            <div>#</div>
                            <div>Title</div>
                            <div>URL</div>
                            <div>Type</div>
                            <div className="right">Actions</div>
                        </div>

                        {materials
                            .sort((a, b) => (a.materialOrderIndex ?? 0) - (b.materialOrderIndex ?? 0))
                            .map((m, idx) => (
                                <div className="trow" key={m.tempId}>
                                    <div>{m.materialOrderIndex ?? idx + 1}</div>
                                    <div className="ellipsis">{m.materialTitle}</div>
                                    <div className="ellipsis">
                                        {m.resourceUrl ? (
                                            <a href={m.resourceUrl} target="_blank" rel="noreferrer">
                                                {m.resourceUrl}
                                            </a>
                                        ) : (
                                            <em className="muted">—</em>
                                        )}
                                    </div>
                                    <div>
                                        {materialTypes.find((t) => String(t.id) === String(m.materialTypeId))?.name || "?"}
                                    </div>
                                    <div className="right">
                                        <button type="button" className="btn danger" onClick={() => handleDeleteMaterial(m.tempId)}>
                                            Delete
                                        </button>
                                    </div>
                                </div>
                            ))}
                    </div>
                )}
            </section>
        </div>
    );
}
