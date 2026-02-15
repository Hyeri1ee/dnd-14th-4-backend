package whatsinmypack.mvp.adapter.out.persistence.pack;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import whatsinmypack.mvp.domain.pack.port.PackPersistencePort;

@Component
@RequiredArgsConstructor
public class PackPersistenceAdapter implements PackPersistencePort {

    private final PackJpaRepository packJpaRepository;

}
