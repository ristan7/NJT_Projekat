import React, { useEffect, useMemo, useState } from "react";
import { getMe, getUsers, updateUserRole } from "../api/api";


const ROLES = [
    { id: 1, name: "STUDENT" },
    { id: 2, name: "TEACHER" },
    { id: 3, name: "ADMIN" },
];

export default function AdminChangeRole() {
    const [me, setMe] = useState(null);
    const [users, setUsers] = useState([]);
    const [targetId, setTargetId] = useState("");
    const [roleId, setRoleId] = useState("");
    const [busy, setBusy] = useState(false);
    const [msg, setMsg] = useState("");

    const isAdmin = useMemo(() => me?.roleName === "ADMIN" || me?.roleId === 3, [me]);

    useEffect(() => {
        (async () => {
            try {
                const [meRes, usersRes] = await Promise.all([getMe(), getUsers()]);
                setMe(meRes);
                const myId = meRes?.userId ?? meRes?.id;
                setUsers((usersRes || []).filter(u => (u.userId ?? u.id) !== myId));
            } catch {
                setMsg("Greška pri učitavanju podataka.");
            }
        })();
    }, []);

    async function submit(e) {
        e.preventDefault();
        if (!targetId || !roleId) return;
        try {
            setBusy(true);
            await updateUserRole(Number(targetId), Number(roleId));
            setMsg("Uspešno promenjena rola.");
        } catch {
            setMsg("Promena role nije uspela.");
        } finally {
            setBusy(false);
        }
    }

    if (!me) return <div className="users-loading" style={{ padding: 24 }}>Učitavam...</div>;
    if (!isAdmin) return <div className="users-error" style={{ padding: 24 }}>Samo admin ima pristup.</div>;

    return (
        <div className="users-page" style={{ padding: 24 }}>
            <h2>Promena role korisniku</h2>

            <form onSubmit={submit} className="role-form" style={{ display: "grid", gap: 12, maxWidth: 420 }}>
                <label style={{ display: "grid", gap: 6 }}>
                    Korisnik
                    <select value={targetId} onChange={e => setTargetId(e.target.value)} disabled={busy}>
                        <option value="">-- izaberi korisnika --</option>
                        {users.map(u => {
                            const id = u.userId ?? u.id;
                            const label = u.username
                                ? `${u.username}${u.email ? ` (${u.email})` : ""}`
                                : (u.email || id);
                            return <option key={id} value={id}>{label}</option>;
                        })}
                    </select>
                </label>

                <label style={{ display: "grid", gap: 6 }}>
                    Nova rola
                    <select value={roleId} onChange={e => setRoleId(e.target.value)} disabled={busy}>
                        <option value="">-- izaberi rolu --</option>
                        {ROLES.map(r => <option key={r.id} value={r.id}>{r.name}</option>)}
                    </select>
                </label>

                <button className="btn primary" disabled={busy || !targetId || !roleId}>
                    {busy ? "Snima..." : "Promeni rolu"}
                </button>
            </form>

            {msg && <p className="muted" style={{ marginTop: 12 }}>{msg}</p>}
        </div>
    );
}
