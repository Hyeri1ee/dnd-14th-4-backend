package whatsinmypack.mvp.application.pack.getlist;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.pack.port.PackPersistencePort;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchPacksService implements SearchPacksUseCase {

    private final PackPersistencePort packPersistencePort;

    @Override
    public Slice<Pack> search(
            String keyword,
            List<String> contextNames,
            Pageable pageable
    ) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        if (normalizedKeyword.isBlank()) {
            return new SliceImpl<>(List.of(), pageable, false);
        }

        packPersistencePort.increaseSearchKeywordCount(normalizedKeyword);
        return packPersistencePort.search(normalizedKeyword, contextNames, pageable);
    }

    @Override
    public List<String> getPopularKeywords() {
        return packPersistencePort.findTop10PopularKeywords();
    }
}
