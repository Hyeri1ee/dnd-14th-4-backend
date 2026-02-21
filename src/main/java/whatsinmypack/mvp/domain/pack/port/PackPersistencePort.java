package whatsinmypack.mvp.domain.pack.port;

import whatsinmypack.mvp.domain.pack.entity.Pack;

public interface PackPersistencePort {

    Pack save(Pack pack);
}
