package whatsinmypack.mvp.application.wishlist;

import java.util.List;
import whatsinmypack.mvp.domain.pack.entity.Pack;

public interface GetWishlistPacksUseCase {

    List<Pack> getPacksByUserId(Long userId);
}
