package whatsinmypack.mvp.application.profile;

import whatsinmypack.mvp.domain.user.entity.AgeGroup;
import whatsinmypack.mvp.domain.user.entity.Gender;
import org.springframework.web.multipart.MultipartFile;

public record UpdateProfileCommand(
        Long userId,
        String nickname,
        Gender gender,
        AgeGroup ageGroup,
        MultipartFile profileImage
) {
}
