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
public class GetUserPacksService implements GetUserPacksUseCase {

    private final PackPersistencePort packPersistencePort;

    @Override
    public List<Pack> findUserPacks(User user) {
        return packPersistencePort.findUserPacks(user);
    }
}
