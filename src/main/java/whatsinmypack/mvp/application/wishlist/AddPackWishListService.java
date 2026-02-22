package whatsinmypack.mvp.application.wishlist;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.application.wishlist.port.PackWishListPort;

@Service
@Transactional
@RequiredArgsConstructor
public class AddPackWishListService implements AddPackWishListUseCase {

    private final PackWishListPort packWishListPort;

    @Override
    public void add(Long userId, Long packId) {
        packWishListPort.add(userId, packId);
    }
}
