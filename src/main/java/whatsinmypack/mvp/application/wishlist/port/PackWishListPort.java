package whatsinmypack.mvp.application.wishlist.port;

/**
 * 팩 위시리스트 추가/삭제. pack_wishlists 테이블에 is_wishlist 1(추가) 또는 0(삭제)으로 저장.
 */
public interface PackWishListPort {

    void add(Long userId, Long packId);

    void remove(Long userId, Long packId);
}
