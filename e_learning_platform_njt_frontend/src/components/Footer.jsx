import React from "react";

export default function Footer() {
  return (
    <footer className="footer">
      <div className="container footer-inner">
        <div className="muted">© {new Date().getFullYear()} E-Learn demo</div>
        <div className="footer-links">
          <a href="/notifications">Notifications</a>
          <a href="/">Home</a>
        </div>
      </div>
    </footer>
  );
}
