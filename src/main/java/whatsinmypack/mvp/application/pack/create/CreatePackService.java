package whatsinmypack.mvp.application.pack.create;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.adapter.in.web.pack.req.CreatePackRequest;
import whatsinmypack.mvp.domain.contextCategory.entity.ContextCategory;
import whatsinmypack.mvp.domain.contextCategory.port.ContextCategoryPersistencePort;
import whatsinmypack.mvp.domain.item.entity.Item;
import whatsinmypack.mvp.domain.item.port.ItemPersistencePort;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.pack.port.PackPersistencePort;
import whatsinmypack.mvp.domain.relation.entity.PackItem;
import whatsinmypack.mvp.domain.user.entity.User;

@Service
@Transactional
@RequiredArgsConstructor
public class CreatePackService implements CreatePackUseCase {

    private final PackPersistencePort packPersistencePort;
    private final ContextCategoryPersistencePort contextCategoryPersistencePort;
    private final ItemPersistencePort itemPersistencePort;

    @Override
    public Pack create(User user, CreatePackRequest request) {

        // 1. ContextCategory 조회 (이름 기반)
        ContextCategory contextCategory =
                contextCategoryPersistencePort.findByName(request.contextCategory());

        // 2. Pack 생성
        Pack pack = Pack.builder()
                .title(request.title())
                .introduction(request.review())
                .contextCategory(contextCategory)
                .build();

        // 3. Pack 생명주기 시작 (User 소유)
        user.addPack(pack);

        // 4. Item 조회
        List<Item> items = itemPersistencePort.findAllByIds(request.items());

        // 5. PackItem 생성 (관계만 설정)
        for (Item item : items) {
            PackItem packItem = new PackItem(pack, item);
            pack.getPackItems().add(packItem);
        }

        // 6. 저장
        return packPersistencePort.save(pack);
    }
}
