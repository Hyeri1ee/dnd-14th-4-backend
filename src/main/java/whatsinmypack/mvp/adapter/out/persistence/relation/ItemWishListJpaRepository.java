package whatsinmypack.mvp.adapter.out.persistence.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import whatsinmypack.mvp.domain.relation.entity.ItemWishList;

import java.util.List;
import java.util.Optional;

public interface ItemWishListJpaRepository extends JpaRepository<ItemWishList, Long> {

    Optional<ItemWishList> findByUser_IdAndItem_Id(Long userId, Long itemId);

    List<ItemWishList> findByUser_IdAndIsWishlistTrue(Long userId);

    void deleteByUser_Id(Long userId);

    void deleteByItem_Id(Long itemId);

    @Query("""
        select iw.item.id
        from ItemWishList iw
        where iw.user.id = :userId
          and iw.isWishlist = true
          and iw.item.id in :itemIds
    """)
    List<Long> findWishlistedItemIdsByUserIdAndItemIds(
            @Param("userId") Long userId,
            @Param("itemIds") List<Long> itemIds
    );
}
