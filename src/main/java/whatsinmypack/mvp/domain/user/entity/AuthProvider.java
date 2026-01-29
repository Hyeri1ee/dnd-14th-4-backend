package whatsinmypack.mvp.domain.user.entity;

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
        for (AuthProvider provider : values()) {
            if (provider.clientName.equalsIgnoreCase(clientName)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 소셜 로그인: " + clientName);
    }
}
