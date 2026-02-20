package whatsinmypack.mvp.application.wishlist.port;

import whatsinmypack.mvp.domain.item.entity.Item;

import java.util.List;

/**
 * 유저가 위시리스트로 설정한(is_wishlist=true) 아이템 목록을 item_id로 item 테이블에서 조회.
 */
public interface LoadWishlistItemsPort {

    List<Item> loadByUserId(Long userId);
}
