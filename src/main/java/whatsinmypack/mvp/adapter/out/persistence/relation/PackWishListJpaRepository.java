package whatsinmypack.mvp.adapter.out.persistence.relation;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import whatsinmypack.mvp.domain.relation.entity.PackWishList;

public interface PackWishListJpaRepository extends JpaRepository<PackWishList, Long> {

    Optional<PackWishList> findByUser_IdAndPack_Id(Long userId, Long packId);

    List<PackWishList> findByUser_IdAndIsWishlistTrue(Long userId);

    void deleteByUser_Id(Long userId);

    @Query("""
        select distinct pw
        from PackWishList pw
        join fetch pw.pack p
        left join fetch p.user
        left join fetch p.contextCategory
        left join fetch p.packItems
        where pw.user.id = :userId
          and pw.isWishlist = true
    """)
    List<PackWishList> findActiveByUserIdWithPack(@Param("userId") Long userId);
}
