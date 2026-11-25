package br.edu.utfpr.pesagem.security;

import br.edu.utfpr.pesagem.domain.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey chave;
    private final long expiracaoMs;

    public JwtService(@Value("${app.jwt.secret}") String segredo, @Value("${app.jwt.expiracao-ms}") long expiracaoMs) {
        this.chave = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
        this.expiracaoMs = expiracaoMs;
    }

    public String gerar(Usuario u) {
        return Jwts.builder().subject(u.getLogin()).claim("perfil", u.getPerfil().name())
                .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + expiracaoMs))
                .signWith(chave).compact();
    }

    public Claims ler(String token) {
        return Jwts.parser().verifyWith(chave).build().parseSignedClaims(token).getPayload();
    }
}
