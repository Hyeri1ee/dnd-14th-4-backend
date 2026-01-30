package whatsinmypack.mvp.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import whatsinmypack.mvp.global.dto.ApiResponse;
import whatsinmypack.mvp.global.security.jwt.JwtTokenProvider;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            @Nullable Authentication authentication) {
        log.info("로그아웃 핸들러 작동");
        String tokenValue = request.getHeader(AUTHORIZATION_HEADER);

        // 마지막까지 토큰 유효성 검증을 잊지말자:)
//        String decodedToken = URLDecoder.decode(tokenValue, StandardCharsets.UTF_8);
//        jwtTokenProvider.validateToken(decodedToken);
        jwtTokenProvider.validateToken(tokenValue);

        // sendResponseMsg 메소드로 로그아웃 응답 보내주기
        try {
            sendResponseMsg(response, HttpServletResponse.SC_OK, new ApiResponse("로그아웃이 완료됐습니다."));
        } catch (IOException e) {
            // IOException도 전역 예외 처리가 필요할 것 같은데.. 얘는 어떻게 구현해야 될까
            throw new RuntimeException(e);
        }
    }

    private void sendResponseMsg(HttpServletResponse response, int statusCode, Object responseBody) throws IOException {
        response.reset();
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(new ObjectMapper().writeValueAsString(responseBody));
        writer.flush();
    }
}
