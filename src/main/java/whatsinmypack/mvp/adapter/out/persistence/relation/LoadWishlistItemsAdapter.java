package whatsinmypack.mvp.adapter.out.persistence.relation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import whatsinmypack.mvp.application.wishlist.port.LoadWishlistItemsPort;
import whatsinmypack.mvp.domain.item.entity.Item;
import whatsinmypack.mvp.domain.relation.entity.ItemWishList;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LoadWishlistItemsAdapter implements LoadWishlistItemsPort {

    private final ItemWishListJpaRepository itemWishListJpaRepository;

    @Override
    public List<Item> loadByUserId(Long userId) {
        return itemWishListJpaRepository.findByUser_IdAndIsWishlistTrueWithItem(userId).stream()
                .map(ItemWishList::getItem)
                .toList();
    }
}
