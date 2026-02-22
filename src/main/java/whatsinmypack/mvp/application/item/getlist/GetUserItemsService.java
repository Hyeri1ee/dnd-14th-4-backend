package whatsinmypack.mvp.application.item.getlist;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.domain.item.entity.Item;
import whatsinmypack.mvp.domain.item.port.ItemPersistencePort;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetUserItemsService implements GetUserItemsUseCase {

    private final ItemPersistencePort itemPersistencePort;

    @Override
    public List<Item> getItemsByUserId(Long userId) {
        return itemPersistencePort.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemPersistencePort.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("아이템을 찾을 수 없습니다. id=" + itemId));
    }
}
