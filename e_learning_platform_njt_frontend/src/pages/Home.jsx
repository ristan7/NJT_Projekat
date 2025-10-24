// src/pages/Home.jsx
import React, { useEffect, useState } from "react";
import { getMe, getNotifications, getRecommendedCourses } from "../api/api";
import "../App.css";
import "../css/Home.css";

export default function Home() {
  const [me, setMe] = useState(null);
  const [notes, setNotes] = useState([]);
  const [recs, setRecs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    (async () => {
      try {
        const u = await getMe();
        setMe(u);

        const uid = u?.id || u?.userId;
        const unreadList = await getNotifications({ userId: uid, unread: true, limit: 3 });
        const r = await getRecommendedCourses();

        setNotes(Array.isArray(unreadList) ? unreadList : []);
        setRecs(Array.isArray(r) ? r : []);
      } catch (e) {
        setNotes([]);
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  return (
    <>
      <section className="hero hero--compressed">
        <div className="container hero-grid">
          <div className="hero-copy">
            <h1>Hi{me?.firstName ? `, ${me.firstName}` : ""}! 👋</h1>
            <p className="muted">Welcome to your dashboard.</p>
            <div className="quick-actions">
              <a className="btn primary" href="/notifications">Go to Notifications</a>
              {/* FIX: vodi u katalog kurseva */}
              <a className="btn ghost" href="/courses">Browse courses</a>
            </div>
          </div>

          <div className="profile-card">
            <div className="avatar xl">
              {me?.firstName || me?.lastName ? (
                <span>
                  {(me?.firstName?.[0] || "U").toUpperCase()}
                  {(me?.lastName?.[0] || "").toUpperCase()}
                </span>
              ) : (
                <span>{(me?.username?.[0] || "U").toUpperCase()}</span>
              )}
            </div>
            <div className="profile-meta">
              <div className="profile-name">
                {[me?.firstName, me?.lastName].filter(Boolean).join(" ") || me?.username}
              </div>
              <div className="muted">{me?.email}</div>
            </div>
          </div>
        </div>
      </section>

      <section className="section">
        <div className="container grid-2">
          <div className="card">
            <div className="card-head">
              <h3>Unread notifications</h3>
              <a className="link" href="/notifications">See all</a>
            </div>

            {loading ? (
              <div className="skeleton-list">
                <div className="skeleton" />
                <div className="skeleton" />
                <div className="skeleton" />
              </div>
            ) : notes.length === 0 ? (
              <p className="muted">No new notifications.</p>
            ) : (
              <ul className="note-list" role="list">
                {notes.map((n) => {
                  const id = n.notificationId ?? n.id;
                  return (
                    <li key={id} className="note-item" role="listitem">
                      <div className="note-main">
                        <div className="note-title">{n.notificationTitle || n.title}</div>
                        <div className="note-text muted">
                          {n.message || n.notificationMessage || n.body}
                        </div>
                      </div>
                    </li>
                  );
                })}
              </ul>
            )}
          </div>

          <div className="card">
            <div className="card-head">
              <h3>Recommended courses</h3>
              {/* FIX: link ka katalogu */}
              <a className="link" href="/courses">Catalog</a>
            </div>
            <div className="courses">
              {recs.map((c) => (
                // FIX: detalj kursa => /courses/:id (plural)
                <a key={c.id} className="course" href={`/courses/${c.id}`}>
                  <div className="thumb" />
                  <div className="course-title">{c.title}</div>
                  <div className="muted">{c.meta}</div>
                </a>
              ))}
              {recs.length === 0 && !loading && (
                <p className="muted">Recommendations coming soon.</p>
              )}
            </div>
          </div>
        </div>
      </section>
    </>
  );
}
