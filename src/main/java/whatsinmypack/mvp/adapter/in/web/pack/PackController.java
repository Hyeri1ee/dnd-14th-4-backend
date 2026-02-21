package whatsinmypack.mvp.adapter.in.web.pack;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import whatsinmypack.mvp.adapter.in.web.pack.req.CreatePackRequest;
import whatsinmypack.mvp.adapter.in.web.pack.res.PackDetailResponse;
import whatsinmypack.mvp.application.pack.create.CreatePackUseCase;
import whatsinmypack.mvp.global.security.user.UserDetailsImpl;

@Tag(name = "Pack", description = "팩 관련 컨트롤러")
@RestController
@RequestMapping("/api/v1/packs")
@RequiredArgsConstructor
public class PackController {

    private final CreatePackUseCase createPackUseCase;

    @Operation(
            summary = "팩 생성",
            description = """
                    로그인한 유저가 기존에 등록한 아이템들을 선택하여 새로운 팩을 생성합니다.
                    - 아이템은 이미 존재해야 합니다(향후 예외처리 추가 예정).
                    - 컨텍스트 카테고리는 이름(name) 기준으로 조회됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "팩 생성 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PackDetailResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 필요"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "컨텍스트 카테고리 또는 아이템을 찾을 수 없음"
            )
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public PackDetailResponse createPack(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(
                    description = "아이템 생성 요청 (JSON)",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreatePackRequest.class)))
            CreatePackRequest request
    ) {
        return PackDetailResponse.from(createPackUseCase.create(userDetails.getUser(), request));
    }
}
