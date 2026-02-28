package whatsinmypack.mvp.adapter.in.web.pack.res;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import whatsinmypack.mvp.adapter.in.web.item.res.ItemCapsuleResponse;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.relation.entity.PackItem;

@Schema(description = "팩 상세 조회 응답 DTO")
public record PackDetailResponse(

        @Schema(
                description = "팩 ID",
                example = "12"
        )
        Long id,

        @Schema(
                description = "팩 작성자 닉네임",
                example = "닉네임1"
        )
        String user,

        @Schema(
                description = "팩 제목",
                example = "여행 갈 때 꼭 챙기는 아이템"
        )
        String title,

        @Schema(
                description = "팩 작성 날짜",
                example = "2024-10-12"
        )
        LocalDate date,

        @Schema(
                description = "작성자 프로필 이미지 URL",
                example = "https://cdn.whatsinmypack.com/profile/default.png"
        )
        String profileImage,

        @Schema(
                description = "팩 소개",
                example = "여행 다닐 때 항상 들고 다니는 필수템 모음"
        )
        String introduction,

        @Schema(
                description = "팩의 컨텍스트 카테고리",
                example = "여행/문화"
        )
        String contextCategory,

        @Schema(
                description = "팩을 구성하는 아이템 목록"
        )
        List<ItemCapsuleResponse> itemList,
        @Schema(
                description = "현재 로그인 사용자의 팩 위시리스트 포함 여부",
                example = "true"
        )
        boolean isPackInWishList
) {
    private static final String[] DEFAULT_PROFILE_COLORS = {"blue", "green", "yellow", "purple", "pink"};

    public static PackDetailResponse from(Pack pack) {
        return from(pack, false, Set.of());
    }

    public static PackDetailResponse from(Pack pack, boolean isPackInWishList, Set<Long> wishlistedItemIds) {
        return new PackDetailResponse(
                pack.getId(),
                pack.getUser().getNickname(),
                pack.getTitle(),
                pack.getCreatedAt().toLocalDate(),
                resolveProfileImage(pack.getUser().getProfileImage()),
                pack.getIntroduction(),
                pack.getContextCategory().getName(),
                pack.getPackItems().stream()
                        .map(PackItem::getItem)
                        .map(item -> ItemCapsuleResponse.from(item, wishlistedItemIds))
                        .toList(),
                isPackInWishList
        );
    }

    private static String resolveProfileImage(String profileImage) {
        if (profileImage != null && !profileImage.isBlank()) {
            return profileImage;
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(DEFAULT_PROFILE_COLORS.length);
        return DEFAULT_PROFILE_COLORS[randomIndex];
    }
}
