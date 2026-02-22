package whatsinmypack.mvp.adapter.out.persistence.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import whatsinmypack.mvp.domain.relation.entity.ItemWishList;

import java.util.List;
import java.util.Optional;

public interface ItemWishListJpaRepository extends JpaRepository<ItemWishList, Long> {

    Optional<ItemWishList> findByUser_IdAndItem_Id(Long userId, Long itemId);

    List<ItemWishList> findByUser_IdAndIsWishlistTrue(Long userId);
}
