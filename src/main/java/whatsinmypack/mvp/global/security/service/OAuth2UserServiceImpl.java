package whatsinmypack.mvp.global.security.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import whatsinmypack.mvp.domain.user.entity.User;
import whatsinmypack.mvp.domain.user.repository.UserRepository;
import whatsinmypack.mvp.global.security.info.KakaoUserInfo;
import whatsinmypack.mvp.global.security.info.OAuth2UserInfo;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String oAUthClientName = userRequest.getClientRegistration().getClientName();
        OAuth2UserInfo userInfo = getOAuth2UserInfo(oAUthClientName, oAuth2User.getAttributes());

        // 로그인 혹은 회원가입을 동시 처리
        User user = userRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> createUser(userInfo));

        return oAuth2User;
    }

    // 로그인 제공수단 검증
    private OAuth2UserInfo getOAuth2UserInfo(String clientName, Map<String, Object> attributes) {
        if ("kakao".equalsIgnoreCase(clientName)) {
            return new KakaoUserInfo(attributes);
        } // 향후 추가 전략들 생성 예정(구글, 네이버...)

        throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인: " + clientName);
    }

    // 사용자 엔티티 생성
    private User createUser(OAuth2UserInfo userInfo) {
        return null;
    }
}
