package whatsinmypack.mvp.adapter.in.web.item;

import io.swagger.v3.oas.annotations.media.Schema;
import whatsinmypack.mvp.domain.item.entity.Item;

@Schema(description = "아이템 생성 응답")
public record CreateItemResponse(
        @Schema(description = "생성된 아이템 ID")
        Long id,
        @Schema(description = "브랜드명")
        String brandName,
        @Schema(description = "제품명")
        String productName,
        @Schema(description = "만족도")
        String satisfaction
) {
    public static CreateItemResponse from(Item item) {
        return new CreateItemResponse(
                item.getId(),
                item.getBrand(),
                item.getTitle(),
                item.getSatisfaction() != null ? item.getSatisfaction().name() : null
        );
    }
}
