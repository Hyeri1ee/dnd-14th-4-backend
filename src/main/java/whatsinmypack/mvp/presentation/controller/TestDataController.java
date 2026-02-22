package whatsinmypack.mvp.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import whatsinmypack.mvp.application.testdata.SeedDataUseCase;

@Tag(
        name = "TestData",
        description = "테스트용 시드 데이터 API. "
                + "**참고 (시드 생성):** 시드 삭제 API를 먼저 호출하지 않아도, 이미 시드가 있어도 생성 API만 다시 호출하면 기존 테스트 데이터가 제거된 뒤 새 데이터로 덮어쓰기 됩니다."
)
@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestDataController {

    private final SeedDataUseCase seedDataUseCase;

    @Operation(
            summary = "시드 데이터 생성",
            description = """
                    테스트용 데이터를 초기화한 뒤 새로 생성합니다.
                    
                    생성 데이터
                    - 테스트 유저 2명
                      - user_1@test.com (닉네임: 테스트유저1)
                      - user_2@test.com (닉네임: 테스트유저2)
                    - 유저별 아이템 20개 (브랜드/제품명/리뷰/만족도/사용기간/구매처 랜덤)
                    - 유저별 팩 10개 (컨텍스트 카테고리 랜덤)
                    - 각 팩은 해당 유저 아이템 중 랜덤 2~5개로 구성
                    
                    동작 방식
                    - 기존 동일 테스트 유저가 있으면 관련 데이터 정리 후 재생성
                    - 삭제 API를 먼저 호출하지 않아도 생성 API만 재호출하면 덮어쓰기
                    
                    인증
                    - Authorization: Bearer user_1 또는 Bearer user_2
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/seed")
    public ResponseEntity<Void> seed() {
        seedDataUseCase.seed();
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "시드 데이터 삭제",
            description = """
                    테스트용 유저 2명(user_1@test.com, user_2@test.com)과 연관 데이터를 삭제합니다.
                    
                    삭제 대상
                    - 해당 유저의 팩, 아이템
                    - 위시리스트/관심카테고리 등 연관 데이터
                    - 테스트 유저 계정
                    
                    동작
                    - 시드 유저가 없으면 아무 작업 없이 204 반환(멱등)
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/seed")
    public ResponseEntity<Void> deleteSeed() {
        seedDataUseCase.deleteSeed();
        return ResponseEntity.noContent().build();
    }
}
