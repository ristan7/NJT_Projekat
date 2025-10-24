import React, { useEffect, useState } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import { getCourse, updateCourse, getCourseLevels, getCourseStatuses } from "../api/courses";

export default function CourseEditor() {
  const { courseId } = useParams();
  const navigate = useNavigate();

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
    (async () => {
      try {
        setLoading(true);
        const [c, lv, st] = await Promise.all([
          getCourse(courseId),
          getCourseLevels(),
          getCourseStatuses(),
        ]);
        setLevels(Array.isArray(lv) ? lv : []);
        setStatuses(Array.isArray(st) ? st : []);

        if (c) {
          setTitle(c.courseTitle ?? c.title ?? "");
          setDesc(c.courseDescription ?? c.description ?? "");
          setLevelId(c.courseLevelId ?? c.levelId ?? c?.courseLevel?.courseLevelId ?? "");
          setStatusId(c.courseStatusId ?? c.statusId ?? c?.courseStatus?.courseStatusId ?? "");
        }
      } catch (e) {
        setErr(e?.response?.data?.message || e?.message || "Failed to load course.");
      } finally {
        setLoading(false);
      }
    })();
  }, [courseId]);

  const onSave = async (e) => {
    e.preventDefault();
    try {
      setSaving(true);
      await updateCourse(courseId, {
        title,
        description: desc,
        levelId: levelId ? Number(levelId) : null,
        statusId: statusId ? Number(statusId) : null,
      });
      navigate("/teacher/courses", { replace: true });
    } catch (e2) {
      setErr(e2?.response?.data?.message || e2?.message || "Save failed.");
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div className="container"><p>Loading…</p></div>;

  return (
    <div className="container">
      <h2>Edit course</h2>
      <p className="muted">Update main course info.</p>
      {err && <div className="alert error">{err}</div>}
      <form onSubmit={onSave} className="card form-grid">
        <label className="col-2">
          <span>Title</span>
          <input value={title} onChange={(e) => setTitle(e.target.value)} />
        </label>
        <label className="col-2">
          <span>Description</span>
          <textarea rows={6} value={desc} onChange={(e) => setDesc(e.target.value)} />
        </label>
        <label>
          <span>Level</span>
          <select value={levelId} onChange={(e) => setLevelId(e.target.value)}>
            <option value="">—</option>
            {(levels || []).map((x, i) => {
              const id = x?.courseLevelId ?? x?.id ?? i;
              const name = x?.courseLevelName ?? x?.name ?? `Level ${id}`;
              return <option key={id} value={id}>{name}</option>;
            })}
          </select>
        </label>
        <label>
          <span>Status</span>
          <select value={statusId} onChange={(e) => setStatusId(e.target.value)}>
            <option value="">—</option>
            {(statuses || []).map((x, i) => {
              const id = x?.courseStatusId ?? x?.id ?? i;
              const name = x?.courseStatusName ?? x?.name ?? `Status ${id}`;
              return <option key={id} value={id}>{name}</option>;
            })}
          </select>
        </label>

        <div />
        <div className="row">
          <button className="btn primary" disabled={saving || !title.trim()}>
            {saving ? "Saving…" : "Save changes"}
          </button>
          <Link className="btn ghost" to="/teacher/courses">Cancel</Link>
          {/* ⛔️ uklonjeno “Manage lessons/materials” dugme */}
        </div>
      </form>
    </div>
  );
}
