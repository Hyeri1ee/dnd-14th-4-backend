package whatsinmypack.mvp.domain.user.port;

import whatsinmypack.mvp.domain.user.entity.User;

import java.util.Optional;

/**
 * User 조회 포트 (클린 아키텍처 - 아웃바운드)
 * Item 생성 Use Case가 "현재 사용자"를 조회할 때 이 포트만 의존한다.
 */
public interface LoadUserPort {

    Optional<User> findById(Long userId);
}
