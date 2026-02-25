package whatsinmypack.mvp.adapter.out.persistence.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import whatsinmypack.mvp.domain.relation.entity.PackItem;

public interface PackItemJpaRepository extends JpaRepository<PackItem, Long> {

    void deleteByItem_Id(Long itemId);
}
