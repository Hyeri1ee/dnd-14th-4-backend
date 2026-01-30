package whatsinmypack.mvp.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import whatsinmypack.mvp.application.UserOnboardingService;
import whatsinmypack.mvp.presentation.request.NicknameRequest;
import whatsinmypack.mvp.presentation.response.ApiResponse;

@Tag(name = "Onboarding", description = "사용자 온보딩 과정의 요청, 응답 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserOnboardingController {

    private final UserOnboardingService userOnboardingService;

    @PostMapping("/nickname")
    @Operation(summary = "사용자 닉네임 생성", description = "사용자 닉네임 유효성 및 중복 검증 API")
    public ApiResponse createUserNickname(
            @RequestBody NicknameRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        userOnboardingService.verifyAndUpdateNickname(request.getNickname(), userDetails.getUsername());
        return new ApiResponse("닉네임이 생성됐습니다");
    }
}
