package whatsinmypack.mvp.application.wishlist;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.application.wishlist.port.ItemWishListPort;

@Service
@Transactional
@RequiredArgsConstructor
public class AddItemWishListService implements AddItemWishListUseCase {

    private final ItemWishListPort itemWishListPort;

    @Override
    public void add(Long userId, Long itemId) {
        itemWishListPort.add(userId, itemId);
    }
}
