import React from "react";

export default function Footer() {
  const year = new Date().getFullYear();

  return (
    <footer className="footer">
      <div className="footer-container">
        {/* Brand & copy */}
        <div className="footer-section">
          <h3>E-Learn</h3>
          <div className="muted">© {year} E-Learn demo</div>
        </div>

        {/* Navigacija */}
        <div className="footer-section">
          <h3>Navigate</h3>
          <a href="/">Home</a>
          <a href="/courses">Courses</a>
          <a href="/notifications">Notifications</a>
        </div>

        {/* Pomoć / kontakt */}
        <div className="footer-section">
          <h3>Support</h3>
          <a href="#" onClick={(e) => e.preventDefault()}>Help Center</a>
          <a href="#" onClick={(e) => e.preventDefault()}>Privacy Policy</a>
          <a href="#" onClick={(e) => e.preventDefault()}>Terms of Service</a>
        </div>

        {/* Društvene mreže */}
        <div className="footer-section socials">
          <h3>Follow</h3>
          <div>
            <a href="#" aria-label="Twitter" onClick={(e) => e.preventDefault()}>𝕏</a>
            <a href="#" aria-label="Instagram" onClick={(e) => e.preventDefault()}>📸</a>
            <a href="#" aria-label="YouTube" onClick={(e) => e.preventDefault()}>▶️</a>
          </div>
        </div>
      </div>
    </footer>
  );
}
