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

// Courses pages
import Courses from "./pages/Courses";
import CourseDetails from "./pages/CourseDetails";
import LessonView from "./pages/LessonView";

import AdminRoute from "./components/AdminRoute";
import AdminChangeRole from "./pages/AdminChangeRole";

import TeacherRoute from "./components/TeacherRoute";
import TeacherCourses from "./pages/TeacherCourses";
import CourseEditor from "./pages/CourseEditor";
import CourseManage from "./pages/CourseManage";
import LessonEditor from "./pages/LessonEditor";

import AdminEnrollments from "./pages/AdminEnrollments";


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

          {/* Courses catalog */}
          <Route
            path="/courses"
            element={
              <ProtectedRoute>
                <Courses />
              </ProtectedRoute>
            }
          />

          {/* Course details */}
          <Route
            path="/courses/:id"
            element={
              <ProtectedRoute>
                <CourseDetails />
              </ProtectedRoute>
            }
          />

          {/* Lesson view */}
          <Route
            path="/lessons/:id"
            element={
              <ProtectedRoute>
                <LessonView />
              </ProtectedRoute>
            }
          />

          {/* Notifications */}
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
          <Route
            path="/admin/change-role"
            element={
              <ProtectedRoute>
                <AdminRoute>
                  <AdminChangeRole />
                </AdminRoute>
              </ProtectedRoute>
            }
          />
          {/* Teacher dashboard: moji kursevi */}
          <Route
            path="/teacher/courses"
            element={
              <ProtectedRoute>
                <TeacherRoute>
                  <TeacherCourses />
                </TeacherRoute>
              </ProtectedRoute>
            }
          />

          {/* Kreiranje novog kursa */}
          <Route
            path="/teacher/courses/new"
            element={
              <ProtectedRoute>
                <TeacherRoute>
                  <CourseEditor mode="create" />
                </TeacherRoute>
              </ProtectedRoute>
            }
          />

          {/* Izmena kursa */}
          <Route
            path="/teacher/courses/:courseId/edit"
            element={
              <ProtectedRoute>
                <TeacherRoute>
                  <CourseEditor mode="edit" />
                </TeacherRoute>
              </ProtectedRoute>
            }
          />

          {/* Upravljanje lekcijama i materijalima za kurs */}
          <Route
            path="/teacher/courses/:courseId/manage"
            element={
              <ProtectedRoute>
                <TeacherRoute>
                  <CourseManage />
                </TeacherRoute>
              </ProtectedRoute>
            }
          />

          {/* Uređivanje jedne lekcije + materijala */}
          <Route
            path="/teacher/courses/:courseId/lessons/:lessonId"
            element={
              <ProtectedRoute>
                <TeacherRoute>
                  <LessonEditor />
                </TeacherRoute>
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/enrollments"
            element={
              <ProtectedRoute>
                <AdminRoute>
                  <AdminEnrollments />
                </AdminRoute>
              </ProtectedRoute>
            }
          />


          {/* Public auth routes */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          {/* Fallback */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  );
}
