// src/components/TeacherRoute.jsx
import React from "react";
import { Navigate } from "react-router-dom";

export default function TeacherRoute({ children }) {
    const token = localStorage.getItem("token");
    if (!token) return <Navigate to="/login" replace />;

    const userStr = localStorage.getItem("user");
    let role = "";
    try {
        const u = JSON.parse(userStr || "null");
        role = (u?.role?.name || u?.roleName || u?.role || "").toString().toUpperCase();
    } catch { }

    if (role !== "TEACHER") return <Navigate to="/" replace />;


    return children;
}
