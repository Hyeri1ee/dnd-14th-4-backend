package whatsinmypack.mvp.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import whatsinmypack.mvp.application.mypage.MyPageProfile;

import java.util.List;

@Schema(description = "마이페이지 프로필 응답 (프로필 사진 URL + 관심 카테고리 이름 목록)")
public record MyPageProfileResponse(
        @Schema(description = "프로필 사진 URL")
        String profileImageUrl,
        @Schema(description = "관심 카테고리 이름 목록 (user_context_category + context_category join)")
        List<String> contextCategoryNames
) {
    public static MyPageProfileResponse from(MyPageProfile profile) {
        return new MyPageProfileResponse(
                profile.profileImageUrl(),
                profile.contextCategoryNames()
        );
    }
}
