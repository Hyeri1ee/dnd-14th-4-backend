package whatsinmypack.mvp.adapter.out.persistence.pack;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Component;
import whatsinmypack.mvp.adapter.out.persistence.relation.PackWishListJpaRepository;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.pack.entity.SearchKeyword;
import whatsinmypack.mvp.domain.pack.port.PackPersistencePort;
import whatsinmypack.mvp.domain.user.entity.User;

@Component
@RequiredArgsConstructor
public class PackPersistenceAdapter implements PackPersistencePort {

    private final PackJpaRepository packJpaRepository;
    private final SearchKeywordJpaRepository searchKeywordJpaRepository;
    private final PackWishListJpaRepository packWishListJpaRepository;

    @Override
    public Pack findById(Long id) {
        return packJpaRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("아이디에 대응되는 팩이 없음")
        );
    }

    @Override
    public Pack save(Pack pack) {
        return packJpaRepository.save(pack);
    }

    @Override
    public void delete(Pack pack) {
        packJpaRepository.delete(pack);
    }

    @Override
    public void clearReferencesByPackId(Long packId) {
        packWishListJpaRepository.deleteByPack_Id(packId);
    }

    @Override
    public Slice<Pack> search(
            String keyword,
            List<String> contextNames,
            Pageable pageable
    ) {
        // 1. 컨텍스트 카테고리 빈 리스트 방어
        List<String> cns =
                (contextNames == null || contextNames.isEmpty()) ? null : contextNames;

        // 2. step 1: ID 조회 + 페이징
        Slice<Long> idSlice =
                packJpaRepository.searchPackIdsOrderByWishlist(keyword, cns, pageable);

        if (idSlice.isEmpty()) return new SliceImpl<>(List.of(), pageable, false);

        // 3. step 2: fetch join
        List<Pack> packs =
                packJpaRepository.findWithItemsByIds(idSlice.getContent());

        // 4. 정렬 복원
        Map<Long, Pack> map = packs.stream()
                .collect(Collectors.toMap(Pack::getId, Function.identity()));

        List<Pack> ordered = idSlice.getContent().stream()
                .map(map::get)
                .toList();

        // 5. Slice로 재조립
        return new SliceImpl<>(ordered, pageable, idSlice.hasNext());
    }

    @Override
    public void increaseSearchKeywordCount(String keyword) {
        searchKeywordJpaRepository.findByKeyword(keyword)
                .ifPresentOrElse(
                        SearchKeyword::increaseCount,
                        () -> searchKeywordJpaRepository.save(
                                SearchKeyword.builder()
                                        .keyword(keyword)
                                        .searchCount(1L)
                                        .build()
                        )
                );
    }

    @Override
    public List<String> findTop10PopularKeywords() {
        return searchKeywordJpaRepository.findTopKeywords(PageRequest.of(0, 10));
    }

    @Override
    public List<Pack> findUserPacks(User user) {
        return packJpaRepository.findByUser(user);
    }

    @Override
    public Pack findByIdWithItems(Long packId) {
        return packJpaRepository.findByIdWithItems(packId)
                .orElseThrow(() -> new IllegalArgumentException("팩을 찾을 수 없습니다."));
    }

    @Override
    public List<Pack> recommendPacks(Long contextId) {
        List<Long> topIds = packJpaRepository.findTopPackIdsByContextCategory(contextId,
                PageRequest.of(0, 10));

        return packJpaRepository.findWithItemsByIds(topIds);
    }

    @Override
    public List<Pack> findTop3LatestByContextCategory(Long contextId) {
        return packJpaRepository.findLatestByContextCategoryId(contextId, PageRequest.of(0, 3));
    }
}
