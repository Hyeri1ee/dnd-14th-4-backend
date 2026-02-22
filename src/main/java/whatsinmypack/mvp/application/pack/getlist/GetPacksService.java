package whatsinmypack.mvp.application.pack.getlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.adapter.in.web.pack.res.PackRecommendationResponse;
import whatsinmypack.mvp.adapter.out.persistence.relation.UserContextCategoryJpaRepository;
import whatsinmypack.mvp.domain.contextCategory.entity.ContextCategory;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.pack.port.PackPersistencePort;
import whatsinmypack.mvp.domain.relation.entity.UserContextCategory;
import whatsinmypack.mvp.domain.user.entity.User;

@Service
@Transactional
@RequiredArgsConstructor
public class GetPacksService implements GetPacksUseCase {

    private final PackPersistencePort packPersistencePort;
    private final UserContextCategoryJpaRepository userContextCategoryJpaRepository;

    @Override
    public Pack findById(Long id) {
        return packPersistencePort.findById(id);
    }

    @Override
    public List<Pack> findUserPacks(User user) {
        return packPersistencePort.findUserPacks(user);
    }

    @Override
    public Map<Long, List<PackRecommendationResponse>> findTopByContextCategory(User user){
        List<ContextCategory> contextCategories =
                userContextCategoryJpaRepository.findByUserId(user.getId())
                        .stream().map(UserContextCategory::getContextCategory).toList();

        Map<Long, List<PackRecommendationResponse>> result = new HashMap<>();

        for (ContextCategory contextCategory : contextCategories) {

            // 1. 위시리스트 기준 상위 10개
            List<Pack> top10 =
                    packPersistencePort.recommendPacks(contextCategory.getId());

            if (top10.isEmpty()) {
                result.put(contextCategory.getId(), List.of());
                continue;
            }

            // 2. 이 카테고리 전용 랜덤 인덱스 3개
            List<Integer> indices = new ArrayList<>(IntStream.range(0, top10.size())
                    .boxed()
                    .toList());

            Collections.shuffle(indices);

            List<PackRecommendationResponse> picked =
                    indices.stream()
                            .limit(3)
                            .map(top10::get)
                            .map(PackRecommendationResponse::from)
                            .toList();

            // 3. 결과 저장
            result.put(contextCategory.getId(), picked);
        }

        return result;
    }
}
