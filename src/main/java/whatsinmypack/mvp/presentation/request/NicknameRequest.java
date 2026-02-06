package whatsinmypack.mvp.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NicknameRequest {

    @NotBlank(message = "닉네임은 필수 입력값입니다")
    @Length(max = 10, message = "닉네임은 10자 이하로 입력해주세요")
    @Pattern(
            regexp = "^[a-zA-Z0-9가-힣]*$",
            message = "한글, 영문자, 숫자만 사용할 수 있습니다"
    )
    private String nickname;
}
