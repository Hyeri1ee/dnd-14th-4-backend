package whatsinmypack.mvp.domain.item.port;

import whatsinmypack.mvp.domain.item.entity.Item;

import java.util.List;

public interface ItemPersistencePort {

    Item save(Item item);

    List<Item> findByUserIdOrderByCreatedAtDesc(Long userId);
}
