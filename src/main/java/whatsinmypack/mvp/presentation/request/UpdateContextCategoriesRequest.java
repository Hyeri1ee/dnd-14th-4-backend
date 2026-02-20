package whatsinmypack.mvp.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "개인화 정보 설정 - 관심 context_category 수정 요청. 최대 3개까지 선택 가능. 기존 선택은 삭제 후 새로 저장.")
public record UpdateContextCategoriesRequest(
        @Schema(description = "context_category ID 목록 (1~3개). 빈 배열이면 전체 해제", example = "[1, 2, 3]")
        @Size(max = 3, message = "관심 카테고리는 최대 3개까지 선택할 수 있습니다.")
        List<Long> contextCategoryIds
) {
}
