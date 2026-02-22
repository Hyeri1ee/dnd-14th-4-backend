package whatsinmypack.mvp.adapter.out.persistence.relation;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import whatsinmypack.mvp.domain.relation.entity.PackWishList;

public interface PackWishListJpaRepository extends JpaRepository<PackWishList, Long> {

    Optional<PackWishList> findByUser_IdAndPack_Id(Long userId, Long packId);
}
