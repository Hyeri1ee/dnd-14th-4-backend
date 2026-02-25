package whatsinmypack.mvp.application.item.delete;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.domain.item.entity.Item;
import whatsinmypack.mvp.domain.item.entity.value.ItemImage;
import whatsinmypack.mvp.domain.item.port.ImageStoragePort;
import whatsinmypack.mvp.domain.item.port.ItemPersistencePort;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteItemService implements DeleteItemUseCase {

    private final ItemPersistencePort itemPersistencePort;
    private final ImageStoragePort imageStoragePort;

    @Override
    public void delete(Long itemId, Long userId) {
        Item item = itemPersistencePort.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("아이템을 찾을 수 없습니다. id=" + itemId));

        Long ownerId = item.getUser() != null ? item.getUser().getId() : null;
        if (!userId.equals(ownerId)) {
            throw new IllegalStateException("해당 아이템을 삭제할 권한이 없습니다.");
        }

        List<String> imageUrls = item.getImages().stream()
                .map(ItemImage::getPath)
                .toList();
        imageStoragePort.deleteAllByUrls(imageUrls);

        itemPersistencePort.clearReferencesByItemId(itemId);
        itemPersistencePort.delete(item);
    }
}
