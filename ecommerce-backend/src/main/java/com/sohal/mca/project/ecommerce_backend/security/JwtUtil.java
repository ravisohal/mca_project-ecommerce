package com.sohal.mca.project.ecommerce_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-30
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: JwtUtil class for handling JWT operations in the e-commerce application.
 * Provides methods to generate, validate, and extract information from JWT tokens.
 * This class uses io.jsonwebtoken library for JWT handling.
 * It is used in conjunction with Spring Security to manage authentication and authorization.
 */

@Component
public class JwtUtil {

    // Secret key for signing JWTs. Loaded from application.properties.
    // IMPORTANT: In a production environment, this should be a strong, randomly generated key
    // and ideally managed securely (e.g., environment variable, secret management service).
    @Value("${jwt.secret}")
    private String secret;

    // Token expiration time in milliseconds (e.g., 24 hours)
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Extracts the username from the JWT token.
     * @param token The JWT token.
     * @return The username (subject) from the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the JWT token.
     * @param token The JWT token.
     * @return The expiration date from the token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from the JWT token using a claims resolver function.
     * @param token The JWT token.
     * @param claimsResolver Function to resolve a specific claim from Claims.
     * @param <T> Type of the claim.
     * @return The resolved claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the JWT token.
     * @param token The JWT token.
     * @return All claims from the token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Checks if the JWT token is expired.
     * @param token The JWT token.
     * @return True if the token is expired, false otherwise.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Generates a JWT token for the given UserDetails.
     * @param userDetails The UserDetails object.
     * @return The generated JWT token.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Creates the JWT token with specified claims and subject.
     * @param claims Additional claims to include in the token.
     * @param subject The subject of the token (usually username).
     * @return The built JWT token string.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        io.jsonwebtoken.JwtBuilder builder = Jwts.builder()
                .claim("sub", subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration)); // Token valid for 'expiration' milliseconds

        // Add claims individually to avoid deprecated setClaims
        if (claims != null) {
            for (Map.Entry<String, Object> entry : claims.entrySet()) {
                builder.claim(entry.getKey(), entry.getValue());
            }
        }

        return builder
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Use HS256 algorithm with the secret key
                .compact();
    }

    /**
     * Validates the JWT token against UserDetails.
     * Checks if the username matches and the token is not expired.
     * @param token The JWT token.
     * @param userDetails The UserDetails object.
     * @return True if the token is valid, false otherwise.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Returns the signing key from the secret string.
     * Decodes the base64 secret and creates a Key object.
     * @return The signing Key.
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

