package whatsinmypack.mvp.adapter.out.persistence.relation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import whatsinmypack.mvp.application.mypage.MyPageProfile;
import whatsinmypack.mvp.application.mypage.port.LoadMyPageProfilePort;
import whatsinmypack.mvp.domain.relation.entity.UserContextCategory;
import whatsinmypack.mvp.domain.user.entity.User;
import whatsinmypack.mvp.domain.user.port.LoadUserPort;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LoadMyPageProfileAdapter implements LoadMyPageProfilePort {

    private static final String[] DEFAULT_PROFILE_COLORS = {"yellow", "red", "blue", "green", "purple"};

    private final LoadUserPort loadUserPort;
    private final UserContextCategoryJpaRepository userContextCategoryJpaRepository;

    @Override
    public MyPageProfile loadByUserId(Long userId) {
        User user = loadUserPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. userId=" + userId));

        String name = user.getNickname();
        String profileImageUrl = resolveProfileImageUrl(user.getProfileImage(), userId);
        String gender = user.getGender() != null ? user.getGender().name() : null;
        String age = user.getAgeGroup() != null ? user.getAgeGroup().name() : null;

        List<String> categoryNames = userContextCategoryJpaRepository.findByUserId(userId).stream()
                .map(UserContextCategory::getContextCategory)
                .map(cc -> cc.getName() != null ? cc.getName() : "")
                .toList();

        return new MyPageProfile(name, profileImageUrl, gender, age, categoryNames);
    }

    /**
     * DB 프로필 이미지가 null/비어 있으면 userId 기반 해시로 기본 색상 문자열 반환 (0=yellow, 1=red, 2=blue, 3=green, 4=purple).
     */
    private String resolveProfileImageUrl(String profileImage, Long userId) {
        if (profileImage != null && !profileImage.isBlank()) {
            return profileImage;
        }
        long hash = Math.floorMod(userId * 31L + 17L, 5L);
        return DEFAULT_PROFILE_COLORS[(int) hash];
    }
}
