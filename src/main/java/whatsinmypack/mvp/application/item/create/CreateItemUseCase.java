package whatsinmypack.mvp.application.item.create;

import whatsinmypack.mvp.domain.item.entity.Item;


public interface CreateItemUseCase {

    Item create(CreateItemCommand command);
}
