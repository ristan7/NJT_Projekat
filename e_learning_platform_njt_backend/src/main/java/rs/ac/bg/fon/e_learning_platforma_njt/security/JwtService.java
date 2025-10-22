package rs.ac.bg.fon.e_learning_platforma_njt.security;

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

/**
 * Servis za generisanje i validaciju JWT tokena (JJWT 0.11.5). - subject = username - HS256 HMAC, ključ izveden iz app.jwt.secret (>= 32 znaka) - exp čita iz app.jwt.expiration-ms (u milisekundama)
 */
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
     * Generiše JWT za korisnika; po želji dodaj extra claim-ove (npr. role, uid...).
     */
    public String generate(UserDetails user, Map<String, Object> extraClaims) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(user.getUsername())
                .addClaims(extraClaims == null ? Map.of() : extraClaims)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key()) // novi API: algoritam se određuje po ključu
                .compact();
    }

    /**
     * Vraća username (subject) iz tokena.
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Da li je token validan za zadatog korisnika (username se poklapa i nije istekao).
     */
    public boolean isValid(String token, UserDetails user) {
        try {
            final String un = extractUsername(token);
            return un.equals(user.getUsername()) && !isExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Da li je token istekao.
     */
    private boolean isExpired(String token) {
        Date exp = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return exp.before(new Date());
    }
    
}
