package whatsinmypack.mvp.application.item.create;

import whatsinmypack.mvp.domain.item.entity.Satisfaction;
import whatsinmypack.mvp.domain.item.entity.UsePeriod;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record CreateItemCommand(
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
    public CreateItemCommand {
        reviewImages = reviewImages != null ? List.copyOf(reviewImages) : List.of();
        tags = tags != null ? List.copyOf(tags) : List.of();
    }
}
