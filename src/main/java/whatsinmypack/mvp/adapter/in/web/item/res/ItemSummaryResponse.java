package whatsinmypack.mvp.adapter.in.web.item.res;

import io.swagger.v3.oas.annotations.media.Schema;
import whatsinmypack.mvp.domain.item.entity.Item;

@Schema(description = "아이템 요약 (목록용)")
public record ItemSummaryResponse(
        @Schema(description = "아이템 ID")
        Long id,
        @Schema(description = "브랜드명")
        String brandName,
        @Schema(description = "제품명")
        String productName,
        @Schema(description = "만족도")
        String satisfaction,
        @Schema(description = "리뷰")
        String review,
        @Schema(description = "사용 기간")
        String usePeriod,
        @Schema(description = "구매처")
        String purchaseLocation
) {
    public static ItemSummaryResponse from(Item item) {
        return new ItemSummaryResponse(
                item.getId(),
                item.getBrand(),
                item.getTitle(),
                item.getSatisfaction() != null ? item.getSatisfaction().name() : null,
                item.getReview(),
                item.getUsePeriod() != null ? item.getUsePeriod().name() : null,
                item.getPurchase()
        );
    }
}
