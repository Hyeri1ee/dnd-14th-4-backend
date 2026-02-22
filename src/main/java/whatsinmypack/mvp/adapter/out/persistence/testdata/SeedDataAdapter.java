package whatsinmypack.mvp.adapter.out.persistence.testdata;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.adapter.out.persistence.contextcategory.ContextCategoryJpaRepository;
import whatsinmypack.mvp.application.testdata.port.SeedDataPort;
import whatsinmypack.mvp.domain.item.entity.Item;
import whatsinmypack.mvp.domain.item.entity.Satisfaction;
import whatsinmypack.mvp.domain.item.entity.UsePeriod;
import whatsinmypack.mvp.domain.contextCategory.entity.ContextCategory;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.relation.entity.PackItem;
import whatsinmypack.mvp.domain.user.entity.AgeGroup;
import whatsinmypack.mvp.domain.user.entity.AuthProvider;
import whatsinmypack.mvp.domain.user.entity.Gender;
import whatsinmypack.mvp.domain.user.entity.User;
import whatsinmypack.mvp.domain.user.repository.UserRepository;
import whatsinmypack.mvp.adapter.out.persistence.item.ItemJpaRepository;
import whatsinmypack.mvp.adapter.out.persistence.pack.PackJpaRepository;
import whatsinmypack.mvp.adapter.out.persistence.relation.ItemWishListJpaRepository;
import whatsinmypack.mvp.adapter.out.persistence.relation.PackWishListJpaRepository;
import whatsinmypack.mvp.adapter.out.persistence.relation.UserContextCategoryJpaRepository;

@Component
@RequiredArgsConstructor
public class SeedDataAdapter implements SeedDataPort {

    private static final String TEST_USER_1_EMAIL = "user_1@test.com";
    private static final String TEST_USER_2_EMAIL = "user_2@test.com";
    private static final int ITEMS_PER_USER = 20;
    private static final int PACKS_PER_USER = 10;
    private static final Random RANDOM = new Random();

    private static final String[] BRAND_POOL = {
            "무인양품", "다이소", "애플", "삼성", "이케아", "나이키", "스테들러", "제트스트림", "올리브영", "아트박스"
    };
    private static final String[] ITEM_NAME_POOL = {
            "텀블러", "파우치", "멀티탭", "노트", "무선이어폰", "노트북스탠드", "휴대용선풍기", "보조배터리", "연필깎이", "펜케이스",
            "손소독제", "카메라", "메모패드", "우산", "칫솔세트", "핸드크림", "에코백", "마우스", "키보드", "타이머"
    };
    private static final String[] REVIEW_POOL = {
            "가성비가 좋아요", "매일 들고 다니는 필수템", "내구성이 괜찮아요", "휴대성이 좋아서 만족", "디자인이 깔끔해요",
            "생각보다 더 편해요", "선물용으로도 좋아요", "재구매 의사 있어요", "실사용 만족도가 높아요", "사용감이 부드러워요"
    };
    private static final String[] PURCHASE_POOL = {
            "쿠팡", "네이버", "올리브영", "다이소", "무신사", "아마존", "이마트", "홈플러스", "공식몰", "편의점"
    };
    private static final String[] PACK_TITLE_POOL = {
            "출근 필수 팩", "여행 준비 팩", "운동 가방 팩", "시험 기간 팩", "주말 외출 팩",
            "재택근무 팩", "데이트 준비 팩", "캠핑 스타터 팩", "촬영 장비 팩", "헬스장 루틴 팩"
    };
    private static final String[] PACK_INTRO_POOL = {
            "자주 쓰는 아이템을 모아둔 팩이에요.",
            "상황별로 챙기기 좋은 구성입니다.",
            "실사용 위주로 고른 조합이에요.",
            "이 조합이면 실패가 적어요.",
            "필수템만 추려서 구성했어요."
    };
    private static final String[] DEFAULT_CONTEXT_NAMES = {
            "공부/시험", "면접/취준", "업무/출근", "약속/데이트",
            "운동/건강", "여행/문화", "취미/작업", "육아/반려동물"
    };

    private final UserRepository userRepository;
    private final ItemJpaRepository itemJpaRepository;
    private final PackJpaRepository packJpaRepository;
    private final ContextCategoryJpaRepository contextCategoryJpaRepository;
    private final ItemWishListJpaRepository itemWishListJpaRepository;
    private final PackWishListJpaRepository packWishListJpaRepository;
    private final UserContextCategoryJpaRepository userContextCategoryJpaRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void deleteSeed() {
        removeExistingTestData();
    }

