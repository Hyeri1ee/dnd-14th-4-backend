package whatsinmypack.mvp.application.pack.update;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.adapter.in.web.pack.req.UpdatePackRequest;
import whatsinmypack.mvp.domain.item.entity.Item;
import whatsinmypack.mvp.domain.item.port.ItemPersistencePort;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.pack.port.PackPersistencePort;
import whatsinmypack.mvp.domain.relation.entity.PackItem;
import whatsinmypack.mvp.domain.user.entity.User;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdatePackService implements UpdatePackUseCase {

    private final PackPersistencePort packPersistencePort;
    private final ItemPersistencePort itemPersistencePort;

    @Override
    public Pack update(Long packId, User user, UpdatePackRequest request) {
        Pack pack = packPersistencePort.findByIdWithItems(packId);

        // 1. 권한 체크
        if (!pack.getUser().getId().equals(user.getId()))
            throw new IllegalArgumentException("사용자의 팩이 아님");

        // 2. introduction 수정
        if (request.introduction() != null) pack.setIntroduction(request.introduction());

        // 3. PackItem 제거
        if (request.removeItems() != null && !request.removeItems().isEmpty()) {
            pack.getPackItems().removeIf(
                    pi -> request.removeItems().contains(pi.getItem().getId())
            );
        }

        // 4. PackItem 추가
        if (request.addItems() != null && !request.addItems().isEmpty()) {
            List<Item> items = itemPersistencePort.findAllByIds(request.addItems());

            for (Item item : items) {
                boolean exists = pack.getPackItems().stream()
                        .anyMatch(pi -> pi.getItem().getId().equals(item.getId()));

                if (!exists) {
                    pack.getPackItems().add(
                            PackItem.builder()
                                    .pack(pack)
                                    .item(item)
                                    .build()
                    );
                }
            }
        }

        return pack;
    }
}
