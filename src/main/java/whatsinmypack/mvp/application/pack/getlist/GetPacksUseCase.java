package whatsinmypack.mvp.application.pack.getlist;

import java.util.List;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.user.entity.User;

public interface GetPacksUseCase {
    Pack findById(Long id); // 팩 개별 조회
    List<Pack> findUserPacks(User user); // 사용자 팩 조회
    List<Pack> findTopByContextCategory(List<Long> contextCategoryIds, int limit);
}
