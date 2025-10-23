import React, { useEffect, useState } from "react";
import { getMe } from "../api/api";

export default function AdminRoute({ children }) {
  const [loading, setLoading] = useState(true);
  const [allowed, setAllowed] = useState(false);

  useEffect(() => {
    let alive = true;
    (async () => {
      try {
        const me = await getMe();
        const isAdmin = me?.roleName === "ADMIN" || me?.roleId === 3; // 1=STUDENT, 2=TEACHER, 3=ADMIN
        if (alive) {
          setAllowed(!!isAdmin);
          setLoading(false);
        }
      } catch {
        if (alive) {
          setAllowed(false);
          setLoading(false);
        }
      }
    })();
    return () => { alive = false; };
  }, []);

  if (loading) return <div className="users-loading" style={{padding:24}}>Učitavam...</div>;
  if (!allowed) return <div className="users-error" style={{padding:24}}>Samo admin ima pristup.</div>;
  return children;
}
