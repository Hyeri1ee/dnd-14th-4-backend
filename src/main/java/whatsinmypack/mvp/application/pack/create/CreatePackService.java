package whatsinmypack.mvp.application.pack.create;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.domain.pack.port.PackPersistencePort;

@Service
@Transactional
@RequiredArgsConstructor
public class CreatePackService implements CreatePackUseCase {

    private final PackPersistencePort packPersistencePort;
    
}
