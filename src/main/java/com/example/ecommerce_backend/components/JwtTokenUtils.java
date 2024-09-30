package com.example.ecommerce_backend.components;



import com.example.ecommerce_backend.exception.InvalidParamException;
import com.example.ecommerce_backend.models.Token;
import com.example.ecommerce_backend.models.User;
import com.example.ecommerce_backend.repositories.TokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.security.Key;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.*;
import java.util.function.Function;




@Component
@RequiredArgsConstructor
public class JwtTokenUtils {
    @Value("${jwt.expiration}")
    private int expiration; //save to an environment variable

    @Value("${jwt.expiration-refresh-token}")
    private int expirationRefreshToken;

    @Value("${jwt.secretKey}")
    private String secretKey;
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtils.class);
    private final TokenRepository tokenRepository;
    public String generateToken(User user) throws Exception{
        //properties => claims
        Map<String, Object> claims = new HashMap<>();
        // Add subject identifier (phone number or email)
//        String subject = getSubject(user);
        claims.put("email", user.getEmail());
        // Add user ID
        claims.put("userId", user.getId());

        claims.put("roles", user.getRole());
        try {
            String token = Jwts.builder()
                    .setClaims(claims) //how to extract claims from this ?
                    .setSubject(user.getEmail())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
            return token;
        }catch (Exception e) {
            //you can "inject" Logger, instead System.out.println
            throw new InvalidParamException("Cannot create jwt token, error: "+e.getMessage());
            //return null;
        }
    }

    public String generateRefreshToken(User user) throws Exception {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("userId", user.getId());
        claims.put("roles", user.getRole());
        try {
            String refreshToken = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getEmail())
                    .setExpiration(new Date(System.currentTimeMillis() + expirationRefreshToken * 1000L)) // longer expiration
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
            return refreshToken;
        } catch (Exception e) {
            throw new InvalidParamException("Cannot create refresh token, error: " + e.getMessage());
        }
    }
    private static String getSubject(User user) {
        // Determine subject identifier (phone number or email)
        String subject = user.getEmail();

        return subject;
    }
    private Key getSignInKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        //Keys.hmacShaKeyFor(Decoders.BASE64.decode("TaqlmGv1iEDMRiFp/pHuID1+T84IABfuA0xXh4GhiUI="));
        return Keys.hmacShaKeyFor(bytes);
    }

    private String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32]; // 256-bit key
        random.nextBytes(keyBytes);
        String secretKey = Encoders.BASE64.encode(keyBytes);
        return secretKey;
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    //check expiration
    public boolean isTokenExpired(String token) {
        final Date expiration = extractClaim(token, Claims::getExpiration);
        logger.info("Ngày hết hạn của token: {}", expiration);
        logger.info("Ngày hiện tại: {}", new Date());
        return expiration.before(new Date());
    }

    public String getEmail(String token) {
        return  extractClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token, User emailUser) {
        try {
            // Trích xuất email từ token
            String emailFromToken = extractClaim(token, Claims::getSubject);
            logger.info("Email trích xuất từ token: {}", emailFromToken);
            logger.info("Tên người dùng từ chi tiết người dùng: {}", emailUser.getEmail());

            // Kiểm tra xem token có hết hạn không
            if (isTokenExpired(token)) {
                logger.info("Token đã hết hạn");
                return false;
            }

            // Kiểm tra trạng thái hoạt động của người dùng
            if (!emailUser.isActive()) {
                logger.info("Người dùng không hoạt động");
                return false;
            }

            // So sánh email trích xuất từ token với email của người dùng
            return emailFromToken.equals(emailUser.getEmail());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }


    public String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                logger.info("Cookie name: {}, Cookie value: {}", cookie.getName(), cookie.getValue());
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

////
//    public void addTokenToCookie(HttpServletResponse response, String token) {
//        Cookie cookie = new Cookie("access_token", token);
////        cookie.setHttpOnly(true); // Prevent access from JavaScript
//        cookie.setHttpOnly(false); // Prevent access from JavaScript
//        cookie.setSecure(true); // Only send over HTTPS
//        cookie.setPath("/"); // Make cookie available for the whole application
//        cookie.setMaxAge(3600); // 1 hour
//        response.addCookie(cookie);
//    }
//
//    public void clearTokenCookie(HttpServletResponse response) {
//        Cookie cookie = new Cookie("access_token", null);
//        cookie.setHttpOnly(true);
//        cookie.setSecure(true);
//        cookie.setPath("/");
//        cookie.setMaxAge(0); // Remove the cookie
//        response.addCookie(cookie);
//    }
}
