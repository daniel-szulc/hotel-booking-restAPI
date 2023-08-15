package com.danielszulc.roomreserve.config;

import com.danielszulc.roomreserve.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
@Order(1)
public class JwtTokenUtil {
    @Value("${app.jwt.secret}")
    private String SECRET_KEY;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    private static final long EXPIRE_DURATION = 24 * 60 * 60 * 1000; // 24 hours

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(String.format("%s,%s", user.getId(), user.getEmail()))
                .setIssuer("RoomReserve")
                .claim("name", user.getUsername())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION))
                .signWith(getSecretKey())
                .compact();
    }

    public String extractUsername(String token) {
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token);
        return claims.getBody().getSubject().split(",")[1];
    }

    public String getUsernameFromToken(String token)
    {
        Claims claims = getAllClaimsFromToken(token);
        return (String) claims.get("name");
    }

    public String getRoleFromToken(String token)
    {
        Claims claims = getAllClaimsFromToken(token);
        return (String) claims.get("role");
    }

    public Date getExpirationDateFromToken(String token)
    {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver)
    {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token)
    {
        return Jwts.parser().setSigningKey(getSecretKey()).parseClaimsJws(token).getBody();
    }

    public boolean validateToken(String token, String usernameToken) {
        try {
            String username = getUsernameFromToken(token);
            return (username.equals(usernameToken) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
}
