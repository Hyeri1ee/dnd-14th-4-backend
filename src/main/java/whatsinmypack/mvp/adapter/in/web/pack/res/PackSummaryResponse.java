package whatsinmypack.mvp.adapter.in.web.pack.res;

import whatsinmypack.mvp.domain.pack.entity.Pack;

public record PackSummaryResponse(
        Long id,
        String title,
        String contextCategory,
        String nickname,
        Integer items
) {
    public static PackSummaryResponse from(Pack pack, String nickname) {
        return new PackSummaryResponse(
                pack.getId(),
                pack.getTitle(),
                pack.getContextCategory().getName(),
                nickname,
                pack.getPackItems().size()
        );
    }
}
