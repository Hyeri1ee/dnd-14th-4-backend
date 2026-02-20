package whatsinmypack.mvp.adapter.out.persistence.relation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import whatsinmypack.mvp.application.wishlist.port.ItemWishListPort;
import whatsinmypack.mvp.domain.item.entity.Item;
import whatsinmypack.mvp.domain.relation.entity.ItemWishList;
import whatsinmypack.mvp.domain.user.entity.User;
import whatsinmypack.mvp.domain.user.port.LoadUserPort;
import whatsinmypack.mvp.adapter.out.persistence.item.ItemJpaRepository;

@Component
@RequiredArgsConstructor
public class ItemWishListPersistenceAdapter implements ItemWishListPort {

    private final ItemWishListJpaRepository itemWishListJpaRepository;
    private final LoadUserPort loadUserPort;
    private final ItemJpaRepository itemJpaRepository;

    @Override
    public void add(Long userId, Long itemId) {
        User user = loadUserPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. userId=" + userId));
        Item item = itemJpaRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("아이템을 찾을 수 없습니다. itemId=" + itemId));

        ItemWishList wishList = itemWishListJpaRepository.findByUser_IdAndItem_Id(userId, itemId)
                .orElse(ItemWishList.builder().user(user).item(item).isWishlist(true).build());
        wishList.setWishlist(true);
        itemWishListJpaRepository.save(wishList);
    }

    @Override
    public void remove(Long userId, Long itemId) {
        itemWishListJpaRepository.findByUser_IdAndItem_Id(userId, itemId)
                .ifPresent(wishList -> {
                    wishList.setWishlist(false);
                    itemWishListJpaRepository.save(wishList);
                });
    }
}
