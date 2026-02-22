package whatsinmypack.mvp.global.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import whatsinmypack.mvp.global.security.handler.JwtAuthenticationEntryPoint;
import whatsinmypack.mvp.global.security.jwt.JwtTokenProvider;

@RequiredArgsConstructor
@Slf4j(topic = "jwtAuthenticationFilter")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("요청 URI : {}", request.getRequestURI());
        String tokenValue = request.getHeader(AUTHORIZATION_HEADER); // 엑세스 토큰 추출
        if ("/api/v1/items/new".equals(request.getRequestURI())) {
            log.debug(
                    "[upload-auth-check] method={}, contentType={}, hasAuthorizationHeader={}",
                    request.getMethod(),
                    request.getContentType(),
                    tokenValue != null && !tokenValue.isBlank()
            );
        }

        try {
            // 엑세스 토큰 존재여부 검증
            if (tokenValue == null || tokenValue.isBlank()) {
                throw new AuthenticationCredentialsNotFoundException("Access Token이 존재하지 않습니다.");
            }

            String email;
            String rawToken = tokenValue.startsWith("Bearer ") ? tokenValue.substring(7) : tokenValue;

            // 테스트용 토큰: Bearer user_1 / Bearer user_2 → 해당 테스트 유저 이메일로 인증 (JWT 검증 생략)
            if ("user_1".equals(rawToken)) {
                email = "user_1@test.com";
            } else if ("user_2".equals(rawToken)) {
                email = "user_2@test.com";
            } else {
                jwtTokenProvider.validateToken(tokenValue);
                email = jwtTokenProvider.getEmailFromToken(tokenValue);
            }

            // 응답 헤더에 엑세스 토큰 삽입
            response.addHeader(AUTHORIZATION_HEADER, tokenValue);

            // 인증 객체 세팅
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(createAuthentication(email));
            SecurityContextHolder.setContext(context);

            // 다음 필터 넘기기
            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            // 인증 예외 발생시 클라이언트에 예외 응답 처리
            SecurityContextHolder.clearContext();
            jwtAuthenticationEntryPoint.commence(request, response, e);
        }

    }

    // 특정 경로는 JWT 필터를 거치지 않음
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        log.info("필터를 거치지 않는 api 엔드포인트: {}", path);

        return path.equals("/api/hello") ||
                path.equals("/favicon.ico") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/api-docs") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/oauth2/") ||
                path.startsWith("/api/v1/test/");
    }

    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
