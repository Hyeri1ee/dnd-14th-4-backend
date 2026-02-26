package whatsinmypack.mvp.application.pack.create;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.adapter.in.web.pack.req.CreatePackRequest;
import whatsinmypack.mvp.adapter.out.persistence.item.ItemJpaRepository;
import whatsinmypack.mvp.adapter.out.persistence.pack.PackJpaRepository;
import whatsinmypack.mvp.adapter.out.persistence.relation.PackItemJpaRepository;
import whatsinmypack.mvp.domain.contextCategory.entity.ContextCategory;
import whatsinmypack.mvp.domain.contextCategory.port.ContextCategoryPersistencePort;
import whatsinmypack.mvp.domain.item.entity.Item;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.relation.entity.PackItem;
import whatsinmypack.mvp.domain.user.entity.User;
import whatsinmypack.mvp.domain.user.repository.UserRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CreatePackService implements CreatePackUseCase {

    private final PackJpaRepository packJpaRepository;
    private final PackItemJpaRepository packItemJpaRepository;
    private final ItemJpaRepository itemJpaRepository;
    private final UserRepository userRepository;
    private final ContextCategoryPersistencePort contextCategoryPersistencePort;

    public Pack create(Long userId, CreatePackRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        ContextCategory contextCategory =
                contextCategoryPersistencePort.findByName(request.contextCategory());

        List<Item> items = itemJpaRepository.findAllById(request.items());
        Map<Long, Item> itemById = new HashMap<>();
        for (Item item : items) {
            itemById.put(item.getId(), item);
        }

        for (Long itemId : request.items()) {
            if (!itemById.containsKey(itemId)) {
                throw new IllegalArgumentException("존재하지 않는 아이템입니다. id=" + itemId);
            }
        }

        // Pack 먼저 저장 (무조건)
        Pack pack = Pack.builder()
                .title(request.title())
                .introduction(request.review())
                .contextCategory(contextCategory)
                .user(user)
                .build();

        Pack save = packJpaRepository.save(pack);// 여기서 ID 확보

        // PackItem 직접 생성 + 저장
        List<PackItem> packItems = request.items().stream()
                .map(itemId -> new PackItem(save, itemById.get(itemId)))
                .toList();

        List<PackItem> saved = packItemJpaRepository.saveAll(packItems);
        saved.forEach(save::addPackItem);

        return packJpaRepository.findById(save.getId())
                .orElseThrow(() -> new IllegalStateException("팩 재조회 실패"));
    }
}
