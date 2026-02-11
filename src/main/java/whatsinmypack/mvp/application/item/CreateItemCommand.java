package whatsinmypack.mvp.application.item;

import whatsinmypack.mvp.domain.item.entity.Satisfaction;
import whatsinmypack.mvp.domain.item.entity.UsePeriod;

import java.util.List;

/**
 * 아이템 생성 Use Case 입력
 */
public record CreateItemCommand(
        Long userId,
        String brandName,
        String productName,
        Satisfaction satisfaction,
        String reviewText,
        List<String> reviewImagePaths,
        List<String> tags,
        UsePeriod usePeriod,
        String purchaseLocation
) {
    public CreateItemCommand {
        reviewImagePaths = reviewImagePaths != null ? List.copyOf(reviewImagePaths) : List.of();
        tags = tags != null ? List.copyOf(tags) : List.of();
    }
}
