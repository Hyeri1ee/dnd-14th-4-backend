package whatsinmypack.mvp.application.item.update;

import whatsinmypack.mvp.domain.item.entity.Satisfaction;
import whatsinmypack.mvp.domain.item.entity.UsePeriod;

import java.util.List;

public record UpdateItemCommand(
        Long itemId,
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
}
