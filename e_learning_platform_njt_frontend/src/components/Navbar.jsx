// src/components/Navbar.jsx
import React, { useEffect, useState } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { getMe, getUnreadCount } from "../api/api";
import { onUnreadChanged } from "../api/notificationsBus";
import "../App.css";

function getStoredUser() {
  try {
    return JSON.parse(localStorage.getItem("user") || "null");
  } catch {
    return null;
  }
}

function getRoleName(u) {
  return (u?.role?.name || u?.roleName || u?.role || "")
    .toString()
    .trim()
    .toUpperCase();
}

export default function Navbar() {
  const [me, setMe] = useState(() => getStoredUser());
  const [badge, setBadge] = useState(0);
  const navigate = useNavigate();

  const token =
    typeof window !== "undefined" ? localStorage.getItem("token") : null;
  const isLoggedIn = !!token;

  // Role iz me ili iz localStorage (fallback)
  const roleName = getRoleName(me) || getRoleName(getStoredUser());
  const isAdmin = roleName === "ADMIN";
  const isTeacherOnly = roleName === "TEACHER"; // ⬅️ samo TEACHER, ne i ADMIN

  useEffect(() => {
    if (!isLoggedIn) return;

    let alive = true;

    // tih refresh /me
    (async () => {
      try {
        const fresh = await getMe();
        if (!alive) return;
        setMe(fresh);
        try {
          localStorage.setItem("user", JSON.stringify(fresh));
        } catch { }
      } catch {
        // ignoriši – ne odjavljuj ovde
      }
    })();

    // inicijalni badge
    (async () => {
      try {
        const u = getStoredUser() || me;
        const id = u?.id || u?.userId;
        if (!id) return;
        const c = await getUnreadCount(id);
        if (alive) setBadge(c);
      } catch { }
    })();

    // realtime badge preko bus-a
    const off = onUnreadChanged(async () => {
      try {
        const u = getStoredUser() || me;
        const id = u?.id || u?.userId;
        if (!id) return;
        const c = await getUnreadCount(id);
        if (alive) setBadge(c);
      } catch { }
    });

    return () => {
      alive = false;
      off?.();
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isLoggedIn]);

  function handleLogout() {
    try {
      fetch("http://localhost:8080/api/auth/logout", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      });
    } catch (e) {
      console.warn("Logout request failed", e);
    }

    localStorage.removeItem("token");
    localStorage.removeItem("user");
    navigate("/login");
  }

  const displayName =
    [me?.firstName, me?.lastName].filter(Boolean).join(" ") ||
    me?.username ||
    "User";

  const userLabel = displayName;

  const active = ({ isActive }) => ({
    textDecoration: "none",
    opacity: isActive ? 1 : 0.85,
  });

  return (
    <nav className="nav">
      <div className="container nav-inner">
        {/* BRAND */}
        <Link to="/" className="brand">
          E-Learning
        </Link>

        {/* IF NOT LOGGED IN */}
        {!isLoggedIn && (
          <div className="nav-right">
            <Link className="btn ghost sm" to="/login">
              Sign in
            </Link>
            <Link className="btn primary sm" to="/register">
              Register
            </Link>
          </div>
        )}

        {/* IF LOGGED IN */}
        {isLoggedIn && (
          <>
            <div className="nav-links">
              <NavLink to="/" style={active} end>
                Home
              </NavLink>
              <NavLink to="/courses" style={active}>
                Courses
              </NavLink>
              <NavLink to="/notifications" style={active}>
                Notifications
              </NavLink>

              {/* ADMIN-ONLY */}
              {isAdmin && (
                <NavLink to="/admin/change-role" style={active}>
                  Change role
                </NavLink>
              )}

              {/* TEACHER-ONLY (ADMIN NE VIDI OVO) */}
              {isTeacherOnly && (
                <>
                  <NavLink to="/teacher/courses" style={active}>
                    My courses
                  </NavLink>
                  <NavLink to="/teacher/courses/new" style={active}>
                    + New course
                  </NavLink>
                </>
              )}
            </div>

            <div className="nav-right">
              {/* Bell */}
              <Link to="/notifications" className="bell" title="Notifications">
                🔔
                {badge > 0 && <span className="badge-dot">{badge}</span>}
              </Link>

              {/* Username chip */}
              <span className="user-chip" title={userLabel}>
                {userLabel}
              </span>

              {/* Logout */}
              <button className="btn ghost sm" onClick={handleLogout}>
                Logout
              </button>
            </div>
          </>
        )}
      </div>
    </nav>
  );
}
