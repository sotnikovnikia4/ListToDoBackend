package com.sotnikov.ListToDoBackend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

@Component
@PropertySource("classpath:/.env")
public class JWTUtil {

    @Value("${jwt_secret}")
    private String secret;
    @Value("localhost:${server.port}")
    private String issuer;
    @Value("${jwt_days_until_expiration}")
    private int expirationDays;

    public String generateToken(String id, String phoneNumber){
        Date expirationDate = Date.from(ZonedDateTime.now().plusDays(expirationDays).toInstant());

        return JWT.create()
                .withSubject("User details")
                .withClaim("id", id)
                .withClaim("phone_number", phoneNumber)
                .withIssuer(issuer)
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret));
    }

    public String validateTokenAndRetrieveClaim(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject("User details")
                .withIssuer(issuer)
                .build();
        DecodedJWT jwt = verifier.verify(token);

        return jwt.getClaim("phone_number").asString();
    }
}
