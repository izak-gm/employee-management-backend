package com.riverbank.employee_management_backend.service;

import com.riverbank.employee_management_backend.entity.Employee;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;


@Service
public class JwtService {

  @Value("${SECRET_KEY}")
  private String SECRET_KEY;

  //  Adding extra claims to get id,email,role in jwt-token
  private <T> T extraClaim(String token, Function<Claims, T> claimResolver) {
    final Claims claims = extractAllClaims(token);
    return claimResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
          .verifyWith(getSignKey())
          .build()
          .parseSignedClaims(token)
          .getPayload();
  }

  public String extractUsername(String token) {
    return extraClaim(token, Claims::getSubject);
  }

  //  generate  a token
  public String generateToken(Employee employee) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("id", employee.getId().toString());
    claims.put("email", employee.getEmail());
    claims.put("role", employee.getRole().name());

    return buildToken(claims, employee.getEmail());
  }

  //  build a token
  private String buildToken(Map<String, Object> claims, String subject) {
    long now = System.currentTimeMillis(); // to cal time the jwt will last
    return Jwts.builder()
          .claims(claims)
          .subject(subject)
          .issuedAt(new Date(now))
          .expiration(new Date(now + TimeUnit.DAYS.toMillis(7))) // one day expiration period
          .signWith(getSignKey())
          .compact();
  }

  private SecretKey getSignKey() {
    return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
  }

  //  check validity of a token
  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extraClaim(token, Claims::getExpiration);
  }
}
