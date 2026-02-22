package whatsinmypack.mvp.application.pack.getlist;

import java.util.List;
import java.util.Map;
import whatsinmypack.mvp.adapter.in.web.pack.res.PackRecommendationResponse;
import whatsinmypack.mvp.adapter.in.web.pack.res.PackSummaryResponse;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.user.entity.User;

public interface GetPacksUseCase {
    Pack findById(Long id); // 팩 개별 조회
    List<Pack> findUserPacks(User user); // 사용자 팩 조회
    Map<Long, List<PackRecommendationResponse>> findTopByContextCategory(User user);
    Map<String, List<PackSummaryResponse>> findLatestTop3ByContextCategory();
}
