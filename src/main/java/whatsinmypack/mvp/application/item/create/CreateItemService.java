package whatsinmypack.mvp.application.item.create;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.domain.item.port.ImageStoragePort;
import whatsinmypack.mvp.domain.item.entity.Item;
import whatsinmypack.mvp.domain.item.entity.value.ItemImage;
import whatsinmypack.mvp.domain.item.entity.value.ItemTag;
import whatsinmypack.mvp.domain.item.port.ItemPersistencePort;
import whatsinmypack.mvp.domain.user.entity.User;
import whatsinmypack.mvp.domain.user.port.LoadUserPort;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CreateItemService implements CreateItemUseCase {

    private static final int MAX_REVIEW_IMAGES = 5;
    private static final int MAX_TAGS = 5;
    private static final int MAX_TAG_LENGTH = 10;

    private final ItemPersistencePort itemPersistencePort;
    private final LoadUserPort loadUserPort;
    private final ImageStoragePort imageStoragePort;

    @Override
    public Item create(CreateItemCommand command) {
        User user = loadUserPort.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. userId=" + command.userId()));
        log.info("유저 인증은 잘 통과함: {}", user.getEmail());

        Item item = Item.builder()
                .title(command.productName())
                .brand(command.brandName())
                .review(command.reviewText())
                .satisfaction(command.satisfaction())
                .usePeriod(command.usePeriod())
                .purchase(command.purchaseLocation())
                .user(user)
                .build();
        validateAndAddTags(item, command.tags());

        Item savedItem = itemPersistencePort.save(item);
        log.info("이미지 없이 일단 처음 저장한 아이템: {}", savedItem.getTitle());

        List<String> imagePaths = imageStoragePort.store(
                command.reviewImages(),
                command.userId(),
                savedItem.getId(),
                command.productName()
        );
        validateAndAddImages(savedItem, imagePaths);
        Item result = itemPersistencePort.save(savedItem);
        result.getImages().forEach(e -> log.info("진짜 이미지가 저장됐니? : {}", e.getPath()));

        return result;
    }

    private void validateAndAddImages(Item item, List<String> paths) {
        if (paths == null || paths.isEmpty()) return;
        if (paths.size() > MAX_REVIEW_IMAGES) {
            throw new IllegalArgumentException("리뷰 이미지는 최대 " + MAX_REVIEW_IMAGES + "개까지 등록할 수 있습니다.");
        }
        for (String path : paths) {
            item.addImage(ItemImage.fromPath(path));
        }
    }

    private void validateAndAddTags(Item item, List<String> tagValues) {
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
