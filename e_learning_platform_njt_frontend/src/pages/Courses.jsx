import React, { useEffect, useRef, useState } from "react";
import { useSearchParams } from "react-router-dom";
import {
    getCourses,
    getCourseLevels,
    getCourseStatuses,
    getLessonsCount, // broj lekcija po kursu
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
    const syncingRef = useRef(false);

    // init iz URL-a
    const [q, setQ] = useState(() => searchParams.get("q") || "");
    const [level, setLevel] = useState(() => searchParams.get("level") || "");
    const [status, setStatus] = useState(() => searchParams.get("status") || "PUBLISHED");
    const [category, setCategory] = useState(() => searchParams.get("category") || "");
    const [sort, setSort] = useState(() => searchParams.get("sort") || "date_desc");
    const [page, setPage] = useState(() => toNum(searchParams.get("page"), 0));
    const [size, setSize] = useState(() => toNum(searchParams.get("size"), 12));

    // data
    const [rows, setRows] = useState([]);
    const [total, setTotal] = useState(0);
    const [lessonCounts, setLessonCounts] = useState({}); // { [courseId]: number }

    // lookups
    const [levels, setLevels] = useState([]);
    const [statuses, setStatuses] = useState([]);

    // UX
    const [loading, setLoading] = useState(true);
    const [err, setErr] = useState("");

    // reakcija na promenu URL-a
    useEffect(() => {
        if (syncingRef.current) return;
        setQ(searchParams.get("q") || "");
        setLevel(searchParams.get("level") || "");
        setStatus(searchParams.get("status") || "");
        setCategory(searchParams.get("category") || "");
        setSort(searchParams.get("sort") || "date_desc");
        setPage(toNum(searchParams.get("page"), 0));
        setSize(toNum(searchParams.get("size"), 12));
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [searchParams]);

    // upis u URL
    useEffect(() => {
        const next = cleanParams({ q, level, status, category, sort, page, size });
        const current = Object.fromEntries(searchParams.entries());
        const changed =
            Object.keys(next).length !== Object.keys(current).length ||
            Object.keys(next).some((k) => String(current[k] ?? "") !== String(next[k]));

        if (changed) {
            syncingRef.current = true;
            setSearchParams(next, { replace: true });
            setTimeout(() => (syncingRef.current = false), 0);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [q, level, status, category, sort, page, size]);

    // lookups
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

    // učitaj kurseve + counts
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
                const list = Array.isArray(rows) ? rows : [];
                setRows(list);
                setTotal(Number.isFinite(total) ? total : 0);

                // counts
                const pairs = await Promise.all(
                    list.map(async (c) => {
                        const id = c?.courseId ?? c?.id;
                        if (!id) return [null, 0];
                        try {
                            const n = await getLessonsCount(id);
                            return [String(id), Number(n) || 0];
                        } catch {
                            return [String(id), Array.isArray(c?.lessons) ? c.lessons.length : (c?.lessonCount ?? 0)];
                        }
                    })
                );
                if (alive) setLessonCounts(Object.fromEntries(pairs.filter(([k]) => k != null)));
            } catch (e) {
                if (!alive) return;
                const s = e?.response?.status;
                setErr(
                    s === 401
                        ? "Sesija je istekla. Prijavi se ponovo."
                        : s === 403
                            ? "Nemaš dozvolu za ovaj sadržaj."
                            : e?.response?.data?.message || e?.message || "Failed to load courses."
                );
                setRows([]);
                setTotal(0);
                setLessonCounts({});
            } finally {
                if (alive) setLoading(false);
            }
        })();
        return () => {
            alive = false;
        };
    }, [q, level, status, category, sort, page, size]);

    // resetuj page pri promeni filtera/sorta
    useEffect(() => {
        setPage(0);
    }, [q, level, status, category, sort, size]);

    const levelName = (val) => {
        if (!val) return "";
        if (typeof val === "string") return val;
        return val?.levelName || val?.courseLevelName || val?.name || val?.title || String(val);
    };
    const statusName = (val) => {
        if (!val) return "";
        if (typeof val === "string") return val;
        return val?.statusName || val?.courseStatusName || val?.name || val?.title || String(val);
    };

    const lessonCount = (course) => {
        const id = course?.courseId ?? course?.id;
        const n = lessonCounts[String(id)];
        return Number.isFinite(n) ? n : 0;
    };

    const goTo = (id) => (window.location.href = `/courses/${id}`);

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

                    <select className="input" value={level} onChange={(e) => setLevel(e.target.value)} title="Level">
                        <option value="">All levels</option>
                        {(levels || []).map((x, i) => {
                            const id = x?.courseLevelId ?? x?.levelId ?? x?.id ?? i;
                            const label = x?.courseLevelName ?? x?.levelName ?? x?.name ?? `Level ${id}`;
                            return (
                                <option key={id} value={id}>
                                    {label}
                                </option>
                            );
                        })}
                    </select>

                    <select className="input" value={status} onChange={(e) => setStatus(e.target.value)} title="Status">
                        <option value="">All statuses</option>
                        {(statuses || []).map((x, i) => {
                            const id = x?.courseStatusId ?? x?.statusId ?? x?.id ?? i;
                            const label = x?.courseStatusName ?? x?.statusName ?? x?.name ?? `Status ${id}`;
                            return (
                                <option key={id} value={id}>
                                    {label}
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

                    <select className="input" value={sort} onChange={(e) => setSort(e.target.value)} title="Sort">
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
                            const id = c?.courseId ?? c?.id ?? c?.uuid ?? Math.random().toString(36);
                            const title = c?.courseTitle ?? c?.title ?? c?.name ?? "Untitled course";
                            const desc = c?.courseDescription ?? c?.description ?? c?.shortDescription ?? c?.summary ?? "";
                            const lvl = levelName(c?.level ?? c?.courseLevel ?? c?.levelName);
                            const st = statusName(c?.status ?? c?.courseStatus ?? c?.statusName);
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
                                            <span className="muted">
                                                {lessons} lesson{lessons === 1 ? "" : "s"}
                                            </span>
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
                                            <button className="btn ghost" onClick={() => (window.location.href = `/courses/${id}`)}>
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

            {!loading && total > 0 && (
                <div className="pager container">
                    <div className="muted">
                        Page <strong>{page + 1}</strong> of{" "}
                        <strong>{Math.max(1, Math.ceil((total || 0) / (size || 1)))}</strong> •{" "}
                        <span>{total} total</span>
                    </div>
                    <div className="pager-actions">
                        <button className="pager-btn" disabled={page === 0} onClick={() => setPage((p) => Math.max(0, p - 1))}>
                            ◀ Prev
                        </button>
                        <button
                            className="pager-btn"
                            disabled={page + 1 >= Math.max(1, Math.ceil((total || 0) / (size || 1)))}
                            onClick={() =>
                                setPage((p) =>
                                    p + 1 >= Math.max(1, Math.ceil((total || 0) / (size || 1))) ? p : p + 1
                                )
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
