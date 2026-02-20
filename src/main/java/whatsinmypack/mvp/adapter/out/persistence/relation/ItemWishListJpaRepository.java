package whatsinmypack.mvp.adapter.out.persistence.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import whatsinmypack.mvp.domain.relation.entity.ItemWishList;

import java.util.List;
import java.util.Optional;

public interface ItemWishListJpaRepository extends JpaRepository<ItemWishList, Long> {

    Optional<ItemWishList> findByUser_IdAndItem_Id(Long userId, Long itemId);

    @Query("select distinct w from ItemWishList w join fetch w.item i left join fetch i.images where w.user.id = :userId and w.isWishlist = true")
    List<ItemWishList> findByUser_IdAndIsWishlistTrueWithItem(Long userId);
}
