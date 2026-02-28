package whatsinmypack.mvp.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import whatsinmypack.mvp.presentation.response.ApiResponse;
import whatsinmypack.mvp.global.security.jwt.JwtTokenProvider;
import whatsinmypack.mvp.global.security.user.UserDetailsImpl;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Value("${client.url}")
    private String clientUrl;

    @Value("${client.deployUrl:${client.url}}")
    private String clientDeployUrl;

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        log.info("OAuth 2.0 로그인 성공");
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();
        log.info("OAuth 2.0 로그인 ID: {}", username);

        // 엑세스 토큰 생성 및 응답 헤더 삽입
        String accessToken = jwtTokenProvider.createToken(userDetails.getUser());
        response.addHeader(AUTHORIZATION_HEADER, accessToken);

        // 엑세스 토큰 URL 삽입 및 리다이렉션

        String origin = request.getHeader("Origin");
        boolean isLocalClient = clientUrl.equals(origin);

        log.info("로컬 여부 판단 및 오리진 판단: {}", origin);
        String redirectUrl = isLocalClient ? clientUrl : clientDeployUrl;
        log.info("리다이렉팅 Url 결정: {}", redirectUrl);

        response.setStatus(HttpServletResponse.SC_OK);
        response.sendRedirect(redirectUrl + "/login/success?access_token=" + accessToken);
//        response.sendRedirect(clientDeployUrl + "/login/success?access_token=" + accessToken);

        // sendResponseMsg 메소드 활용해서 로그인 성공 응답 보내기
//        sendResponseMsg(response, HttpServletResponse.SC_OK, new ApiResponse("로그인에 성공했습니다."));
    }

    private void sendResponseMsg(HttpServletResponse response, int statusCode, Object responseBody) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(new ObjectMapper().writeValueAsString(responseBody));
        writer.flush();
    }
}
