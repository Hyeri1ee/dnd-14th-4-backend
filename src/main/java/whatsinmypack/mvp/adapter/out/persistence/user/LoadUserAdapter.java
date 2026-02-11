package whatsinmypack.mvp.adapter.out.persistence.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import whatsinmypack.mvp.domain.user.entity.User;
import whatsinmypack.mvp.domain.user.port.LoadUserPort;
import whatsinmypack.mvp.domain.user.repository.UserRepository;

import java.util.Optional;

/**
 * User 조회 어댑터 (클린 아키텍처 - 아웃바운드 구현체)
 * Domain의 LoadUserPort를 구현하여 DB에서 사용자를 조회한다.
 */
@Component
@RequiredArgsConstructor
public class LoadUserAdapter implements LoadUserPort {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }
}
