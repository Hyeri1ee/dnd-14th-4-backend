package whatsinmypack.mvp.application.pack.getlist;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import whatsinmypack.mvp.domain.pack.entity.Pack;

public interface SearchPacksUseCase {
    Slice<Pack> search(
            String keyword,
            List<String> contextNames,
            Pageable pageable
    );
}
