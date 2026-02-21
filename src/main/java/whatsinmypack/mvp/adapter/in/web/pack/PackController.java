package whatsinmypack.mvp.adapter.in.web.pack;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import whatsinmypack.mvp.adapter.in.web.pack.req.CreatePackRequest;
import whatsinmypack.mvp.adapter.in.web.pack.req.UpdatePackRequest;
import whatsinmypack.mvp.adapter.in.web.pack.res.PackDetailResponse;
import whatsinmypack.mvp.adapter.in.web.pack.res.PackSummaryResponse;
import whatsinmypack.mvp.adapter.in.web.pack.res.SliceResponse;
import whatsinmypack.mvp.application.pack.create.CreatePackUseCase;
import whatsinmypack.mvp.application.pack.getlist.GetUserPacksUseCase;
import whatsinmypack.mvp.application.pack.getlist.SearchPacksUseCase;
import whatsinmypack.mvp.application.pack.update.UpdatePackUseCase;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.global.security.user.UserDetailsImpl;

@Tag(name = "Pack", description = "팩 관련 컨트롤러")
@RestController
@RequestMapping("/api/v1/packs")
@RequiredArgsConstructor
public class PackController {

    private final CreatePackUseCase createPackUseCase;
    private final SearchPacksUseCase searchPacksUseCase;
    private final GetUserPacksUseCase getUserPacksUseCase;
    private final UpdatePackUseCase updatePackUseCase;

    @Operation(
            summary = "팩 생성",
            description = """
                    로그인한 유저가 기존에 등록한 아이템들을 선택하여 새로운 팩을 생성
                    - 아이템은 이미 존재(향후 예외처리 추가 예정)
                    - 컨텍스트 카테고리는 이름(name) 기준으로 조회
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
                    description = "인증 필요",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = whatsinmypack.mvp.presentation.response.ApiResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "컨텍스트 카테고리 또는 아이템을 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = whatsinmypack.mvp.presentation.response.ApiResponse.class)
                    )
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

    @Operation(summary = "내 팩 전체 조회", description = "로그인한 유저의 작성 팩 목록을 최신순으로 조회")
    @GetMapping
    public List<PackSummaryResponse> getMyPackList(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return getUserPacksUseCase.findUserPacks(userDetails.getUser())
                .stream()
                .map(e -> PackSummaryResponse.from(e, userDetails.getUser().getNickname()))
                .toList();
    }


    @Operation(
            summary = "팩 검색",
            description = """
                키워드를 기반으로 팩을 검색
                
                검색 대상
                - 팩 제목
                - 팩 설명
                - 아이템 제목
                - 아이템 브랜드
                - 아이템 구매처
                
                컨텍스트 카테고리
                - contexts 파라미터가 없으면 전체 팩 대상 검색
                - 여러 개 전달 시 OR 조건으로 검색
                
                페이징
                - 무한 스크롤 방식
                - wishlist 개수 기준 내림차순 정렬
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "검색 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = SliceResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 파라미터",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = whatsinmypack.mvp.presentation.response.ApiResponse.class
                            )
                    )
            )
    })
    @GetMapping("/search")
    public SliceResponse<PackDetailResponse> searchPacks(
            @RequestParam String q,
            @RequestParam(required = false) List<String> contexts,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<Pack> slice = searchPacksUseCase.search(q, contexts, pageable);

        return SliceResponse.from(slice.map(PackDetailResponse::from));
    }

    @Operation(
            summary = "팩 업데이트",
            description = """
                로그인한 유저가 자신의 팩을 수정
                
                수정 가능 항목:
                - 팩 소개(introduction)
                - 아이템 추가(addItems)
                - 아이템 삭제(removeItems)
                
                요청 시 addItems와 removeItems는 동시에 전달 가능하며,
                존재하지 않는 아이템 ID는 무시
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "팩 업데이트 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PackDetailResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 필요",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = whatsinmypack.mvp.presentation.response.ApiResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "본인 팩이 아님",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = whatsinmypack.mvp.presentation.response.ApiResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "팩 또는 아이템을 찾을 수 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = whatsinmypack.mvp.presentation.response.ApiResponse.class)
                    )
            )
    })
    @PatchMapping("/{packId}")
    public PackDetailResponse updatePack(
            @PathVariable Long packId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody UpdatePackRequest request
    ) {
        Pack pack = updatePackUseCase.update(packId, userDetails.getUser(), request);
        return PackDetailResponse.from(pack);
    }
}
