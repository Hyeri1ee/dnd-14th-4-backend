package whatsinmypack.mvp.adapter.in.web.pack.res;

import java.util.Objects;
import whatsinmypack.mvp.domain.pack.entity.Pack;

public record PackRecommendationResponse(
        Long id,
        String title,
        String contextCategory,
        String nickname,
        Integer items,
        String imageUrl // 대표 이미지
) {
    public static PackRecommendationResponse from(Pack pack) {
        return new PackRecommendationResponse(
                pack.getId(),
                pack.getTitle(),
                pack.getContextCategory().getName(),
                pack.getUser().getNickname(),
                pack.getPackItems().size(),
                Objects.requireNonNull(Objects.requireNonNull(pack.getPackItems().stream()
                                .findFirst()
                                .orElse(null))
                        .getItem().getImages().stream()
                        .findFirst().orElse(null)).getPath()
        );
    }
}
