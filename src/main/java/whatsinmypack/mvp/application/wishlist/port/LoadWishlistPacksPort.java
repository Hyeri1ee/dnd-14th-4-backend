package whatsinmypack.mvp.application.wishlist.port;

import java.util.List;
import whatsinmypack.mvp.domain.pack.entity.Pack;

/**
 * 유저가 위시리스트로 설정한(is_wishlist=true) 팩 목록을 pack_id로 pack 테이블에서 조회.
 */
public interface LoadWishlistPacksPort {

    List<Pack> loadByUserId(Long userId);
}
