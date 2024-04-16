package com.d2d.securityConfig;


import com.d2d.constant.Constant;
import com.d2d.entity.Users;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
@Component
public class JWTUtils implements Serializable {
    private static final long serialVersionUID = -2550185165626007488L;

    public static final long JWT_TOKEN_VALIDITY = 3L * 60 * 60;

    public static final long JWT_REFRESH_TOKEN_VALIDITY = 5L * 60 * 60;

    @Value("${jwt.secret}")
    private String secret;

    // retrieve username from jwt token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getEmail(String token) {
        return extractValueFromToken(token, secret);
    }

    private String extractValueFromToken(String token, String secret) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().get(Constant.EMAIL).toString();
    }

    // retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    // check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // validate token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
    
    public String generateToken(Authentication authentication, Users users) {
        Map<String,Object> claims=new HashMap<>();
        claims.put(Constant.EMAIL,users.getEmail());
        claims.put(Constant.MOBILE,users.getMobileNumber());
        claims.put("id",users.getId());
        return getAccessToken(claims,  authentication.getName());

    }
    public String getAccessToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+ JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS256, secret).compact();
    }

    public String refreshToken(String token, Users users) {
        final Claims claims = extractAllClaims(token);
        claims.setIssuedAt(new Date(System.currentTimeMillis()));
        claims.setExpiration(new Date(System.currentTimeMillis() + JWT_REFRESH_TOKEN_VALIDITY*1000));
        claims.put(Constant.EMAIL,users.getEmail());
        claims.put(Constant.MOBILE,users.getMobileNumber());
        claims.put("id",users.getId());
        return Jwts.builder()
                .setClaims(claims).
                 signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public UsernamePasswordAuthenticationToken getAuthentication(final String token, final Authentication existingAuth,
                                                                 final UserDetails userDetails) {

        final JwtParser jwtParser = Jwts.parser().setSigningKey(secret);

        final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);

        final Claims claims = (Claims) claimsJws.getBody();


final List<SimpleGrantedAuthority> authorities =new ArrayList<>();
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }
}
