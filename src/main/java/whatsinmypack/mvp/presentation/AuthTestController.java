package whatsinmypack.mvp.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import whatsinmypack.mvp.global.dto.ApiResponse;

@Tag(name = "Auth", description = "Authentication API")
@RestController
@RequestMapping("/api/auth")
public class AuthTestController {
    @Operation(summary = "인증 테스트", description = "인증 테스트 API(요청 헤더에 유효한 엑세스 토큰 요구)")
    @GetMapping
    public ResponseEntity<ApiResponse> test() {
        return ResponseEntity.ok(new ApiResponse("인증을 뚫어낸 DND 14기 4조 화이팅"));
    }
}
