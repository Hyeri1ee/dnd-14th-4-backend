package whatsinmypack.mvp.application.contextcategories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.application.contextcategories.port.ReplaceUserContextCategoriesPort;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateUserContextCategoriesService implements UpdateUserContextCategoriesUseCase {

    private static final int MAX_CONTEXT_CATEGORIES = 3;

    private final ReplaceUserContextCategoriesPort replaceUserContextCategoriesPort;

    @Override
    public void update(Long userId, List<Long> contextCategoryIds) {
        if (contextCategoryIds == null) {
            replaceUserContextCategoriesPort.replace(userId, List.of());
            return;
        }
        if (contextCategoryIds.size() > MAX_CONTEXT_CATEGORIES) {
            throw new IllegalArgumentException("관심 카테고리는 최대 " + MAX_CONTEXT_CATEGORIES + "개까지 선택할 수 있습니다.");
        }
        replaceUserContextCategoriesPort.replace(userId, contextCategoryIds);
    }
}
