package whatsinmypack.mvp.adapter.in.web.item.res;

import io.swagger.v3.oas.annotations.media.Schema;
import whatsinmypack.mvp.domain.item.entity.Item;

@Schema(description = "아이템 수정 응답")
public record UpdateItemResponse(
        @Schema(description = "수정된 아이템 ID")
        Long id,
        @Schema(description = "브랜드명")
        String brandName,
        @Schema(description = "제품명")
        String productName,
        @Schema(description = "만족도")
        String satisfaction
) {
    public static UpdateItemResponse from(Item item) {
        return new UpdateItemResponse(
                item.getId(),
                item.getBrand(),
                item.getTitle(),
                item.getSatisfaction() != null ? item.getSatisfaction().name() : null
        );
    }
}
