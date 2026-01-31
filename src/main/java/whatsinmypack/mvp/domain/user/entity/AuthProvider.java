package whatsinmypack.mvp.domain.user.entity;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthProvider {
    KAKAO("kakao"),
    NAVER("naver"),
    GOOGLE("google");

    private final String clientName;

    public static AuthProvider fromClientName(String clientName) {
        return Arrays.stream(values())
                .filter(p -> p.clientName.equalsIgnoreCase(clientName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 소셜 로그인: " + clientName));
    }
}
