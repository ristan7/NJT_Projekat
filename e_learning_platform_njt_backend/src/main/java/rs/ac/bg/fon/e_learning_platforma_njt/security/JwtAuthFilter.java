// JwtAuthFilter.java
package rs.ac.bg.fon.e_learning_platforma_njt.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Čita Authorization: Bearer <token>, ako je validan postavi Authentication. Ako nema/loš token ili je javna ruta → NE prekida lanac (pusti dalje).
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwt;
    private final UserDetailsService uds;

    public JwtAuthFilter(JwtService jwt, UserDetailsService uds) {
        this.jwt = jwt;
        this.uds = uds;
    }

    private boolean isPublicPath(String p, String method) {
        if (p == null) {
            return false;
        }
        // preflight uvek pusti
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        // auth login/register uvek javno
        if (p.equals("/api/auth/login") || p.equals("/api/auth/register")) {
            return true;
        }

        // READ-ONLY lookups
        if ("GET".equalsIgnoreCase(method)) {
            return p.equals("/api/notification-types")
                    || p.equals("/api/course-levels")
                    || p.equals("/api/course-statuses")
                    || p.equals("/api/lesson-types")
                    || p.equals("/api/material-types");
        }
        return false;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        String p = req.getServletPath();
        String m = req.getMethod();
        if (p == null) {
            return false;
        }

        if ("OPTIONS".equalsIgnoreCase(m)) {
            return true;
        }
        if (p.equals("/api/auth/login") || p.equals("/api/auth/register")) {
            return true;
        }

        if ("GET".equalsIgnoreCase(m)) {
            return p.equals("/api/notification-types") || p.startsWith("/api/notification-types/")
                    || p.equals("/api/course-levels") || p.startsWith("/api/course-levels/")
                    || p.equals("/api/course-statuses") || p.startsWith("/api/course-statuses/")
                    || p.equals("/api/lesson-types") || p.startsWith("/api/lesson-types/")
                    || p.equals("/api/material-types") || p.startsWith("/api/material-types/")
                    || p.equals("/api/enrollment-statuses") || p.startsWith("/api/enrollment-statuses/"); // ⬅️ DODATO
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        // Ako smo ovde, filter se primenjuje (nije javna ruta).
        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res); // bez tokena – pusti dalje; SecurityConfig odlučuje
            return;
        }

        String token = header.substring(7);
        try {
            String username = jwt.extractUsername(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails ud = uds.loadUserByUsername(username);
                if (jwt.isValid(token, ud)) {
                    UsernamePasswordAuthenticationToken auth
                            = new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (Exception ignore) {
            // NIKAKO ne prekidaj – pusti lanac dalje
        }

        chain.doFilter(req, res);
    }
}
