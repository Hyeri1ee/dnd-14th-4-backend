package whatsinmypack.mvp.global.security.jwt;

import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import whatsinmypack.mvp.domain.user.entity.User;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final long ACCESS_TOKEN_VALIDITY = 365L * 24 * 60 * 60 * 1000; // 1년 mvp용...

    private final SecretKey secretKey;

    public JwtTokenProvider(@Value("${jwt.secret.key}") String secret) {
        this.secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // 토큰 생성
    public String createToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_VALIDITY);

        return BEARER_PREFIX + Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    // 토큰에서 이메일 추출
    public String getEmailFromToken(String token) {
        token = removeBearerPrefix(token);

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            token = removeBearerPrefix(token);
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    private String removeBearerPrefix(String token) {
        if (token != null && token.startsWith(BEARER_PREFIX)) {
            return token.substring(BEARER_PREFIX.length());
        }
        return token;
    }
}
