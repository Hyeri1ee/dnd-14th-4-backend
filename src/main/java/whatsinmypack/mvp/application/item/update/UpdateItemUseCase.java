package whatsinmypack.mvp.application.item.update;

import whatsinmypack.mvp.domain.item.entity.Item;

public interface UpdateItemUseCase {
    Item update(UpdateItemCommand command);
}
