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

@Tag(name = "TestData", description = "테스트용 시드 데이터 API")
@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestDataController {

    private final SeedDataUseCase seedDataUseCase;

    @Operation(summary = "시드 데이터 생성", description = "테스트용 user 2명(user_1@test.com, user_2@test.com), item 5개 생성. "
            + "기존 동일 이메일 유저가 있으면 해당 유저의 아이템 삭제 후 유저 삭제 후 재생성. "
            + "인증: Authorization 헤더에 Bearer user_1 또는 Bearer user_2 로 테스트 유저 인증 가능.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/seed")
    public ResponseEntity<Void> seed() {
        seedDataUseCase.seed();
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "시드 데이터 삭제", description = "테스트용 user 2명(user_1@test.com, user_2@test.com)과 해당 유저의 아이템 전부 삭제.")
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
