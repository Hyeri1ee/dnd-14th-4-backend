package whatsinmypack.mvp.application.item.getlist;

import whatsinmypack.mvp.domain.item.entity.Item;

import java.util.List;

public interface GetUserItemsUseCase {

    List<Item> getItemsByUserId(Long userId);

    Item getItemById(Long itemId);
}
