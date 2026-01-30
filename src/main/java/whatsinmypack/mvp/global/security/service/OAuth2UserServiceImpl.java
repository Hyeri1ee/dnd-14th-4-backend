package whatsinmypack.mvp.global.security.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import whatsinmypack.mvp.domain.user.entity.AuthProvider;
import whatsinmypack.mvp.domain.user.entity.User;
import whatsinmypack.mvp.domain.user.repository.UserRepository;
import whatsinmypack.mvp.global.security.info.KakaoUserInfo;
import whatsinmypack.mvp.global.security.info.OAuth2UserInfo;
import whatsinmypack.mvp.global.security.user.UserDetailsImpl;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        log.info("일단 여긴 오나?");

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String oAUthClientName = userRequest.getClientRegistration().getClientName();
        OAuth2UserInfo userInfo = getOAuth2UserInfo(oAUthClientName, oAuth2User.getAttributes());

//        log.info("사용자 이메일: {}", userInfo.getEmail());
//        log.info("사용자 소셜 로그인 인증 제공처: {}", userInfo.getProvider().getClientName());
//        log.info("사용자 프로필 이미지: {}", userInfo.getProfileImage());

        // 로그인과 회원가입 동시 처리
        User user = userRepository.findByEmail(userInfo.getEmail()) // 있으면 로그인
                .map(u -> validateAuthProvider(u, userInfo.getProvider())) // 중복 방지
                .orElseGet(() -> createUser(userInfo)); // 없으면 회원가입

        return new UserDetailsImpl(user, oAuth2User.getAttributes());
    }

    // 로그인 제공수단 검증
    private OAuth2UserInfo getOAuth2UserInfo(String clientName, Map<String, Object> attributes) {
        AuthProvider provider = AuthProvider.fromClientName(clientName);

        return switch (provider) {
            case KAKAO -> new KakaoUserInfo(attributes);
            // 향후 추가 전략들 생성 예정(구글, 네이버...)

            default -> throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인: " + clientName);
        };
    }

    // 로그인 제공수단 상호 비교를 통한 중복 이메일 가입 방지
    private User validateAuthProvider(User user, AuthProvider provider) {
        if (user.getAuthProvider() != provider) {
            throw new OAuth2AuthenticationException(
                    String.format("이미 %s 계정으로 가입된 이메일입니다.", user.getAuthProvider())
            );
        }

        return user;
    }

    // 사용자 엔티티 생성
    private User createUser(OAuth2UserInfo userInfo) {
        User user = User.builder()
                .email(userInfo.getEmail())
                .authProvider(userInfo.getProvider())
                .profileImage(userInfo.getProfileImage())
                .build();

        return userRepository.save(user);
    }
}
