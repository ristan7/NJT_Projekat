// src/pages/Register.jsx
import React, { useState } from "react";
import http from "../api/http";
import "../css/auth.css";

export default function Register({ onSuccess }) {
    const [form, setForm] = useState({
        username: "",
        email: "",
        password: "",
        firstName: "",   // NEW
        lastName: "",    // NEW
    });
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState("");
    const [ok, setOk] = useState("");

    async function handleSubmit(e) {
        e.preventDefault();
        setErr(""); setOk(""); setLoading(true);

        try {
            // 1) Register
            await http.post("/auth/register", form);

            // 2) Auto-login
            const loginRes = await http.post("/auth/login", {
                username: form.username,
                password: form.password,
            });

            const { token, user } = loginRes.data || {};
            localStorage.setItem("token", token);
            if (user) localStorage.setItem("user", JSON.stringify(user));

            setOk("Account created successfully!");
            if (typeof onSuccess === "function") onSuccess(user);
        } catch (e2) {
            const msg = e2?.response?.data?.message || e2?.message || "Registration failed. Please try again.";
            setErr(msg);
        } finally { setLoading(false); }
    }

    return (
        <div className="auth-wrap">
            <div className="auth-card">
                <h2>Create account</h2>
                <p className="muted">Join and start learning</p>

                {err && <div className="auth-alert">{err}</div>}
                {ok && <div className="auth-success">{ok}</div>}

                <form onSubmit={handleSubmit} className="auth-form" autoComplete="on">
                    <div className="field">
                        <label>First name</label>
                        <input
                            type="text"
                            value={form.firstName}
                            onChange={(e) => setForm((f) => ({ ...f, firstName: e.target.value }))}
                        />
                    </div>

                    <div className="field">
                        <label>Last name</label>
                        <input
                            type="text"
                            value={form.lastName}
                            onChange={(e) => setForm((f) => ({ ...f, lastName: e.target.value }))}
                        />
                    </div>

                    <div className="field">
                        <label>Username</label>
                        <input
                            type="text"
                            value={form.username}
                            onChange={(e) => setForm((f) => ({ ...f, username: e.target.value }))}
                            required
                            autoComplete="username"
                        />
                    </div>

                    <div className="field">
                        <label>Email</label>
                        <input
                            type="email"
                            value={form.email}
                            onChange={(e) => setForm((f) => ({ ...f, email: e.target.value }))}
                            required
                            autoComplete="email"
                        />
                    </div>

                    <div className="field">
                        <label>Password</label>
                        <input
                            type="password"
                            value={form.password}
                            onChange={(e) => setForm((f) => ({ ...f, password: e.target.value }))}
                            required
                            minLength={8}
                            autoComplete="new-password"
                        />
                    </div>

                    <button className="btn-primary" disabled={loading}>
                        {loading ? "Creating..." : "Create account"}
                    </button>
                </form>

                <div className="auth-footer">
                    <span className="muted">Already have an account? </span>
                    <a href="/login">Sign in</a>
                </div>
            </div>
        </div>
    );
}
