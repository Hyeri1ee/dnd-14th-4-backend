package whatsinmypack.mvp.application.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import whatsinmypack.mvp.domain.user.entity.AgeGroup;
import whatsinmypack.mvp.domain.user.entity.Gender;
import whatsinmypack.mvp.domain.user.entity.User;
import whatsinmypack.mvp.domain.user.exception.DuplicateNicknameException;
import whatsinmypack.mvp.domain.user.port.LoadUserPort;
import whatsinmypack.mvp.domain.user.port.StoreProfileImagePort;
import whatsinmypack.mvp.domain.user.repository.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateProfileService implements UpdateProfileUseCase {

    private static final int NICKNAME_MAX_LENGTH = 10;
    private static final String NICKNAME_PATTERN = "^[a-z0-9가-힣]+$";

    private final LoadUserPort loadUserPort;
    private final StoreProfileImagePort storeProfileImagePort;
    private final UserRepository userRepository;

    @Override
    public User updateProfile(UpdateProfileCommand command) {
        User user = loadUserPort.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. userId=" + command.userId()));

        validateNickname(command.nickname(), user);

        String profileImageUrl = user.getProfileImage();
        if (command.profileImage() != null && !command.profileImage().isEmpty()) {
            profileImageUrl = storeProfileImagePort.store(command.profileImage(), command.userId());
        }

        user.updateProfile(
                command.nickname(),
                command.gender(),
                command.ageGroup(),
                profileImageUrl
        );
        return user;
    }

    private void validateNickname(String nickname, User currentUser) {
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("닉네임을 입력해주세요.");
        }
        if (nickname.length() > NICKNAME_MAX_LENGTH) {
            throw new IllegalArgumentException("닉네임은 " + NICKNAME_MAX_LENGTH + "자 이내로 입력해주세요.");
        }
        if (!nickname.matches(NICKNAME_PATTERN)) {
            throw new IllegalArgumentException("특수기호, 띄어쓰기, 영문 대문자는 사용할 수 없습니다.");
        }
        if (userRepository.existsByNickname(nickname) && !nickname.equals(currentUser.getNickname())) {
            throw new DuplicateNicknameException(nickname, "이미 사용 중인 닉네임입니다");
        }
    }
}
