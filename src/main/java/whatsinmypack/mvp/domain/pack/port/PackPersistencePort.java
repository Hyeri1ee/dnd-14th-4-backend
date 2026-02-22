package whatsinmypack.mvp.domain.pack.port;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.user.entity.User;

public interface PackPersistencePort {

    Pack findById(Long id);

    Pack save(Pack pack);

    Slice<Pack> search(
            String keyword,
            List<String> contextNames,
            Pageable pageable
    );

    List<Pack> findUserPacks(User user);

    Pack findByIdWithItems(Long packId);

    List<Pack> recommendPacks(Long contextId);

    List<Pack> findTop3LatestByContextCategory(Long contextId);
}
