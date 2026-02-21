package whatsinmypack.mvp.application.contextcategories;

import java.util.List;

public interface UpdateUserContextCategoriesUseCase {

    /**
     * 해당 유저의 관심 context_category 를 교체한다. 기존 user_context_category 는 삭제 후 새로 저장.
     * 최대 3개까지 선택 가능.
     */
    void update(Long userId, List<Long> contextCategoryIds);
}
