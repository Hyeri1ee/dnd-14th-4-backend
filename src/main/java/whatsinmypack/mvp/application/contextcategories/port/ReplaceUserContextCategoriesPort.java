package whatsinmypack.mvp.application.contextcategories.port;

import java.util.List;

/**
 * 유저의 관심 context_category 를 교체한다. 기존 user_context_category 는 삭제하고 새로 저장.
 */
public interface ReplaceUserContextCategoriesPort {

    void replace(Long userId, List<Long> contextCategoryIds);
}
