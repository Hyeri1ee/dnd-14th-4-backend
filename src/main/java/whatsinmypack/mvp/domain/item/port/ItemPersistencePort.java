package whatsinmypack.mvp.domain.item.port;

import whatsinmypack.mvp.domain.item.entity.Item;


public interface ItemPersistencePort {

    Item save(Item item);
}
