import React, { useEffect, useState } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import {
  getCourse,
  createCourse,
  updateCourse,
  getCourseLevels,
  getCourseStatuses,
} from "../api/courses";
import "../css/Courses.css";

/* ------------------ TEST PREKIDAČИ ------------------ */
// ALT — simuliraj neuspeh čuvanja (bilo create bilo update)
const TEST_FAIL_SAVE_COURSE = false;
/* ---------------------------------------------------- */

export default function CourseEditor() {
  const { courseId } = useParams();
  const navigate = useNavigate();
  const isCreate = !courseId || courseId === "new";

  const [title, setTitle] = useState("");
  const [desc, setDesc] = useState("");
  const [levelId, setLevelId] = useState("");
  const [statusId, setStatusId] = useState("");
  const [levels, setLevels] = useState([]);
  const [statuses, setStatuses] = useState([]);
  const [saving, setSaving] = useState(false);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState("");

  useEffect(() => {
    let alive = true;
    (async () => {
      try {
        setLoading(true);
        setErr("");

        const [lv, st] = await Promise.all([getCourseLevels(), getCourseStatuses()]);
        if (!alive) return;
        setLevels(Array.isArray(lv) ? lv : []);
        setStatuses(Array.isArray(st) ? st : []);

        if (!isCreate) {
          const c = await getCourse(courseId);
          if (!alive) return;
          if (c) {
            setTitle(c.courseTitle ?? c.title ?? "");
            setDesc(c.courseDescription ?? c.description ?? "");
            setLevelId(c.courseLevelId ?? c.levelId ?? c?.courseLevel?.courseLevelId ?? "");
            setStatusId(c.courseStatusId ?? c.statusId ?? c?.courseStatus?.courseStatusId ?? "");
          }
        }
      } catch (e) {
        if (!alive) return;
        setErr(e?.response?.data?.message || e?.message || "Failed to load course.");
      } finally {
        if (alive) setLoading(false);
      }
    })();
    return () => { alive = false; };
  }, [courseId, isCreate]);

  async function handleSave(e, simulateError = false) {
    e.preventDefault();
    setErr("");
    try {
      setSaving(true);

      if (simulateError || TEST_FAIL_SAVE_COURSE) {
        throw new Error("Simulated save error");
      }

      const payload = {
        title: title.trim(),
        description: desc.trim(),
        levelId: levelId ? Number(levelId) : null,
        statusId: statusId ? Number(statusId) : null,
      };

      if (isCreate) {
        await createCourse(payload);
        window.alert("✅ Course successfully added !");
      } else {
        await updateCourse(Number(courseId), payload);
        window.alert("✅ Course successfully updated !");
      }

      navigate("/teacher/courses", { replace: true });
    } catch (e2) {
      setErr(e2?.response?.data?.message || e2?.message || "Save failed.");

      // ❌ Odvojene poruke za create i update
      if (isCreate) {
        window.alert("❌ The system cannot save the course!");
      } else {
        window.alert("❌ The system cannot update the course!");
      }
    } finally {
      setSaving(false);
    }
  }

  if (loading)
    return (
      <div className="container narrow">
        <p>Loading…</p>
      </div>
    );

  return (
    <div className="container narrow">
      <h2>{isCreate ? "Create new course" : "Edit course"}</h2>
      <p className="muted">
        {isCreate
          ? "Fill in the details to create a new course."
          : "Update main course info."}
      </p>

      {err && <div className="alert error">{err}</div>}

      <form onSubmit={(e) => handleSave(e)} className="card form-grid">
        <label className="col-2">
          <span>Title</span>
          <input
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="Enter course title"
            required
          />
        </label>

        <label className="col-2">
          <span>Description</span>
          <textarea
            rows={5}
            value={desc}
            onChange={(e) => setDesc(e.target.value)}
            placeholder="Describe your course..."
          />
        </label>

        <label>
          <span>Level</span>
          <select value={levelId} onChange={(e) => setLevelId(e.target.value)}>
            <option value="">—</option>
            {levels.map((x, i) => {
              const id = x?.courseLevelId ?? x?.id ?? i;
              const name = x?.courseLevelName ?? x?.name ?? `Level ${id}`;
              return (
                <option key={id} value={id}>
                  {name}
                </option>
              );
            })}
          </select>
        </label>

        <label>
          <span>Status</span>
          <select value={statusId} onChange={(e) => setStatusId(e.target.value)}>
            <option value="">—</option>
            {statuses.map((x, i) => {
              const id = x?.courseStatusId ?? x?.id ?? i;
              const name = x?.courseStatusName ?? x?.name ?? `Status ${id}`;
              return (
                <option key={id} value={id}>
                  {name}
                </option>
              );
            })}
          </select>
        </label>

        <div />
        <div className="row">
          {/* ALT-klik za ad-hoc test neuspeha */}
          <button
            className="btn primary"
            disabled={saving || !title.trim()}
            onClick={(e) => {
              e.preventDefault();
              handleSave(e, e.altKey === true);
            }}
          >
            {saving ? "Saving…" : isCreate ? "Create course" : "Save changes"}
          </button>
          <Link className="btn ghost" to="/teacher/courses">
            Cancel
          </Link>
        </div>
      </form>
    </div>
  );
}
