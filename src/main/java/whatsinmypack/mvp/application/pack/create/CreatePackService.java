package whatsinmypack.mvp.application.pack.create;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.adapter.in.web.pack.req.CreatePackRequest;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.pack.port.PackPersistencePort;
import whatsinmypack.mvp.domain.user.entity.User;

@Service
@Transactional
@RequiredArgsConstructor
public class CreatePackService implements CreatePackUseCase {

    private final PackPersistencePort packPersistencePort;

    @Override
    public Pack create(User user, CreatePackRequest request) {

        return null;
    }
}
