// src/App.js
import React from "react";
import { BrowserRouter, Routes, Route, Navigate, useLocation } from "react-router-dom";

import Navbar from "./components/Navbar";
import Footer from "./components/Footer";
import ProtectedRoute from "./components/ProtectedRoute";

import Home from "./pages/Home";
import Notifications from "./pages/Notifications";
import AddNotification from "./pages/AddNotification";
import Login from "./pages/Login";
import Register from "./pages/Register";

import "./App.css";

function Layout({ children }) {
  const location = useLocation();
  const authPage = location.pathname === "/login" || location.pathname === "/register";

  return (
    <>
      {!authPage && <Navbar />}
      {children}
      {!authPage && <Footer />}
    </>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <Layout>
        <Routes>
          {/* Protected routes */}
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <Home />
              </ProtectedRoute>
            }
          />
          <Route
            path="/notifications"
            element={
              <ProtectedRoute>
                <Notifications />
              </ProtectedRoute>
            }
          />
          <Route
            path="/notifications/new"
            element={
              <ProtectedRoute>
                <AddNotification />
              </ProtectedRoute>
            }
          />

          {/* Public auth routes (bez onSuccess — Login/Register interne rade navigate) */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          {/* Fallback */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  );
}
