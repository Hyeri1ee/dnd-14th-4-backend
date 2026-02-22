package whatsinmypack.mvp.adapter.in.web.pack.res;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import whatsinmypack.mvp.domain.pack.entity.Pack;

@Schema(description = "팩 추천 응답 DTO")
public record PackRecommendationResponse(

        @Schema(
                description = "팩 ID",
                example = "12"
        )
        Long id,

        @Schema(
                description = "팩 제목",
                example = "여행 갈 때 꼭 필요한 팩"
        )
        String title,

        @Schema(
                description = "컨텍스트 카테고리 이름",
                example = "여행/문화"
        )
        String contextCategory,

        @Schema(
                description = "팩 작성자 닉네임",
                example = "닉네임1"
        )
        String nickname,

        @Schema(
                description = "팩에 포함된 아이템 개수",
                example = "5"
        )
        Integer items,

        @Schema(
                description = "대표 아이템 이미지 URL",
                example = "https://cdn.example.com/items/image1.jpg"
        )
        String imageUrl // 대표 이미지이미지
) {
    public static PackRecommendationResponse from(Pack pack) {
        return new PackRecommendationResponse(
                pack.getId(),
                pack.getTitle(),
                pack.getContextCategory().getName(),
                pack.getUser().getNickname(),
                pack.getPackItems().size(),
                Objects.requireNonNull(Objects.requireNonNull(pack.getPackItems().stream()
                                .findFirst()
                                .orElse(null))
                        .getItem().getImages().stream()
                        .findFirst().orElse(null)).getPath()
        );
    }
}
