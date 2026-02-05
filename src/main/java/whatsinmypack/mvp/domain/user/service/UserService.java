package whatsinmypack.mvp.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.domain.user.entity.User;
import whatsinmypack.mvp.domain.user.repository.UserRepository;
import whatsinmypack.mvp.global.security.service.KakaoApiService;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KakaoApiService kakaoApiService;

    public void deleteUser(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("사용자 이메일이 조회되지 않음"));

        kakaoApiService.unlink(user.getKakaoId());
        userRepository.delete(user);
    }
}
