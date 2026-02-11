package whatsinmypack.mvp.domain.user.port;

import whatsinmypack.mvp.domain.user.entity.User;

import java.util.Optional;


public interface LoadUserPort {

    Optional<User> findById(Long userId);
}
