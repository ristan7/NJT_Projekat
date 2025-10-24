// src/pages/AdminEnrollments.jsx
import React, { useEffect, useState } from "react";
import { getUsers } from "../api/api";
import { getAllCoursesSimple } from "../api/enrollments";
import { adminCreateEnrollment } from "../api/enrollments";

export default function AdminEnrollments() {
    const [users, setUsers] = useState([]);
    const [courses, setCourses] = useState([]);
    const [userId, setUserId] = useState("");
    const [courseId, setCourseId] = useState("");
    const [days, setDays] = useState(365);
    const [busy, setBusy] = useState(false);
    const [msg, setMsg] = useState("");

    useEffect(() => {
        (async () => {
            try {
                const [us, cs] = await Promise.all([getUsers(), getAllCoursesSimple()]);
                setUsers(Array.isArray(us) ? us : []);
                setCourses(Array.isArray(cs) ? cs : []);
            } catch (e) {
                setMsg("Greška pri učitavanju podataka.");
            }
        })();
    }, []);

    async function submit(e) {
        e.preventDefault();
        setMsg("");
        if (!userId || !courseId) { setMsg("Izaberi korisnika i kurs."); return; }
        try {
            setBusy(true);
            await adminCreateEnrollment({ userId: Number(userId), courseId: Number(courseId), daysValid: Number(days) });
            setMsg("Enrollment kreiran.");
        } catch (e) {
            setMsg(e?.response?.data?.message || e?.message || "Kreiranje nije uspelo.");
        } finally {
            setBusy(false);
        }
    }

    return (
        <div className="container" style={{ padding: 24 }}>
            <h2>Admin • Create enrollment</h2>
            <p className="muted">Dodeli pristup kursu korisniku.</p>

            <form onSubmit={submit} className="card" style={{ display: "grid", gap: 12, maxWidth: 520 }}>
                <label style={{ display: "grid", gap: 6 }}>
                    Korisnik
                    <select value={userId} onChange={(e) => setUserId(e.target.value)} disabled={busy}>
                        <option value="">-- izaberi --</option>
                        {users.map((u) => {
                            const id = u?.userId ?? u?.id;
                            const label = [u?.firstName, u?.lastName].filter(Boolean).join(" ") || u?.username || u?.email || id;
                            return <option key={id} value={id}>{label}</option>;
                        })}
                    </select>
                </label>

                <label style={{ display: "grid", gap: 6 }}>
                    Kurs
                    <select value={courseId} onChange={(e) => setCourseId(e.target.value)} disabled={busy}>
                        <option value="">-- izaberi --</option>
                        {courses.map((c) => {
                            const id = c?.courseId ?? c?.id;
                            const title = c?.title || c?.name || `Course ${id}`;
                            return <option key={id} value={id}>{title}</option>;
                        })}
                    </select>
                </label>

                <label style={{ display: "grid", gap: 6 }}>
                    Važenje (dana)
                    <input type="number" min="1" value={days} onChange={(e) => setDays(e.target.value)} />
                </label>

                <div style={{ display: "flex", gap: 8 }}>
                    <button className="btn primary" disabled={busy || !userId || !courseId}>
                        {busy ? "Snimam…" : "Kreiraj enrollment"}
                    </button>
                    <a className="btn ghost" href="/notifications">Pošalji obaveštenje</a>
                </div>
                {msg && <div className="muted">{msg}</div>}
            </form>
        </div>
    );
}
