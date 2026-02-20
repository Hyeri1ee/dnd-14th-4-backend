package whatsinmypack.mvp.application.wishlist.port;

/**
 * 아이템 위시리스트 추가/삭제. item_wishlists 테이블에 is_wishlist 1(추가) 또는 0(삭제)으로 저장.
 */
public interface ItemWishListPort {

    void add(Long userId, Long itemId);

    void remove(Long userId, Long itemId);
}
