package whatsinmypack.mvp.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import whatsinmypack.mvp.application.UserProfileService;
import whatsinmypack.mvp.application.mypage.GetMyPageProfileUseCase;
import whatsinmypack.mvp.application.mypage.MyPageProfile;
import whatsinmypack.mvp.global.security.user.UserDetailsImpl;
import whatsinmypack.mvp.presentation.request.NicknameRequest;
import whatsinmypack.mvp.presentation.response.ApiResponse;
import whatsinmypack.mvp.presentation.response.MyPageProfileResponse;

@Tag(name = "User Profile", description = "사용자 회원정보 관련 요소 생명주기 제어 컨트롤러")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final GetMyPageProfileUseCase getMyPageProfileUseCase;

    @Operation(summary = "마이페이지 프로필 조회", description = "프로필 사진 URL + 관심 카테고리 이름 목록 (user_context_category, context_category join)")
    @GetMapping("/mypage")
    public ResponseEntity<MyPageProfileResponse> getMyPageProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        MyPageProfile profile = getMyPageProfileUseCase.getMyPageProfile(userDetails.getUserId());
        return ResponseEntity.ok(MyPageProfileResponse.from(profile));
    }

    @Operation(summary = "회원탈퇴", description = "서비스 회원탈퇴 및 카카오 연동해제 동시 성공")
    @PostMapping("/withdrawal")
    public ResponseEntity<Void> withdraw(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userProfileService.withdraw(userDetails.getUser());
        return ResponseEntity.noContent().build(); // 204는 바디가 없음
    }

    @Operation(summary = "사용자 닉네임 생성 및 수정", description = "사용자 닉네임 유효성 및 중복 검증 API")
    @PostMapping("/nickname")
    public ApiResponse updateUserNickname(
            @Valid @RequestBody NicknameRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        userProfileService.verifyAndUpdateNickname(request.getNickname(), userDetails.getUser());
        return new ApiResponse("닉네임이 생성됐습니다");
    }
}
