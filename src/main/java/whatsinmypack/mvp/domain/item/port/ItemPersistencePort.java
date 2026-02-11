package whatsinmypack.mvp.domain.item.port;

import whatsinmypack.mvp.domain.item.entity.Item;

/**
 * Item 도메인 영속성 포트 (클린 아키텍처 - 아웃바운드)
 * Use Case는 이 인터페이스에만 의존하고, 구현은 Adapter가 담당한다.
 */
public interface ItemPersistencePort {

    Item save(Item item);
}
