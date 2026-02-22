package whatsinmypack.mvp.application.wishlist;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.application.wishlist.port.LoadWishlistPacksPort;
import whatsinmypack.mvp.domain.pack.entity.Pack;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetWishlistPacksService implements GetWishlistPacksUseCase {

    private final LoadWishlistPacksPort loadWishlistPacksPort;

    @Override
    public List<Pack> getPacksByUserId(Long userId) {
        return loadWishlistPacksPort.loadByUserId(userId);
    }
}
