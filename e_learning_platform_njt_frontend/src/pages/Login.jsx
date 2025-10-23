import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { login, saveAuth } from "../api/api";
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
            const data = await login(form.username.trim(), form.password);
            if (!data?.token) throw new Error("Token missing in response");
            saveAuth(data);
            setOk("Welcome back!");
            navigate("/", { replace: true });
        } catch (e2) {
            const msg =
                e2?.response?.data?.message ||
                e2?.response?.data?.error ||
                e2?.message ||
                "Login failed. Please try again.";
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
                            value={form.username}
                            onChange={(e) => setForm((f) => ({ ...f, username: e.target.value }))}
                            placeholder="marko123"
                            autoComplete="username"
                            required
                        />
                    </div>

                    <div className="field">
                        <label>Password</label>
                        <input
                            type="password"
                            value={form.password}
                            onChange={(e) => setForm((f) => ({ ...f, password: e.target.value }))}
                            placeholder="••••••••"
                            autoComplete="current-password"
                            minLength={6}
                            required
                        />
                    </div>

                    <button className="btn-primary" disabled={loading}>
                        {loading ? "Signing in…" : "Sign in"}
                    </button>
                </form>

                <div className="auth-footer">
                    <span className="muted">Don’t have an account? </span>
                    <Link to="/register">Create one</Link>
                </div>
            </div>
        </div>
    );
}
