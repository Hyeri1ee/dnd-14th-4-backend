package whatsinmypack.mvp.application.pack.getlist;

import java.util.List;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.user.entity.User;

public interface GetUserPacksUseCase {
    List<Pack> findUserPacks(User user);
}
