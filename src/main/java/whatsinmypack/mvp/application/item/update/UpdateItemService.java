package whatsinmypack.mvp.application.item.update;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.domain.item.entity.Item;
import whatsinmypack.mvp.domain.item.entity.value.ItemImage;
import whatsinmypack.mvp.domain.item.entity.value.ItemTag;
import whatsinmypack.mvp.domain.item.port.ItemPersistencePort;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateItemService implements UpdateItemUseCase {

    private static final int MAX_REVIEW_IMAGES = 5;
    private static final int MAX_TAGS = 5;
    private static final int MAX_TAG_LENGTH = 10;

    private final ItemPersistencePort itemPersistencePort;

    @Override
    public Item update(UpdateItemCommand command) {
        Item item = itemPersistencePort.findById(command.itemId())
                .orElseThrow(() -> new IllegalArgumentException("아이템을 찾을 수 없습니다. id=" + command.itemId()));

        //권한 확인
        if (!item.getUser().getId().equals(command.userId())) {
            throw new IllegalStateException("해당 아이템을 수정할 권한이 없습니다.");
        }

        //정보 업데이트
        item.update(
                command.productName(),
                command.brandName(),
                command.reviewText(),
                command.satisfaction(),
                command.usePeriod(),
                command.purchaseLocation()
        );

        //이미지 업데이트
        updateImages(item, command.reviewImagePaths());
        
        //태그 업데이트
        updateTags(item, command.tags());

        return item;
    }

    private void updateImages(Item item, List<String> paths) {
        item.clearImages();
        if (paths == null || paths.isEmpty()) return;
        if (paths.size() > MAX_REVIEW_IMAGES) {
            throw new IllegalArgumentException("리뷰 이미지는 최대 " + MAX_REVIEW_IMAGES + "개까지 등록할 수 있습니다.");
        }
        for (String path : paths) {
            item.addImage(ItemImage.builder().path(path).build());
        }
    }

    private void updateTags(Item item, List<String> tagValues) {
        item.clearTags();
        if (tagValues == null || tagValues.isEmpty()) return;
        if (tagValues.size() > MAX_TAGS) {
            throw new IllegalArgumentException("태그는 최대 " + MAX_TAGS + "개까지 등록할 수 있습니다.");
        }
        for (String value : tagValues) {
            if (value != null && value.length() > MAX_TAG_LENGTH) {
                throw new IllegalArgumentException("태그는 태그당 최대 " + MAX_TAG_LENGTH + "자까지 입력할 수 있습니다.");
            }
            if (value != null && !value.isBlank()) {
                item.addTag(ItemTag.builder().tag(value.trim()).build());
            }
        }
    }
}
