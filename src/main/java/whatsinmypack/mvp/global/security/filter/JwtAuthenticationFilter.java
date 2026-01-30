package whatsinmypack.mvp.global.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

        try {
            // 엑세스 토큰 존재여부 검증
            if (tokenValue == null || tokenValue.isBlank()) {
                throw new AuthenticationCredentialsNotFoundException("Access Token이 존재하지 않습니다.");
            }

            // 토큰 디코딩 및 검증
//            String decodedToken = URLDecoder.decode(tokenValue, StandardCharsets.UTF_8);
//            jwtTokenProvider.validateToken(decodedToken);
            jwtTokenProvider.validateToken(tokenValue);

            /**
             * 현재로써는 검증이 끝나면 바로 다시 응답 헤더에 엑세스 토큰 반납 처리
             * 리프레시 토큰 기반 재발급 등등은 나중에 보강합시다잉
             */
            // 디코딩 토큰으로부터 사용자 식별값(이메일 추출)
            String email = jwtTokenProvider.getEmailFromToken(tokenValue);

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

        return path.equals("/api/hello") ||
                path.equals("/favicon.ico") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/api-docs") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/oauth2/");
    }

    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
