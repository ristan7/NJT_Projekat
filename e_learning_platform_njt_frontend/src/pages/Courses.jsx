// src/pages/Courses.jsx
import React, { useEffect, useMemo, useRef, useState } from "react";
import { useSearchParams } from "react-router-dom";
import {
    getCourses,
    getCourseLevels,
    getCourseStatuses,
} from "../api/courses";
import "../css/Courses.css";

const SORTS = [
    { id: "date_desc", label: "Newest first" },
    { id: "date_asc", label: "Oldest first" },
    { id: "title_asc", label: "Title A–Z" },
    { id: "title_desc", label: "Title Z–A" },
    { id: "level_asc", label: "Level A–Z" },
    { id: "level_desc", label: "Level Z–A" },
    { id: "status_asc", label: "Status A–Z" },
    { id: "status_desc", label: "Status Z–A" },
];

// helpers
const toNum = (v, def) => {
    const n = Number(v);
    return Number.isFinite(n) && n >= 0 ? n : def;
};
const cleanParams = (obj = {}) => {
    const out = {};
    Object.entries(obj).forEach(([k, v]) => {
        if (v === undefined || v === null || v === "") return;
        out[k] = String(v);
    });
    return out;
};

export default function Courses() {
    const [searchParams, setSearchParams] = useSearchParams();
    const syncingRef = useRef(false); // spreči loop pri dvosmernoj sinhr.

    // ------------- INIT STATE iz URL-a -------------
    const [q, setQ] = useState(() => searchParams.get("q") || "");
    const [level, setLevel] = useState(() => searchParams.get("level") || "");
    const [status, setStatus] = useState(() => searchParams.get("status") || "");
    const [category, setCategory] = useState(() => searchParams.get("category") || "");
    const [sort, setSort] = useState(() => searchParams.get("sort") || "date_desc");
    const [page, setPage] = useState(() => toNum(searchParams.get("page"), 0));
    const [size, setSize] = useState(() => toNum(searchParams.get("size"), 12));

    // Data
    const [rows, setRows] = useState([]);
    const [total, setTotal] = useState(0);

    // Lookups
    const [levels, setLevels] = useState([]);
    const [statuses, setStatuses] = useState([]);

    // UX
    const [loading, setLoading] = useState(true);
    const [err, setErr] = useState("");

    // ------------- Reakcija na PROMENU URL-a (Back/Forward) -------------
    useEffect(() => {
        if (syncingRef.current) return; // ignoriši kad sami upišemo URL
        setQ(searchParams.get("q") || "");
        setLevel(searchParams.get("level") || "");
        setStatus(searchParams.get("status") || "");
        setCategory(searchParams.get("category") || "");
        setSort(searchParams.get("sort") || "date_desc");
        setPage(toNum(searchParams.get("page"), 0));
        setSize(toNum(searchParams.get("size"), 12));
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [searchParams]);

    // ------------- Upis u URL kad state prom. -------------
    useEffect(() => {
        const next = cleanParams({ q, level, status, category, sort, page, size });
        const current = Object.fromEntries(searchParams.entries());
        const changed =
            Object.keys(next).length !== Object.keys(current).length ||
            Object.keys(next).some((k) => String(current[k] ?? "") !== String(next[k]));

        if (changed) {
            syncingRef.current = true;
            setSearchParams(next, { replace: true });
            // nakon sledećeg tick-a dozvoli reakcije na URL promene
            setTimeout(() => (syncingRef.current = false), 0);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [q, level, status, category, sort, page, size]);

    // Load lookups once
    useEffect(() => {
        (async () => {
            try {
                const [lv, st] = await Promise.all([getCourseLevels(), getCourseStatuses()]);
                setLevels(Array.isArray(lv) ? lv : []);
                setStatuses(Array.isArray(st) ? st : []);
            } catch {
                setLevels([]);
                setStatuses([]);
            }
        })();
    }, []);

    // Load courses whenever filters/sort/page change
    useEffect(() => {
        let alive = true;
        (async () => {
            setLoading(true);
            setErr("");
            try {
                const { rows, total } = await getCourses({
                    q: q || undefined,
                    level: level || undefined,
                    status: status || undefined,
                    category: category || undefined,
                    sort,
                    page,
                    size,
                });
                if (!alive) return;
                setRows(Array.isArray(rows) ? rows : []);
                setTotal(Number.isFinite(total) ? total : 0);
            } catch (e) {
                if (!alive) return;
                const s = e?.response?.status;
                setErr(
                    s === 401
                        ? "Sesija je istekla. Prijavi se ponovo."
                        : s === 403
                            ? "Nemaš dozvolu za ovaj sadržaj."
                            : (e?.response?.data?.message || e?.message || "Failed to load courses.")
                );
                setRows([]);
                setTotal(0);
            } finally {
                if (alive) setLoading(false);
            }
        })();
        return () => {
            alive = false;
        };
    }, [q, level, status, category, sort, page, size]);

    // Reset page na 0 kad se filteri/sort/size promene (ali ne kada samo page menjaš)
    useEffect(() => {
        setPage(0);
    }, [q, level, status, category, sort, size]);

    // Helpers
    const totalPages = useMemo(
        () => Math.max(1, Math.ceil((total || 0) / (size || 1))),
        [total, size]
    );

    const levelName = (val) => {
        if (!val) return "";
        if (typeof val === "string") return val;
        return val?.name || val?.levelName || val?.title || String(val);
    };

    const statusName = (val) => {
        if (!val) return "";
        if (typeof val === "string") return val;
        return val?.name || val?.statusName || val?.title || String(val);
    };

    const lessonCount = (course) => {
        const lessons = course?.lessons;
        if (Array.isArray(lessons)) return lessons.length;
        const n = Number(course?.lessonCount ?? course?.lessonsCount);
        return Number.isFinite(n) ? n : undefined;
    };

    const goTo = (id) => {
        window.location.href = `/courses/${id}`;
    };

    return (
        <div className="courses-wrap">
            <header className="courses-head container">
                <div className="title-side">
                    <h2>Courses</h2>
                    <p className="muted">Browse catalog and find your next lesson.</p>
                </div>

                <div className="filters">
                    <input
                        className="input"
                        placeholder="Search by title or description…"
                        value={q}
                        onChange={(e) => setQ(e.target.value)}
                    />

                    <select
                        className="input"
                        value={level}
                        onChange={(e) => setLevel(e.target.value)}
                        title="Level"
                    >
                        <option value="">All levels</option>
                        {(levels || []).map((x, i) => {
                            const id =
                                x?.id ??
                                x?.levelId ??
                                x?.courseLevelId ??
                                levelName(x) ??
                                i;
                            return (
                                <option key={id} value={x?.code ?? x?.id ?? levelName(x)}>
                                    {levelName(x)}
                                </option>
                            );
                        })}
                    </select>

                    <select
                        className="input"
                        value={status}
                        onChange={(e) => setStatus(e.target.value)}
                        title="Status"
                    >
                        <option value="">All statuses</option>
                        {(statuses || []).map((x, i) => {
                            const id =
                                x?.id ??
                                x?.statusId ??
                                x?.courseStatusId ??
                                statusName(x) ??
                                i;
                            return (
                                <option key={id} value={x?.code ?? x?.id ?? statusName(x)}>
                                    {statusName(x)}
                                </option>
                            );
                        })}
                    </select>

                    <input
                        className="input"
                        placeholder="Category…"
                        value={category}
                        onChange={(e) => setCategory(e.target.value)}
                        title="Category"
                    />

                    <select
                        className="input"
                        value={sort}
                        onChange={(e) => setSort(e.target.value)}
                        title="Sort"
                    >
                        {SORTS.map((s) => (
                            <option key={s.id} value={s.id}>
                                {s.label}
                            </option>
                        ))}
                    </select>

                    <select
                        className="input"
                        value={size}
                        onChange={(e) => setSize(Number(e.target.value))}
                        title="Per page"
                    >
                        {[6, 12, 24, 48].map((n) => (
                            <option key={n} value={n}>
                                {n}/page
                            </option>
                        ))}
                    </select>
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
                    <div className="empty muted">No courses match your filters.</div>
                ) : (
                    <div className="course-grid">
                        {rows.map((c) => {
                            const id =
                                c?.courseId ?? c?.id ?? c?.uuid ?? Math.random().toString(36);
                            const title = c?.title || c?.name || "Untitled course";
                            const desc =
                                c?.description ||
                                c?.shortDescription ||
                                c?.summary ||
                                "";
                            const lvl = levelName(c?.level ?? c?.courseLevel ?? c?.levelName);
                            const st = statusName(
                                c?.status ?? c?.courseStatus ?? c?.statusName
                            );
                            const lessons = lessonCount(c);

                            return (
                                <article key={id} className="course-card">
                                    <div className="thumb" aria-hidden />
                                    <div className="body">
                                        <h3 className="course-title" title={title}>
                                            {title}
                                        </h3>

                                        <div className="meta-line">
                                            {lvl && <span className="pill">{lvl}</span>}
                                            {st && <span className="pill ghost">{st}</span>}
                                            {Number.isFinite(lessons) && (
                                                <span className="muted">
                                                    {lessons} lesson{lessons === 1 ? "" : "s"}
                                                </span>
                                            )}
                                        </div>

                                        {desc && (
                                            <p className="muted clamp-2" title={desc}>
                                                {desc}
                                            </p>
                                        )}

                                        <div className="actions">
                                            <button className="btn primary" onClick={() => goTo(id)}>
                                                View details
                                            </button>
                                            <button
                                                className="btn ghost"
                                                onClick={() => (window.location.href = `/courses/${id}`)}
                                            >
                                                Preview
                                            </button>
                                        </div>
                                    </div>
                                </article>
                            );
                        })}
                    </div>
                )}
            </section>

            {/* Pagination */}
            {!loading && total > 0 && (
                <div className="pager container">
                    <div className="muted">
                        Page <strong>{page + 1}</strong> of <strong>{Math.max(1, Math.ceil((total || 0) / (size || 1)))}</strong> •{" "}
                        <span>{total} total</span>
                    </div>
                    <div className="pager-actions">
                        <button
                            className="pager-btn"
                            disabled={page === 0}
                            onClick={() => setPage((p) => Math.max(0, p - 1))}
                        >
                            ◀ Prev
                        </button>
                        <button
                            className="pager-btn"
                            disabled={page + 1 >= Math.max(1, Math.ceil((total || 0) / (size || 1)))}
                            onClick={() =>
                                setPage((p) => (p + 1 >= Math.max(1, Math.ceil((total || 0) / (size || 1))) ? p : p + 1))
                            }
                        >
                            Next ▶
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
}
