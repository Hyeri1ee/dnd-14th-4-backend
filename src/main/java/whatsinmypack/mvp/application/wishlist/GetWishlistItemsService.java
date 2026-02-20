package whatsinmypack.mvp.application.wishlist;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.application.wishlist.port.LoadWishlistItemsPort;
import whatsinmypack.mvp.domain.item.entity.Item;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetWishlistItemsService implements GetWishlistItemsUseCase {

    private final LoadWishlistItemsPort loadWishlistItemsPort;

    @Override
    public List<Item> getItemsByUserId(Long userId) {
        return loadWishlistItemsPort.loadByUserId(userId);
    }
}
