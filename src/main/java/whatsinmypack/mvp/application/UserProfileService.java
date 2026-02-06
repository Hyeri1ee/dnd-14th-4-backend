package whatsinmypack.mvp.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.domain.user.entity.User;
import whatsinmypack.mvp.domain.user.exception.DuplicateNicknameException;
import whatsinmypack.mvp.domain.user.repository.UserRepository;
import whatsinmypack.mvp.global.security.service.KakaoApiService;

@Service
@Transactional
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final KakaoApiService kakaoApiService;

    public void withdraw(User user) {
        kakaoApiService.unlink(user.getKakaoId());
        userRepository.delete(user);
    }

    public void verifyAndUpdateNickname(String nickname, User user) {
        if (userRepository.existsByNickname(nickname))
            throw new DuplicateNicknameException(nickname, "이미 사용 중인 닉네임입니다");

        user.updateNickname(nickname);
    }
}
