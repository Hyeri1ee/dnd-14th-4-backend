package whatsinmypack.mvp.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import whatsinmypack.mvp.application.mypage.MyPageProfile;

import java.util.List;

@Schema(description = "마이페이지 프로필 응답 (닉네임 + 프로필 사진 URL 또는 기본 색상 + 관심 카테고리 이름 목록)")
public record MyPageProfileResponse(
        @Schema(description = "사용자 닉네임", example = "whatsinmypack")
        String name,
        @Schema(description = "프로필 사진 URL. DB(profile_image)가 null/빈값이면 userId 해시 기반 기본 색상 문자열 반환: yellow, red, blue, green, purple 중 하나",
                example = "https://whatsinmypack.s3.ap-northeast-2.amazonaws.com/upload/profile/1/1234567890.jpg")
        String profileImageUrl,
        @Schema(description = "관심 카테고리 이름 목록 (user_context_category, context_category join)", example = "[\"공부/시험\", \"면접/취준\", \"여행/캠핑\"]")
        List<String> contextCategoryNames
) {
    public static MyPageProfileResponse from(MyPageProfile profile) {
        return new MyPageProfileResponse(
                profile.name(),
                profile.profileImageUrl(),
                profile.contextCategoryNames()
        );
    }
}
