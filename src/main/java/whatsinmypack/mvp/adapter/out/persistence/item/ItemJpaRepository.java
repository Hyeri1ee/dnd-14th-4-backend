package whatsinmypack.mvp.adapter.out.persistence.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import whatsinmypack.mvp.domain.item.entity.Item;

import java.util.List;

public interface ItemJpaRepository extends JpaRepository<Item, Long> {

    //N+1 쿼리 fetch join으로 방지
    @Query("select distinct i from Item i left join fetch i.images where i.user.id = :userId order by i.createdAt desc")
    List<Item> findByUserIdOrderByCreatedAtDesc(Long userId);
}
