package whatsinmypack.mvp.adapter.out.persistence.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import whatsinmypack.mvp.domain.user.entity.User;
import whatsinmypack.mvp.domain.user.port.LoadUserPort;
import whatsinmypack.mvp.domain.user.repository.UserRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoadUserAdapter implements LoadUserPort {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }
}
