package whatsinmypack.mvp.application.item;

import whatsinmypack.mvp.domain.item.entity.Item;

/**
 * 아이템 생성 Use Case
 */
public interface CreateItemUseCase {

    Item create(CreateItemCommand command);
}
