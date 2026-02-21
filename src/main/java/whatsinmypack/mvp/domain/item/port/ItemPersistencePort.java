package whatsinmypack.mvp.domain.item.port;

import whatsinmypack.mvp.domain.item.entity.Item;

import java.util.List;
import java.util.Optional;

public interface ItemPersistencePort {

    Item save(Item item);

    List<Item> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Item> findById(Long id);

    List<Item> findAllByIds(List<Long> ids); // 팩 생성용 item 리스트 조회
}
