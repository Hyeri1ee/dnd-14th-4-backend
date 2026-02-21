package whatsinmypack.mvp.adapter.in.web.item.res;

import java.util.List;
import whatsinmypack.mvp.domain.item.entity.Item;
import whatsinmypack.mvp.domain.item.entity.value.ItemImage;
import whatsinmypack.mvp.domain.item.entity.value.ItemTag;

public record ItemCapsuleResponse(
        Long itemId,
        String title,
        String brand,
        String review,
        String satisfaction,
        String usePeriod,
        String purchase,
        List<String> imageUrls,
        List<String> tags
) {

    public static ItemCapsuleResponse from(Item item) {
        return new ItemCapsuleResponse(
                item.getId(),
                item.getTitle(),
                item.getBrand(),
                item.getReview(),
                item.getSatisfaction().kor(),
                item.getUsePeriod().kor(),
                item.getPurchase(),
                item.getImages().stream()
                        .map(ItemImage::getPath)
                        .toList(),
                item.getTags().stream()
                        .map(ItemTag::getTag)
                        .toList()
        );
    }
}