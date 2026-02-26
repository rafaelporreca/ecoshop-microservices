package com.ecoshop.gateway.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

    // Lê a mesma chave secreta que vamos partilhar no repositório de configurações
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    public void validateToken(final String token) {
        // Se o token for inválido, expirado ou adulterado, isto lançará uma exceção automaticamente
        Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
