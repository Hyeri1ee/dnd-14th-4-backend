package whatsinmypack.mvp.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.domain.user.entity.User;
import whatsinmypack.mvp.domain.user.service.UserService;
import whatsinmypack.mvp.global.security.service.KakaoApiService;

@Service
@Transactional
@RequiredArgsConstructor
public class UserProfileService {

    private final UserService userService;
    private final KakaoApiService kakaoApiService;

    public void withdraw(User user) {
        kakaoApiService.unlink(user.getKakaoId());
        userService.deleteUser(user.getEmail());
    }
}
