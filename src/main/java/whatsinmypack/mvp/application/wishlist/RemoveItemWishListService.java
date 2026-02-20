package whatsinmypack.mvp.application.wishlist;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.application.wishlist.port.ItemWishListPort;

@Service
@Transactional
@RequiredArgsConstructor
public class RemoveItemWishListService implements RemoveItemWishListUseCase {

    private final ItemWishListPort itemWishListPort;

    @Override
    public void remove(Long userId, Long itemId) {
        itemWishListPort.remove(userId, itemId);
    }
}
