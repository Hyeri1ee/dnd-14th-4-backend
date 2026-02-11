package whatsinmypack.mvp.adapter.out.persistence.item;

import org.springframework.data.jpa.repository.JpaRepository;
import whatsinmypack.mvp.domain.item.entity.Item;


public interface ItemJpaRepository extends JpaRepository<Item, Long> {
}
