// src/pages/Login.jsx
import React, { useState } from "react";
import http from "../api/http";
import { useNavigate } from "react-router-dom";
import "../css/auth.css";

export default function Login() {
    const navigate = useNavigate();
    const [form, setForm] = useState({ username: "", password: "" });
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState("");
    const [ok, setOk] = useState("");

    async function handleSubmit(e) {
        e.preventDefault();
        setErr("");
        setOk("");
        setLoading(true);

        try {
            const res = await http.post("/auth/login", {
                username: form.username.trim(),
                password: form.password,
            });

            const { token, user } = res.data || {};
            if (!token) throw new Error("Token missing in response");

            localStorage.setItem("token", token);
            if (user) localStorage.setItem("user", JSON.stringify(user));

            setOk("Welcome back!");
            // ⬇️ ključno: replace da ne ostane /login u istoriji
            navigate("/", { replace: true });
        } catch (e2) {
            const msg = e2?.response?.data?.message || e2?.message || "Login failed. Please try again.";
            setErr(msg);
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="auth-wrap">
            <div className="auth-card">
                <h2>Welcome back</h2>
                <p className="muted">Sign in to continue</p>

                {err && <div className="auth-alert">{err}</div>}
                {ok && <div className="auth-success">{ok}</div>}

                <form onSubmit={handleSubmit} className="auth-form" autoComplete="on">
                    <div className="field">
                        <label>Username</label>
                        <input
                            type="text"
                            autoComplete="username"
                            value={form.username}
                            onChange={(e) => setForm((f) => ({ ...f, username: e.target.value }))}
                            required
                        />
                    </div>

                    <div className="field">
                        <label>Password</label>
                        <input
                            type="password"
                            autoComplete="current-password"
                            value={form.password}
                            onChange={(e) => setForm((f) => ({ ...f, password: e.target.value }))}
                            required
                            minLength={6}
                        />
                    </div>

                    <button className="btn-primary" disabled={loading}>
                        {loading ? "Signing in…" : "Sign in"}
                    </button>
                </form>

                <div className="auth-footer">
                    <span className="muted">Don’t have an account? </span>
                    <a href="/register">Create one</a>
                </div>
            </div>
        </div>
    );
}
