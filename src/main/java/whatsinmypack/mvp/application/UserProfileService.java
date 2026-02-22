package whatsinmypack.mvp.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.adapter.out.persistence.item.ItemJpaRepository;
import whatsinmypack.mvp.adapter.out.persistence.pack.PackJpaRepository;
import whatsinmypack.mvp.adapter.out.persistence.relation.ItemWishListJpaRepository;
import whatsinmypack.mvp.adapter.out.persistence.relation.PackWishListJpaRepository;
import whatsinmypack.mvp.adapter.out.persistence.relation.UserContextCategoryJpaRepository;
import whatsinmypack.mvp.domain.user.entity.User;
import whatsinmypack.mvp.domain.user.exception.DuplicateNicknameException;
import whatsinmypack.mvp.domain.user.repository.UserRepository;
import whatsinmypack.mvp.global.security.service.KakaoApiService;

@Service
@Transactional
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final KakaoApiService kakaoApiService;
    private final ItemWishListJpaRepository itemWishListJpaRepository;
    private final PackWishListJpaRepository packWishListJpaRepository;
    private final UserContextCategoryJpaRepository userContextCategoryJpaRepository;
    private final ItemJpaRepository itemJpaRepository;
    private final PackJpaRepository packJpaRepository;

    public void withdraw(User user) {
        kakaoApiService.unlink(user.getKakaoId());
        Long userId = user.getId();
        itemWishListJpaRepository.deleteByUser_Id(userId);
        packWishListJpaRepository.deleteByUser_Id(userId);
        userContextCategoryJpaRepository.deleteByUser_Id(userId);
        itemJpaRepository.clearUserReference(userId);
        packJpaRepository.clearUserReference(userId);
        userRepository.delete(user);
    }

    public void verifyAndUpdateNickname(String nickname, User user) {
        if (userRepository.existsByNickname(nickname))
            throw new DuplicateNicknameException(nickname, "이미 사용 중인 닉네임입니다");

        user.updateNickname(nickname);
    }
}
