package whatsinmypack.mvp.global.security.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KakaoApiService {

    @Value("${spring.security.oauth2.client.registration.kakao.admin-key}")
    private String adminKey;

    private final HttpClient httpClient;

    public KakaoApiService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10)) // 커넥션 타입아웃 세팅
                .build();
    }

    // 카카오 로그아웃
    public void logout(Long kakaoId) {
        String requestBody = "target_id_type=user_id&target_id=" + kakaoId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://kapi.kakao.com/v1/user/logout"))
                .header("Authorization", "KakaoAK " + adminKey)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            //TODO: 전역 예외 핸들러 달기
            if (response.statusCode() == 200) {
                log.info("카카오 로그아웃 성공: {}", response.body());
            } else {
                log.error("카카오 로그아웃 실패: status={}, body={}", response.statusCode(), response.body());
                throw new RuntimeException("카카오 로그아웃 실패");
            }
        } catch (IOException | InterruptedException e) {
            log.error("카카오 API 호출 중 오류 발생", e);
            throw new RuntimeException("카카오 로그아웃 실패", e);
        }
    }

    // 카카오 연동 해제
    public void unlink(Long kakaoId) {
        String requestBody = "target_id_type=user_id&target_id=" + kakaoId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://kapi.kakao.com/v1/user/unlink"))
                .header("Authorization", "KakaoAK " + adminKey)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            //TODO: 전역 예외 핸들러 달기
            if (response.statusCode() == 200) {
                log.info("카카오 연결 해제 성공: {}", response.body());
            } else {
                log.error("카카오 연결 해제 실패: status={}, body={}", response.statusCode(), response.body());
                throw new RuntimeException("카카오 연결 해제 실패");
            }
        } catch (IOException | InterruptedException e) {
            log.error("카카오 API 호출 중 오류 발생", e);
            throw new RuntimeException("카카오 연결 해제 실패", e);
        }
    }
}
