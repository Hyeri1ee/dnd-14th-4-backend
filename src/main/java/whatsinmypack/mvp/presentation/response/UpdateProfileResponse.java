package whatsinmypack.mvp.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import whatsinmypack.mvp.domain.user.entity.User;

@Schema(description = "프로필 수정 응답")
public record UpdateProfileResponse(
        @Schema(description = "닉네임")
        String nickname,
        @Schema(description = "연령대")
        String ageGroup,
        @Schema(description = "성별")
        String gender,
        @Schema(description = "프로필 사진 URL (가장 최근 업로드)")
        String profileImageUrl
) {
    public static UpdateProfileResponse from(User user) {
        return new UpdateProfileResponse(
                user.getNickname(),
                user.getAgeGroup() != null ? user.getAgeGroup().name() : null,
                user.getGender() != null ? user.getGender().name() : null,
                user.getProfileImage()
        );
    }
}
