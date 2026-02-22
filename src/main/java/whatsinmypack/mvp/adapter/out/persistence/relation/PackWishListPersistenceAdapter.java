package whatsinmypack.mvp.adapter.out.persistence.relation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import whatsinmypack.mvp.adapter.out.persistence.pack.PackJpaRepository;
import whatsinmypack.mvp.application.wishlist.port.PackWishListPort;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.relation.entity.PackWishList;
import whatsinmypack.mvp.domain.user.entity.User;
import whatsinmypack.mvp.domain.user.port.LoadUserPort;

@Component
@RequiredArgsConstructor
public class PackWishListPersistenceAdapter implements PackWishListPort {

    private final PackWishListJpaRepository packWishListJpaRepository;
    private final LoadUserPort loadUserPort;
    private final PackJpaRepository packJpaRepository;

    @Override
    public void add(Long userId, Long packId) {
        User user = loadUserPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. userId=" + userId));
        Pack pack = packJpaRepository.findById(packId)
                .orElseThrow(() -> new IllegalArgumentException("팩을 찾을 수 없습니다. packId=" + packId));

        PackWishList wishList = packWishListJpaRepository.findByUser_IdAndPack_Id(userId, packId)
                .orElse(PackWishList.builder().user(user).pack(pack).isWishlist(true).build());
        wishList.setWishlist(true);
        packWishListJpaRepository.save(wishList);
    }

    @Override
    public void remove(Long userId, Long packId) {
        packWishListJpaRepository.findByUser_IdAndPack_Id(userId, packId)
                .ifPresent(wishList -> {
                    wishList.setWishlist(false);
                    packWishListJpaRepository.save(wishList);
                });
    }
}
