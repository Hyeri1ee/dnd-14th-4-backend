package whatsinmypack.mvp.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Hello", description = "Hello World API")
@RestController
@RequestMapping("/api/hello")
public class HelloController {

    @Operation(summary = "테스트", description = "테스트 API")
    @GetMapping
    public ResponseEntity<Map<String, String>> dnd14_4th() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "DND 14기 4조 화이팅");
        
        return ResponseEntity.ok(response);
    }

}
