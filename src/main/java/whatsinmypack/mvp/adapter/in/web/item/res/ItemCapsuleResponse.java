package whatsinmypack.mvp.adapter.in.web.item.res;

import java.util.List;

public record ItemCapsuleResponse(
        String title,
        String brand,
        String purchase,
        String userPeriod,
        String satisfaction,
        List<String> imgList,
        List<String> tagList,
        Boolean isWishlist
) {
}
