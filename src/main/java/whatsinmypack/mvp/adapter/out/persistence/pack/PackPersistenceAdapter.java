package whatsinmypack.mvp.adapter.out.persistence.pack;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Component;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.pack.port.PackPersistencePort;
import whatsinmypack.mvp.domain.user.entity.User;

@Component
@RequiredArgsConstructor
public class PackPersistenceAdapter implements PackPersistencePort {

    private final PackJpaRepository packJpaRepository;

    @Override
    public Pack save(Pack pack) {
        return packJpaRepository.save(pack);
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
    public List<Pack> findUserPacks(User user) {
        return packJpaRepository.findByUser(user);
    }
}
