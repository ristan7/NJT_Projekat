// src/pages/Notifications.jsx
import React, { useEffect, useMemo, useState } from "react";
import {
  getMe,
  getNotifications,
  markNotificationRead,
  markAllNotificationsRead,
  getNotificationTypes,
  deleteNotification,
} from "../api/api";
import { emitUnreadChanged } from "../api/notificationsBus";
import { Link } from "react-router-dom";
import "../css/Notifications.css";

export default function Notifications() {
  const [me, setMe] = useState(null);
  const [items, setItems] = useState([]);
  const [types, setTypes] = useState([]);
  const [loading, setLoading] = useState(true);

  // UI state
  const [onlyUnread, setOnlyUnread] = useState(false);
  const [query, setQuery] = useState("");
  const [sortKey, setSortKey] = useState("date_desc"); // date_asc | title_asc | title_desc | type_asc | type_desc
  const [typeFilter, setTypeFilter] = useState("all");

  // pagination
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  // helper: da li je user admin
  const isAdmin = (u) =>
    (u?.role?.roleName || u?.role?.name || "").toUpperCase() === "ADMIN";

  // ------------------------------ Load ------------------------------
  useEffect(() => {
    (async () => {
      try {
        setLoading(true);

        const u = await getMe();
        setMe(u);
        const uid = u?.id ?? u?.userId;

        const [tps, list] = await Promise.all([
          getNotificationTypes(),
          getNotifications({ userId: uid, unread: false, limit: 500 }),
        ]);

        setTypes(Array.isArray(tps) ? tps : []);
        setItems(Array.isArray(list) ? list : []);
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  // ------------------------------ Helpers ------------------------------
  const typesMap = useMemo(() => {
    const m = new Map();
    for (const t of types || []) {
      if (!t) continue;
      const id = Number(t.id);
      if (Number.isFinite(id)) {
        m.set(id, t.notificationTypeName || t.name || "");
      }
    }
    return m;
  }, [types]);

  const getTypeId = (n) =>
    n?.notificationTypeId ??
    n?.typeId ??
    n?.type?.id ??
    n?.notificationType?.id ??
    null;

  // ------------------------------ Visible (filter/sort) ------------------------------
  const filteredSorted = useMemo(() => {
    let arr = Array.isArray(items) ? [...items] : [];

    if (onlyUnread) arr = arr.filter((n) => !n.read);

    if (typeFilter !== "all") {
      const idNum = Number(typeFilter);
      arr = arr.filter((n) => Number(getTypeId(n)) === idNum);
    }

    if (query.trim()) {
      const q = query.trim().toLowerCase();
      arr = arr.filter((n) => {
        const title = (n.notificationTitle || n.title || "").toLowerCase();
        const msg = (n.message || n.notificationMessage || n.body || "").toLowerCase();
        const tName = (typesMap.get(Number(getTypeId(n))) || "").toLowerCase();
        return title.includes(q) || msg.includes(q) || tName.includes(q);
      });
    }

    arr.sort((a, b) => {
      switch (sortKey) {
        case "title_asc":
        case "title_desc": {
          const A = (a.notificationTitle || a.title || "").toLowerCase();
          const B = (b.notificationTitle || b.title || "").toLowerCase();
          const cmp = A.localeCompare(B);
          return sortKey === "title_asc" ? cmp : -cmp;
        }
        case "type_asc":
        case "type_desc": {
          const A = (typesMap.get(Number(getTypeId(a))) || "").toLowerCase();
          const B = (typesMap.get(Number(getTypeId(b))) || "").toLowerCase();
          const cmp = A.localeCompare(B);
          return sortKey === "type_asc" ? cmp : -cmp;
        }
        case "date_asc":
        case "date_desc":
        default: {
          const A = new Date(a.sentAt || a.createdAt || a.date || 0).getTime();
          const B = new Date(b.sentAt || b.createdAt || b.date || 0).getTime();
          return sortKey === "date_asc" ? A - B : B - A;
        }
      }
    });

    return arr;
  }, [items, onlyUnread, query, sortKey, typeFilter, typesMap]);

  // slice for current page
  const paged = useMemo(() => {
    const start = page * rowsPerPage;
    const end = start + rowsPerPage;
    return filteredSorted.slice(start, end);
  }, [filteredSorted, page, rowsPerPage]);

  // reset to first page on filters/sort change
  useEffect(() => {
    setPage(0);
  }, [onlyUnread, query, sortKey, typeFilter, rowsPerPage]);

  // ------------------------------ Actions ------------------------------
  async function handleMarkRead(id) {
    const prev = items;
    setItems((list) =>
      list.map((n) => (n.notificationId === id || n.id === id ? { ...n, read: true } : n))
    );
    try {
      await markNotificationRead(id);
      emitUnreadChanged();
    } catch {
      setItems(prev);
    }
  }

  async function handleMarkAll() {
    if (!me) return;
    const uid = me.id ?? me.userId;
    const prev = items;
    setItems((list) => list.map((n) => ({ ...n, read: true })));
    try {
      await markAllNotificationsRead(uid);
      emitUnreadChanged();
    } catch {
      setItems(prev);
    }
  }

  async function handleDelete(id) {
    const prev = items;
    setItems((list) => list.filter((n) => (n.notificationId ?? n.id) !== id));
    try {
      await deleteNotification(id);
      emitUnreadChanged();
    } catch {
      setItems(prev);
    }
  }

  // ------------------------------ Render ------------------------------
  const nameOrUsername =
    [me?.firstName, me?.lastName].filter(Boolean).join(" ") || me?.username || "";

  return (
    <div className="page-wrap">
      <header className="n-head">
        <div className="n-left">
          <div className="who">
            {nameOrUsername} • <span className="muted">{me?.email}</span>
          </div>

          <div className="n-filters">
            <input
              className="n-search"
              placeholder="Search by title, message or type…"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
            />

            <label className="n-check">
              <input
                type="checkbox"
                checked={onlyUnread}
                onChange={(e) => setOnlyUnread(e.target.checked)}
              />
              <span>Only unread</span>
            </label>

            <select
              className="n-select"
              value={typeFilter}
              onChange={(e) => setTypeFilter(e.target.value)}
            >
              <option value="all">All types</option>
              {(types || []).map((t) => (
                <option key={t.id} value={t.id}>
                  {t.notificationTypeName || t.name}
                </option>
              ))}
            </select>

            <select
              className="n-select"
              value={sortKey}
              onChange={(e) => setSortKey(e.target.value)}
            >
              <option value="date_desc">Date (new first)</option>
              <option value="date_asc">Date (old first)</option>
              <option value="title_asc">Title A–Z</option>
              <option value="title_desc">Title Z–A</option>
              <option value="type_asc">Type A–Z</option>
              <option value="type_desc">Type Z–A</option>
            </select>
          </div>
        </div>

        <div className="n-right">
          {isAdmin(me) && (
            <Link to="/notifications/new" className="btn ghost">
              + New notification
            </Link>
          )}
          <button className="btn primary" onClick={handleMarkAll}>
            Mark all as read
          </button>
        </div>
      </header>

      <div className="card n-card">
        {/* HEADER ROW */}
        <div className="n-table head">
          <div>#</div>
          <div>Title</div>
          <div>Type</div>
          <div>Message</div>
          <div>Sent</div>
          <div>Actions</div>
        </div>

        {/* BODY */}
        {loading ? (
          <div className="n-skeleton">Loading…</div>
        ) : paged.length === 0 ? (
          <div className="n-empty muted">No results for the current filter/search.</div>
        ) : (
          paged.map((n, idx) => {
            const id = n.notificationId ?? n.id;
            const title = n.notificationTitle || n.title || "—";
            const msg = n.message || n.notificationMessage || n.body || "—";
            const sent = n.sentAt || n.createdAt || n.date;
            const tId = Number(getTypeId(n));
            const tName = typesMap.get(tId) || "—";

            return (
              <div key={id} className={`n-table row ${n.read ? "is-read" : ""}`}>
                <div>{page * rowsPerPage + idx + 1}</div>
                <div className="n-title">{title}</div>
                <div>
                  <span className="type-pill">{tName}</span>
                </div>
                <div className="n-msg">{msg}</div>
                <div className="n-date">
                  {sent ? new Date(sent).toLocaleString() : "—"}
                </div>
                <div className="n-actions gap">
                  {n.read ? (
                    <span className="badge read">Read</span>
                  ) : (
                    <button className="btn ghost sm" onClick={() => handleMarkRead(id)}>
                      Mark read
                    </button>
                  )}
                  <button className="btn danger sm" onClick={() => handleDelete(id)}>
                    Delete
                  </button>
                </div>
              </div>
            );
          })
        )}

        {/* PAGINATION */}
        {!loading && filteredSorted.length > 0 && (
          <div className="n-pagination">
            <div className="n-rows">
              Rows per page:&nbsp;
              <select
                className="n-select sm"
                value={rowsPerPage}
                onChange={(e) => setRowsPerPage(Number(e.target.value))}
              >
                {[5, 10, 25, 50, 100].map((n) => (
                  <option key={n} value={n}>
                    {n}
                  </option>
                ))}
              </select>
              <span className="muted">
                &nbsp;&nbsp;
                {filteredSorted.length === 0
                  ? "0–0 of 0"
                  : `${page * rowsPerPage + 1}–${Math.min(
                    (page + 1) * rowsPerPage,
                    filteredSorted.length
                  )} of ${filteredSorted.length}`}
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
                disabled={(page + 1) * rowsPerPage >= filteredSorted.length}
                onClick={() =>
                  setPage((p) =>
                    (p + 1) * rowsPerPage >= filteredSorted.length ? p : p + 1
                  )
                }
                aria-label="Next"
              >
                ▶
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
