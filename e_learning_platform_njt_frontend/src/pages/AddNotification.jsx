// src/pages/AddNotification.jsx
import React, { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getMe, getUsers, getNotificationTypes } from "../api/api";
import "../css/AddNotification.css";
import http from "../api/http";

export default function AddNotification() {
    const nav = useNavigate();

    const [me, setMe] = useState(null);
    const [loaded, setLoaded] = useState(false);

    const [title, setTitle] = useState("");
    const [messageFront, setMessage] = useState("");
    const [userId, setUserId] = useState("");
    const [typeId, setTypeId] = useState("");

    const [allUsers, setAllUsers] = useState([]);
    const [types, setTypes] = useState([]);

    const [sending, setSending] = useState(false);
    const [errors, setErrors] = useState({});

    const isAdmin = (u) => (u?.roleId === 3) || ((u?.roleName ?? "").toUpperCase() === "ADMIN");

    useEffect(() => {
        (async () => {
            try {
                const u = await getMe();
                setMe(u);

                // samo admin može na ovu stranu
                if (!isAdmin(u)) {
                    nav("/notifications", { replace: true });
                    return;
                }

                const [users, tps] = await Promise.all([getUsers(), getNotificationTypes()]);
                setAllUsers(Array.isArray(users) ? users : []);
                setTypes(Array.isArray(tps) ? tps : []);
            } catch (e) {
                console.error(e);
            } finally {
                setLoaded(true);
            }
        })();
    }, [nav]);

    const meId = me?.id ?? me?.userId;

    const selectableUsers = useMemo(() => {
        const list = Array.isArray(allUsers) ? allUsers : [];
        if (!meId) return list;
        return list.filter((u) => Number(u.id ?? u.userId) !== Number(meId));
    }, [allUsers, meId]);

    function userLabel(u) {
        const name = [u.firstName, u.lastName].filter(Boolean).join(" ");
        return `${name || u.username || "User"} • ${u.email || ""}`.trim();
    }

    function validate() {
        const e = {};
        if (!title.trim()) e.title = "Title is required.";
        if (!messageFront.trim()) e.message = "Message is required.";
        if (!userId) e.userId = "Select a user.";
        if (!typeId) e.typeId = "Select notification type.";
        setErrors(e);
        return Object.keys(e).length === 0;
    }

    async function handleSubmit(e) {
        e.preventDefault();
        if (!validate()) return;

        setSending(true);
        try {
            const uid = Number(userId);
            const tid = Number(typeId);

            // Šaljemo i “flat” i imena koja koristi tvoj bek (robusno);
            // Jackson ignoriše nepoznata polja.
            const payload = {
                // flat
                title: title,
                message: messageFront,
                userId: uid,
                typeId: tid,

                // po tvojim nazivima u entitetima/DTO
                notificationTitle: title,
                notificationMessage: messageFront,
                notificationTypeId: tid,

                // ako bek očekuje objekte (u nekim verzijama)
                user: { userId: uid },
                notificationType: { notificationTypeId: tid },
            };

            await http.post(`/notifications`, payload); // baseURL = /api, JWT interceptor aktivan
            nav("/notifications");
        } catch (err) {
            console.error(err);
            alert(err?.response?.data?.message || err.message || "Error while sending notification.");
        } finally {
            setSending(false);
        }
    }

    if (loaded && me && !isAdmin(me)) {
        return (
            <div className="nf-wrap">
                <div className="nf-card">
                    <h3>403 · Forbidden</h3>
                    <p className="muted">Only administrators can send notifications.</p>
                </div>
            </div>
        );
    }

    return (
        <div className="nf-wrap">
            <form className="nf-card" onSubmit={handleSubmit}>
                <div className="nf-title">
                    <span className="nf-emoji">📣</span>
                    <span>Create new notification</span>
                </div>

                <div className="nf-field">
                    <label className="nf-label" htmlFor="nf-title">Title</label>
                    <input
                        id="nf-title"
                        className={`nf-input ${errors.title ? "invalid" : ""}`}
                        placeholder="Notification title"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                    />
                    {errors.title && <div className="nf-err">{errors.title}</div>}
                </div>

                <div className="nf-field">
                    <label className="nf-label" htmlFor="nf-message">Message</label>
                    <textarea
                        id="nf-message"
                        className={`nf-input nf-textarea ${errors.message ? "invalid" : ""}`}
                        placeholder="Message"
                        rows={6}
                        value={messageFront}
                        onChange={(e) => setMessage(e.target.value)}
                    />
                    {errors.message && <div className="nf-err">{errors.message}</div>}
                </div>

                <div className="nf-row">
                    <div className="nf-col">
                        <label className="nf-label" htmlFor="nf-user">User</label>
                        <select
                            id="nf-user"
                            className={`nf-input ${errors.userId ? "invalid" : ""}`}
                            value={userId}
                            onChange={(e) => setUserId(e.target.value)}
                        >
                            <option value="">— Select user —</option>
                            {selectableUsers.map((u) => (
                                <option key={u.id ?? u.userId} value={u.id ?? u.userId}>
                                    {userLabel(u)}
                                </option>
                            ))}
                        </select>
                        {errors.userId && <div className="nf-err">{errors.userId}</div>}
                    </div>

                    <div className="nf-col">
                        <label className="nf-label" htmlFor="nf-type">Type</label>
                        <select
                            id="nf-type"
                            className={`nf-input ${errors.typeId ? "invalid" : ""}`}
                            value={typeId}
                            onChange={(e) => setTypeId(e.target.value)}
                        >
                            <option value="">— Select type —</option>
                            {(types || []).map((t) => (
                                <option
                                    key={t.notificationTypeId}
                                    value={t.notificationTypeId}
                                >
                                    {t.notificationTypeName}
                                </option>
                            ))}
                        </select>
                        {errors.typeId && <div className="nf-err">{errors.typeId}</div>}
                    </div>
                </div>

                <div className="nf-actions">
                    <button
                        type="button"
                        className="btn ghost"
                        onClick={() => nav("/notifications")}
                    >
                        Cancel
                    </button>
                    <button className="btn primary" disabled={sending}>
                        {sending ? "Sending…" : "Send"}
                    </button>
                </div>
            </form>
        </div>
    );
}
