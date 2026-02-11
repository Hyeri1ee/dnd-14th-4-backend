package whatsinmypack.mvp.adapter.in.web.item;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import whatsinmypack.mvp.domain.item.entity.Satisfaction;
import whatsinmypack.mvp.domain.item.entity.UsePeriod;

import java.util.List;

@Schema(description = "아이템 생성 요청 (인생 아이템 추가)")
public record CreateItemRequest(
        @Schema(description = "브랜드명", example = "스테들러", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "브랜드명을 입력해주세요")
        @Size(max = 25)
        String brandName,

        @Schema(description = "제품명", example = "원통형 2홀 연필깎이", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "제품명을 입력해주세요")
        @Size(max = 25)
        String productName,

        @Schema(description = "만족도 (GOOD: 좋아요, VERY_GOOD: 매우 좋아요, MUST_HAVE: 인생템)", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "만족도를 선택해주세요")
        Satisfaction satisfaction,

        @Schema(description = "리뷰 내용 (선택)", example = "오래 써도 날이 잘 유지돼요")
        @Size(max = 100)
        String reviewText,

        @Schema(description = "리뷰 이미지 URL 또는 경로 (선택, 최대 5개)")
        @Size(max = 5)
        List<String> reviewImagePaths,

        @Schema(description = "태그 (선택, 최대 5개, 태그당 최대 10자)")
        @Size(max = 5)
        List<String> tags,

        @Schema(description = "사용 기간 (선택)")
        UsePeriod usePeriod,

        @Schema(description = "구매처 (선택)", example = "네이버 스마트스토어")
        @Size(max = 25)
        String purchaseLocation
) {
    public CreateItemRequest {
        reviewImagePaths = reviewImagePaths != null ? reviewImagePaths : List.of();
        tags = tags != null ? tags : List.of();
    }
}
