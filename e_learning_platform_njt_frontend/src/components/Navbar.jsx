// src/components/Navbar.jsx
import http from "../api/http";
import React, { useEffect, useState } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { getMe, getUnreadCount } from "../api/api";
import { onUnreadChanged } from "../api/notificationsBus";
import "../App.css";

/* ------------------ TEST PREKIDAČI ------------------ */
// Blokiraj otvaranje "+ New course" (klik u meniju) – seti na true ili drži ALT pri kliku
const TEST_BLOCK_NEW_COURSE = false;
const TEST_BLOCK_MY_COURSES = false;
const TEST_BLOCK_CHANGE_ROLE = true; // seti na true ili drži ALT pri kliku
/* ---------------------------------------------------- */

function getStoredUser() {
  try { return JSON.parse(localStorage.getItem("user") || "null"); } catch { return null; }
}
function getRoleName(u) {
  return (u?.role?.name || u?.roleName || u?.role || "").toString().trim().toUpperCase();
}

export default function Navbar() {
  const [me, setMe] = useState(() => getStoredUser());
  const [badge, setBadge] = useState(0);
  const navigate = useNavigate();

  const token = typeof window !== "undefined" ? localStorage.getItem("token") : null;
  const isLoggedIn = !!token;

  const roleName = getRoleName(me) || getRoleName(getStoredUser());
  const isAdmin = roleName === "ADMIN";
  const isTeacherOnly = roleName === "TEACHER"; // samo TEACHER

  useEffect(() => {
    if (!isLoggedIn) return;
    let alive = true;

    (async () => {
      try {
        const fresh = await getMe();
        if (!alive) return;
        setMe(fresh);
        try { localStorage.setItem("user", JSON.stringify(fresh)); } catch { }
      } catch { }
    })();

    (async () => {
      try {
        const u = getStoredUser() || me;
        const id = u?.id || u?.userId;
        if (!id) return;
        const c = await getUnreadCount(id);
        if (alive) setBadge(c);
      } catch { }
    })();

    const off = onUnreadChanged(async () => {
      try {
        const u = getStoredUser() || me;
        const id = u?.id || u?.userId;
        if (!id) return;
        const c = await getUnreadCount(id);
        if (alive) setBadge(c);
      } catch { }
    });

    return () => { alive = false; off?.(); };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isLoggedIn]);

  async function handleLogout() {
    try { await http.post("/auth/logout").catch(() => { }); } catch { }
    const u = getStoredUser();
    const displayName = [u?.firstName, u?.lastName].filter(Boolean).join(" ") || u?.username || "User";
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    try { if (http?.defaults?.headers?.common) delete http.defaults.headers.common.Authorization; } catch { }
    window.alert(`✅ Successful logout!\nGoodbye, ${displayName}`);
    navigate("/login", { replace: true });
  }

  function onMyCoursesClick(e) {
    // test-prekidač ili ALT klik
    if (TEST_BLOCK_MY_COURSES || e?.altKey === true) {
      e.preventDefault();
      window.alert("⚠️ The system cannot open my courses page.");
    }
  }

  function onChangeRoleClick(e) {
    // ALT-klik ili prekidač simuliraju alternativni scenario
    if (TEST_BLOCK_CHANGE_ROLE || e?.altKey === true) {
      e.preventDefault();
      window.alert("⚠️ The system cannot open the change role page.");
    }
  }


  const displayName = [me?.firstName, me?.lastName].filter(Boolean).join(" ") || me?.username || "User";
  const userLabel = displayName;
  const active = ({ isActive }) => ({ textDecoration: "none", opacity: isActive ? 1 : 0.85 });

  // Guard za "+ New course"
  function onNewCourseClick(e) {
    if (TEST_BLOCK_NEW_COURSE || e?.altKey === true) {
      e.preventDefault();
      window.alert("⚠️ The system cannot open the create course form.");
    }
  }

  return (
    <nav className="nav">
      <div className="container nav-inner">
        <Link to="/" className="brand">E-Learning</Link>

        {!isLoggedIn && (
          <div className="nav-right">
            <Link className="btn ghost sm" to="/login">Sign in</Link>
            <Link className="btn primary sm" to="/register">Register</Link>
          </div>
        )}

        {isLoggedIn && (
          <>
            <div className="nav-links">
              <NavLink to="/" style={active} end>Home</NavLink>
              <NavLink to="/courses" style={active}>Courses</NavLink>
              <NavLink to="/notifications" style={active}>Notifications</NavLink>

              {isAdmin && (
                <NavLink to="/admin/change-role" style={active} onClick={onChangeRoleClick}>
                  Change role
                </NavLink>
              )}


              {isTeacherOnly && (
                <>
                  <NavLink to="/teacher/courses" style={active} onClick={onMyCoursesClick}>
                    My courses
                  </NavLink>
                  {/* ALT-klik ili TEST_BLOCK_NEW_COURSE blokira otvaranje */}
                  <NavLink to="/teacher/courses/new" style={active} onClick={onNewCourseClick}>
                    + New course
                  </NavLink>
                </>
              )}
            </div>

            <div className="nav-right">
              <Link to="/notifications" className="bell" title="Notifications">
                🔔 {badge > 0 && <span className="badge-dot">{badge}</span>}
              </Link>
              <span className="user-chip" title={userLabel}>{userLabel}</span>
              <button className="btn ghost sm" onClick={handleLogout}>Logout</button>
            </div>
          </>
        )}
      </div>
    </nav>
  );
}
