package rs.ac.bg.fon.e_learning_platforma_njt.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    /**
     * Kreira HMAC ključ iz secreta.
     */
    private Key key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generiše JWT i dodaje claim-ove: userId i role (ako postoje na našem User entitetu).
     */
    public String generate(UserDetails user, Map<String, Object> extraClaims) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        Map<String, Object> claims = new java.util.HashMap<>(
                extraClaims == null ? Map.of() : extraClaims
        );

        if (user instanceof rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.User u) {
            claims.put("userId", u.getUserId());
            if (u.getRole() != null && u.getRole().getRoleName() != null) {
                claims.put("role", u.getRole().getRoleName());
            }
        }

        return Jwts.builder()
                .setSubject(user.getUsername())
                .addClaims(claims)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key())
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isValid(String token, UserDetails user) {
        try {
            final String un = extractUsername(token);
            return un.equals(user.getUsername()) && !isExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    private boolean isExpired(String token) {
        Date exp = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return exp.before(new Date());
    }

    /**
     * Izvlači userId iz claim-a "userId" ako postoji.
     */
    public Long extractUserId(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Object id = claims.get("userId");
            if (id == null) {
                return null;
            }

            if (id instanceof Integer i) {
                return i.longValue();
            }
            if (id instanceof Long l) {
                return l;
            }
            return Long.parseLong(id.toString());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Uzimanje JWT iz Authorization: Bearer ... headera.
     */
    public String extractTokenFromHeader() {
        var attrs = org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        if (attrs instanceof org.springframework.web.context.request.ServletRequestAttributes sra) {
            String header = sra.getRequest().getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                return header.substring(7);
            }
        }
        return null;
    }

    /**
     * Vraća ime role iz claim-ova "role" ili "roles".
     */
    public String extractRoleName(String token) {
        try {
            Claims claims = extractAllClaims(token);

            Object role = claims.get("role");
            if (role != null) {
                return normalizeRole(role.toString());
            }

            Object roles = claims.get("roles");
            if (roles instanceof java.util.Collection<?> col && !col.isEmpty()) {
                return normalizeRole(String.valueOf(col.iterator().next()));
            }
            if (roles != null) {
                return normalizeRole(roles.toString());
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Uklanja prefiks ROLE_ i vraća UPPERCASE naziv role.
     */
    private String normalizeRole(String raw) {
        if (raw == null) {
            return null;
        }
        String r = raw.trim().toUpperCase();
        if (r.startsWith("ROLE_")) {
            r = r.substring(5);
        }
        return r;
    }

    /**
     * Centralno parsiranje svih claim-ova sa istim ključem koji već koristiš.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key()) // <-- koristi postojeću key() metodu
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long requireUserId(String token) {
        if (token == null || token.isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Missing token");
        }
        Long uid = extractUserId(token);
        if (uid == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid token (userId)");
        }
        return uid;
    }

}
