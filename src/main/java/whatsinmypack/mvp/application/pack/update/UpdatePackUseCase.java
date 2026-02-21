package whatsinmypack.mvp.application.pack.update;

import whatsinmypack.mvp.adapter.in.web.pack.req.UpdatePackRequest;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.user.entity.User;

public interface UpdatePackUseCase {
    Pack update(Long packId, User user, UpdatePackRequest request);
}
