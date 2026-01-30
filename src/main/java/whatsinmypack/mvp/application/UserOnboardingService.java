package whatsinmypack.mvp.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.domain.common.NotFoundEntityException;
import whatsinmypack.mvp.domain.user.entity.User;
import whatsinmypack.mvp.domain.user.exception.DuplicateNicknameException;
import whatsinmypack.mvp.domain.user.repository.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class UserOnboardingService {

    private final UserRepository userRepository;

    public void verifyAndUpdateNickname(String nickname, String email) {
        if (userRepository.existsByNickname(nickname))
            throw new DuplicateNicknameException(nickname, "이미 사용 중인 닉네임입니다");

        userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundEntityException(User.class, "해당 사용자를 찾을 수 없습니다"))
                .updateNickname(nickname);
    }
}
