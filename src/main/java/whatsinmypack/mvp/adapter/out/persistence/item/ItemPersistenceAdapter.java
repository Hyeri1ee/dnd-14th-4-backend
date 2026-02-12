package whatsinmypack.mvp.adapter.out.persistence.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import whatsinmypack.mvp.domain.item.entity.Item;
import whatsinmypack.mvp.domain.item.port.ItemPersistencePort;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemPersistenceAdapter implements ItemPersistencePort {

    private final ItemJpaRepository itemJpaRepository;

    @Override
    public Item save(Item item) {
        return itemJpaRepository.save(item);
    }

    @Override
    public List<Item> findByUserIdOrderByCreatedAtDesc(Long userId) {
        return itemJpaRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
