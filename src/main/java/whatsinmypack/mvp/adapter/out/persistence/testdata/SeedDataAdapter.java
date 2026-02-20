package whatsinmypack.mvp.adapter.out.persistence.testdata;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.application.testdata.port.SeedDataPort;
import whatsinmypack.mvp.domain.item.entity.Item;
import whatsinmypack.mvp.domain.item.entity.Satisfaction;
import whatsinmypack.mvp.domain.item.entity.UsePeriod;
import whatsinmypack.mvp.domain.user.entity.AgeGroup;
import whatsinmypack.mvp.domain.user.entity.AuthProvider;
import whatsinmypack.mvp.domain.user.entity.Gender;
import whatsinmypack.mvp.domain.user.entity.User;
import whatsinmypack.mvp.domain.user.repository.UserRepository;
import whatsinmypack.mvp.adapter.out.persistence.item.ItemJpaRepository;

@Component
@RequiredArgsConstructor
public class SeedDataAdapter implements SeedDataPort {

    private static final String TEST_USER_1_EMAIL = "user_1@test.com";
    private static final String TEST_USER_2_EMAIL = "user_2@test.com";

    private final UserRepository userRepository;
    private final ItemJpaRepository itemJpaRepository;

    @Override
    @Transactional
    public void deleteSeed() {
        removeExistingTestData();
    }

    @Override
    @Transactional
    public void seed() {
        removeExistingTestData();
        User user1 = createUser(TEST_USER_1_EMAIL, "테스트유저1", 1001L);
        User user2 = createUser(TEST_USER_2_EMAIL, "테스트유저2", 1002L);
        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        createItem(user1, "원통형 2홀 연필깎이", "스테들러", "가벼우면서 잘 깎여요", Satisfaction.GOOD, UsePeriod.ABOVE_ONE_YEAR, "쿠팡");
        createItem(user1, "미니 다이어리", "무브", "일정 관리 필수템", Satisfaction.VERY_GOOD, UsePeriod.BELOW_ONE_YEAR, "올리브영");
        createItem(user1, "볼펜 0.5", "제트스트림", "글씨가 잘 써요", Satisfaction.MUST_HAVE, UsePeriod.ABOVE_THREE_YEAR, "다이소");
        createItem(user2, "노트북 스탠드", "에이치엔", "목 통증 감소", Satisfaction.VERY_GOOD, UsePeriod.ABOVE_ONE_YEAR, "아마존");
        createItem(user2, "무선 이어폰", "갤럭시버즈", "출퇴근 필수", Satisfaction.MUST_HAVE, UsePeriod.ABOVE_ONE_YEAR, "삼성");
    }

    private void removeExistingTestData() {
        userRepository.findByEmail(TEST_USER_1_EMAIL).ifPresent(u -> {
            itemJpaRepository.findByUserIdOrderByCreatedAtDesc(u.getId()).forEach(itemJpaRepository::delete);
            userRepository.delete(u);
        });
        userRepository.findByEmail(TEST_USER_2_EMAIL).ifPresent(u -> {
            itemJpaRepository.findByUserIdOrderByCreatedAtDesc(u.getId()).forEach(itemJpaRepository::delete);
            userRepository.delete(u);
        });
    }

    private User createUser(String email, String nickname, Long kakaoId) {
        return User.builder()
                .email(email)
                .nickname(nickname)
                .kakaoId(kakaoId)
                .gender(Gender.MALE)
                .authProvider(AuthProvider.KAKAO)
                .ageGroup(AgeGroup.AGE_20)
                .build();
    }

    private void createItem(User user, String title, String brand, String review, Satisfaction satisfaction, UsePeriod usePeriod, String purchase) {
        Item item = Item.builder()
                .title(title)
                .brand(brand)
                .review(review)
                .satisfaction(satisfaction)
                .usePeriod(usePeriod)
                .purchase(purchase)
                .user(user)
                .build();
        itemJpaRepository.save(item);
    }
}