    @Override
    @Transactional
    public void seed() {
        removeExistingTestData();
        entityManager.flush();
        entityManager.clear();
        User user1 = createUser(TEST_USER_1_EMAIL, "테스트유저1", 1001L);
        User user2 = createUser(TEST_USER_2_EMAIL, "테스트유저2", 1002L);
        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        List<ContextCategory> categories = ensureContextCategories();

        List<Item> user1Items = createItems(user1, ITEMS_PER_USER);
        List<Item> user2Items = createItems(user2, ITEMS_PER_USER);
        createPacks(user1, user1Items, categories, PACKS_PER_USER);
        createPacks(user2, user2Items, categories, PACKS_PER_USER);
    }

    private void removeExistingTestData() {
        userRepository.findByEmail(TEST_USER_1_EMAIL).ifPresent(u -> {
            Long userId = u.getId();
            itemWishListJpaRepository.deleteByUser_Id(userId);
            packWishListJpaRepository.deleteByUser_Id(userId);
            userContextCategoryJpaRepository.deleteByUser_Id(userId);
            packJpaRepository.findByUser(u).forEach(packJpaRepository::delete);
            itemJpaRepository.findByUserIdOrderByCreatedAtDesc(userId).forEach(itemJpaRepository::delete);
            userRepository.delete(u);
        });
        userRepository.findByEmail(TEST_USER_2_EMAIL).ifPresent(u -> {
            Long userId = u.getId();
            itemWishListJpaRepository.deleteByUser_Id(userId);
            packWishListJpaRepository.deleteByUser_Id(userId);
            userContextCategoryJpaRepository.deleteByUser_Id(userId);
            packJpaRepository.findByUser(u).forEach(packJpaRepository::delete);
            itemJpaRepository.findByUserIdOrderByCreatedAtDesc(userId).forEach(itemJpaRepository::delete);
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

    private List<Item> createItems(User user, int count) {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String title = ITEM_NAME_POOL[RANDOM.nextInt(ITEM_NAME_POOL.length)] + " " + (i + 1);
            String brand = BRAND_POOL[RANDOM.nextInt(BRAND_POOL.length)];
            String review = REVIEW_POOL[RANDOM.nextInt(REVIEW_POOL.length)];
            Satisfaction satisfaction = Satisfaction.values()[RANDOM.nextInt(Satisfaction.values().length)];
            UsePeriod usePeriod = UsePeriod.values()[RANDOM.nextInt(UsePeriod.values().length)];
            String purchase = PURCHASE_POOL[RANDOM.nextInt(PURCHASE_POOL.length)];
            items.add(createItem(user, title, brand, review, satisfaction, usePeriod, purchase));
        }
        return items;
    }

    private Item createItem(User user, String title, String brand, String review, Satisfaction satisfaction, UsePeriod usePeriod, String purchase) {
        Item item = Item.builder()
                .title(title)
                .brand(brand)
                .review(review)
                .satisfaction(satisfaction)
                .usePeriod(usePeriod)
                .purchase(purchase)
                .user(user)
                .build();
        return itemJpaRepository.save(item);
    }

    private void createPacks(User user, List<Item> items, List<ContextCategory> categories, int count) {
        for (int i = 0; i < count; i++) {
            ContextCategory category = categories.get(RANDOM.nextInt(categories.size()));
            int pickCount = 2 + RANDOM.nextInt(4); // 2~5개 아이템
            List<Item> pickedItems = pickRandomItems(items, pickCount);

            Pack pack = Pack.builder()
                    .title(PACK_TITLE_POOL[RANDOM.nextInt(PACK_TITLE_POOL.length)])
                    .introduction(PACK_INTRO_POOL[RANDOM.nextInt(PACK_INTRO_POOL.length)])
                    .user(user)
                    .contextCategory(category)
                    .build();

            List<PackItem> packItems = pickedItems.stream()
                    .map(item -> new PackItem(pack, item))
                    .toList();
            pack.getPackItems().addAll(packItems);
            packJpaRepository.save(pack);
        }
    }

    private List<Item> pickRandomItems(List<Item> items, int count) {
        List<Item> copied = new ArrayList<>(items);
        java.util.Collections.shuffle(copied, RANDOM);
        return copied.subList(0, Math.min(count, copied.size()));
    }

    private List<ContextCategory> ensureContextCategories() {
        List<ContextCategory> categories = contextCategoryJpaRepository.findAllByOrderByIdAsc();
        if (!categories.isEmpty()) {
            return categories;
        }

        List<ContextCategory> defaults = java.util.Arrays.stream(DEFAULT_CONTEXT_NAMES)
                .map(name -> ContextCategory.builder()
                        .name(name)
                        .detail(name + " 관련 상황")
                        .build())
                .toList();
        contextCategoryJpaRepository.saveAll(defaults);
        return contextCategoryJpaRepository.findAllByOrderByIdAsc();
    }
}
