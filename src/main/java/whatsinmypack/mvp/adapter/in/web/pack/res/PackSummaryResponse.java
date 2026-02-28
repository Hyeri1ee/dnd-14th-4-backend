package whatsinmypack.mvp.adapter.in.web.pack.res;

import io.swagger.v3.oas.annotations.media.Schema;
import whatsinmypack.mvp.domain.item.entity.value.ItemImage;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.relation.entity.PackItem;

@Schema(description = "팩 요약 응답 DTO")
public record PackSummaryResponse(

        @Schema(
                description = "팩 ID",
                example = "3"
        )
        Long id,

        @Schema(
                description = "팩 제목",
                example = "출근 가방 필수템"
        )
        String title,

        @Schema(
                description = "컨텍스트 카테고리 이름",
                example = "여행/문화"
        )
        String contextCategory,

        @Schema(
                description = "팩 작성자 닉네임",
                example = "닉네임1"
        )
        String nickname,

        @Schema(
                description = "팩에 포함된 아이템 개수",
                example = "7"
        )
        Integer items,

        @Schema(
                description = "팩에 속한 아이템들 중 하나의 대표 이미지",
                example = "이미지 경로"
        )
        String path
) {
    public static PackSummaryResponse from(Pack pack, String nickname) {
        String imageUrl = pack.getPackItems().stream()
                .findFirst()
                .map(PackItem::getItem)
                .flatMap(item -> item.getImages().stream().findFirst())
                .map(ItemImage::getPath)
                .orElse("이미지 경로가 없습니다");

        return new PackSummaryResponse(
                pack.getId(),
                pack.getTitle(),
                pack.getContextCategory().getName(),
                nickname,
                pack.getPackItems().size(),
                imageUrl
        );
    }
}
