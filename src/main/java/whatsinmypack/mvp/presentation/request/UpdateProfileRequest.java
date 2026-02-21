package whatsinmypack.mvp.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import whatsinmypack.mvp.domain.user.entity.AgeGroup;
import whatsinmypack.mvp.domain.user.entity.Gender;

@Schema(description = "프로필 설정 수정 요청 (닉네임, 연령대, 성별). 프로필 사진은 multipart profileImage 파트로 전송")
public record UpdateProfileRequest(
        @Schema(description = "닉네임 (10자 이내, 특수기호/띄어쓰기/영문 대문자 불가)", example = "닉네임4조", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "닉네임을 입력해주세요.")
        @Size(max = 10, message = "닉네임은 10자 이내로 입력해주세요.")
        @Pattern(regexp = "^[a-z0-9가-힣]+$", message = "특수기호, 띄어쓰기, 영문 대문자는 사용할 수 없습니다.")
        String nickname,

        @Schema(description = "연령대 (AGE_10, AGE_20, AGE_30, AGE_40, AGE_50, AGE_60)", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "연령대를 선택해주세요.")
        AgeGroup ageGroup,

        @Schema(description = "성별 (MALE, FEMALE)", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "성별을 선택해주세요.")
        Gender gender
) {
}
