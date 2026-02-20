package whatsinmypack.mvp.adapter.out.persistence.relation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import whatsinmypack.mvp.application.mypage.MyPageProfile;
import whatsinmypack.mvp.application.mypage.port.LoadMyPageProfilePort;
import whatsinmypack.mvp.domain.relation.entity.UserContextCategory;
import whatsinmypack.mvp.domain.user.port.LoadUserPort;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LoadMyPageProfileAdapter implements LoadMyPageProfilePort {

    private final LoadUserPort loadUserPort;
    private final UserContextCategoryJpaRepository userContextCategoryJpaRepository;

    @Override
    public MyPageProfile loadByUserId(Long userId) {
        String profileImageUrl = loadUserPort.findById(userId)
                .map(user -> user.getProfileImage() != null ? user.getProfileImage() : "")
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. userId=" + userId));

        List<String> categoryNames = userContextCategoryJpaRepository.findByUserId(userId).stream()
                .map(UserContextCategory::getContextCategory)
                .map(cc -> cc.getName() != null ? cc.getName() : "")
                .toList();

        return new MyPageProfile(profileImageUrl, categoryNames);
    }
}
