package whatsinmypack.mvp.adapter.in.web.pack.res;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "팩 검색 무한 스크롤 응답")
public record SlicePackResponse(

        @Schema(
                description = "팩 목록"
        )
        List<PackDetailResponse> items,

        @Schema(
                description = "다음 페이지 존재 여부",
                example = "true"
        )
        boolean hasNext
) {
    public static SlicePackResponse from(SliceResponse<PackDetailResponse> slice) {
        return new SlicePackResponse(
                slice.items(),
                slice.hasNext()
        );
    }
}
