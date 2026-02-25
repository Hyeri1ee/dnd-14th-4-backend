package whatsinmypack.mvp.adapter.out.persistence.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import whatsinmypack.mvp.adapter.out.persistence.relation.ItemWishListJpaRepository;
import whatsinmypack.mvp.adapter.out.persistence.relation.PackItemJpaRepository;
import whatsinmypack.mvp.domain.item.entity.Item;
import whatsinmypack.mvp.domain.item.port.ItemPersistencePort;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ItemPersistenceAdapter implements ItemPersistencePort {

    private final ItemJpaRepository itemJpaRepository;
    private final ItemWishListJpaRepository itemWishListJpaRepository;
    private final PackItemJpaRepository packItemJpaRepository;

    @Override
    public Item save(Item item) {
        return itemJpaRepository.save(item);
    }

    @Override
    public List<Item> findByUserIdOrderByCreatedAtDesc(Long userId) {
        return itemJpaRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public Optional<Item> findById(Long id) {
        return itemJpaRepository.findById(id);
    }

    @Override
    public List<Item> findAllByIds(List<Long> ids) {
        return itemJpaRepository.findAllById(ids);
    }

    @Override
    public void delete(Item item) {
        itemJpaRepository.delete(item);
    }

    @Override
    public void clearReferencesByItemId(Long itemId) {
        packItemJpaRepository.deleteByItem_Id(itemId);
        itemWishListJpaRepository.deleteByItem_Id(itemId);
    }
}
