package whatsinmypack.mvp.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import whatsinmypack.mvp.application.UserProfileService;
import whatsinmypack.mvp.application.mypage.GetMyPageProfileUseCase;
import whatsinmypack.mvp.application.mypage.MyPageProfile;
import whatsinmypack.mvp.application.profile.UpdateProfileCommand;
import whatsinmypack.mvp.application.profile.UpdateProfileUseCase;
import whatsinmypack.mvp.domain.user.entity.User;
import whatsinmypack.mvp.global.security.user.UserDetailsImpl;
import whatsinmypack.mvp.presentation.request.NicknameRequest;
import whatsinmypack.mvp.presentation.request.UpdateProfileRequest;
import whatsinmypack.mvp.presentation.response.ApiResponse;
import whatsinmypack.mvp.presentation.response.MyPageProfileResponse;
import whatsinmypack.mvp.presentation.response.UpdateProfileResponse;

@Tag(name = "User Profile", description = "사용자 회원정보 관련 요소 생명주기 제어 컨트롤러")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final GetMyPageProfileUseCase getMyPageProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;

    @Operation(summary = "마이페이지 > 프로필 조회", description = "로그인한 유저의 프로필 사진 URL + 관심 카테고리 이름 목록 (user_context_category, context_category join)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = MyPageProfileResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @GetMapping("/mypage")
    public ResponseEntity<MyPageProfileResponse> getMyPageProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        MyPageProfile profile = getMyPageProfileUseCase.getMyPageProfile(userDetails.getUserId());
        return ResponseEntity.ok(MyPageProfileResponse.from(profile));
    }

    @Operation(summary = "마이페이지 > 프로필 설정 수정", description = "닉네임, 연령대, 성별, 프로필 사진 수정. multipart: request(JSON) + profileImage(선택). 프로필 사진은 S3 upload/profile/{userId}/{timestamp}.ext 에 저장, 조회 시 가장 최근 URL 사용")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = UpdateProfileResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요")
    })

    @PatchMapping(value = "/profile/basic", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UpdateProfileResponse> updateProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "프로필 수정 요청 (JSON)", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UpdateProfileRequest.class)))
            @RequestPart("request") @Valid UpdateProfileRequest request,
            @Parameter(description = "프로필 사진 (선택)")
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        User user = updateProfileUseCase.updateProfile(new UpdateProfileCommand(
                userDetails.getUserId(),
                request.nickname(),
                request.gender(),
                request.ageGroup(),
                profileImage
        ));
        return ResponseEntity.ok(UpdateProfileResponse.from(user));
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
