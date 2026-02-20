package whatsinmypack.mvp.adapter.out.persistence.relation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import whatsinmypack.mvp.application.contextcategories.port.ReplaceUserContextCategoriesPort;
import whatsinmypack.mvp.domain.contextCategory.entity.ContextCategory;
import whatsinmypack.mvp.domain.relation.entity.UserContextCategory;
import whatsinmypack.mvp.domain.user.entity.User;
import whatsinmypack.mvp.domain.user.port.LoadUserPort;
import whatsinmypack.mvp.adapter.out.persistence.contextcategory.ContextCategoryJpaRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReplaceUserContextCategoriesAdapter implements ReplaceUserContextCategoriesPort {

    private final LoadUserPort loadUserPort;
    private final UserContextCategoryJpaRepository userContextCategoryJpaRepository;
    private final ContextCategoryJpaRepository contextCategoryJpaRepository;

    @Override
    public void replace(Long userId, List<Long> contextCategoryIds) {
        userContextCategoryJpaRepository.deleteByUser_Id(userId);

        if (contextCategoryIds == null || contextCategoryIds.isEmpty()) {
            return;
        }

        List<Long> distinctIds = contextCategoryIds.stream().distinct().toList();
        if (distinctIds.size() > 3) {
            throw new IllegalArgumentException("관심 카테고리는 최대 3개까지 선택할 수 있습니다.");
        }

        User user = loadUserPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. userId=" + userId));

        List<ContextCategory> contextCategories = contextCategoryJpaRepository.findAllById(distinctIds);
        if (contextCategories.size() != distinctIds.size()) {
            throw new IllegalArgumentException("존재하지 않는 관심 카테고리 ID가 포함되어 있습니다.");
        }

        List<UserContextCategory> toSave = contextCategories.stream()
                .map(cc -> UserContextCategory.builder()
                        .user(user)
                        .contextCategory(cc)
                        .build())
                .toList();
        userContextCategoryJpaRepository.saveAll(toSave);
    }
}
