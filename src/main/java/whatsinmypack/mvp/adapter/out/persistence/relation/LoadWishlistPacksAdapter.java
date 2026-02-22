package whatsinmypack.mvp.adapter.out.persistence.relation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import whatsinmypack.mvp.application.wishlist.port.LoadWishlistPacksPort;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.relation.entity.PackWishList;

@Component
@RequiredArgsConstructor
public class LoadWishlistPacksAdapter implements LoadWishlistPacksPort {

    private final PackWishListJpaRepository packWishListJpaRepository;

    @Override
    public List<Pack> loadByUserId(Long userId) {
        return packWishListJpaRepository.findActiveByUserIdWithPack(userId).stream()
                .map(PackWishList::getPack)
                .toList();
    }
}
