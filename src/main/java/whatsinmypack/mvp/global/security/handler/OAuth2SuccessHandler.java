package whatsinmypack.mvp.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import whatsinmypack.mvp.global.security.jwt.JwtTokenProvider;
import whatsinmypack.mvp.global.security.user.UserDetailsImpl;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authentication) throws IOException, ServletException {
        log.info("OAuth 2.0 로그인 성공");
        String username = ((UserDetailsImpl) authentication.getPrincipal()).getUsername();
        log.info("OAuth 2.0 로그인 ID: {}", username);

        // 엑세스 토큰 생성 및 응답 헤더 삽입
        String accessToken = jwtTokenProvider.createToken(username);
        response.addHeader(AUTHORIZATION_HEADER, URLEncoder.encode(accessToken, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20"));

        // sendResponseMsg 메소드 활용해서 로그인 성공 응답 보내기
    }

    private void sendResponseMsg(HttpServletResponse response, int statusCode, Object responseBody) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.print(new ObjectMapper().writeValueAsString(responseBody));
            writer.flush();
        } catch(IOException e){
            log.error(e.getMessage());
        }
    }
}
