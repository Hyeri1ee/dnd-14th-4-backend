package whatsinmypack.mvp.application.wishlist;

import whatsinmypack.mvp.domain.item.entity.Item;

import java.util.List;

public interface GetWishlistItemsUseCase {

    List<Item> getItemsByUserId(Long userId);
}
