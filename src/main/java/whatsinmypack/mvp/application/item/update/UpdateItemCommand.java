package whatsinmypack.mvp.application.item.update;

import whatsinmypack.mvp.domain.item.entity.Satisfaction;
import whatsinmypack.mvp.domain.item.entity.UsePeriod;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record UpdateItemCommand(
        Long itemId,
        Long userId,
        String brandName,
        String productName,
        Satisfaction satisfaction,
        String reviewText,
        List<MultipartFile> reviewImages,
        List<String> tags,
        UsePeriod usePeriod,
        String purchaseLocation
) {
    public UpdateItemCommand {
        reviewImages = reviewImages != null ? List.copyOf(reviewImages) : List.of();
    }
}
