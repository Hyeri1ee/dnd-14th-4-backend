package whatsinmypack.mvp.application.pack.create;

import whatsinmypack.mvp.adapter.in.web.pack.req.CreatePackRequest;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.user.entity.User;

public interface CreatePackUseCase {

    Pack create(Long userId, CreatePackRequest request);

}
