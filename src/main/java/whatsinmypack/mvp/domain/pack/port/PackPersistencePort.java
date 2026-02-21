package whatsinmypack.mvp.domain.pack.port;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.user.entity.User;

public interface PackPersistencePort {

    Pack save(Pack pack);

    Slice<Pack> search(
            String keyword,
            List<String> contextNames,
            Pageable pageable
    );

    List<Pack> findUserPacks(User user);
}
