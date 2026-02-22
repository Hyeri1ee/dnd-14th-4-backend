package whatsinmypack.mvp.adapter.in.web.item.res;

import io.swagger.v3.oas.annotations.media.Schema;
import whatsinmypack.mvp.domain.item.entity.Item;
import whatsinmypack.mvp.domain.item.entity.value.ItemImage;
import whatsinmypack.mvp.domain.item.entity.value.ItemTag;

import java.util.List;

@Schema(description = "아이템 정보 응답")
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
        @Schema(description = "리뷰 이미지 URL 목록")
        List<String> reviewImagePaths,
        @Schema(description = "태그 목록")
        List<String> tags,
        @Schema(description = "사용 기간")
        String usePeriod,
        @Schema(description = "구매처")
        String purchaseLocation
) {
    public static ItemSummaryResponse from(Item item) {
        List<String> imagePaths = item.getImages().stream()
                .map(ItemImage::getPath)
                .toList();
        List<String> tags = item.getTags().stream()
                .map(ItemTag::getTag)
                .toList();
        return new ItemSummaryResponse(
                item.getId(),
                item.getBrand(),
                item.getTitle(),
                item.getSatisfaction() != null ? item.getSatisfaction().name() : null,
                item.getReview(),
                imagePaths,
                tags,
                item.getUsePeriod() != null ? item.getUsePeriod().name() : null,
                item.getPurchase()
        );
    }
}
