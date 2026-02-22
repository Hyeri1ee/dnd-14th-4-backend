package whatsinmypack.mvp.application.pack.getlist;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.pack.port.PackPersistencePort;
import whatsinmypack.mvp.domain.user.entity.User;

@Service
@Transactional
@RequiredArgsConstructor
public class GetPacksService implements GetPacksUseCase {

    private final PackPersistencePort packPersistencePort;

    @Override
    public Pack findById(Long id) {
        return null; //TODO: 조회
    }

    @Override
    public List<Pack> findUserPacks(User user) {
        return packPersistencePort.findUserPacks(user);
    }

    @Override
    public List<Pack> findTopByContextCategory(List<Long> contextCategoryIds, int limit) {
        return null;
    }
}
