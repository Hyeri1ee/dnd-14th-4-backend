package whatsinmypack.mvp.presentation.exception;

import jakarta.annotation.Priority;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import whatsinmypack.mvp.presentation.response.ApiResponse;

@Slf4j
@Priority(Integer.MAX_VALUE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, List<String>> fieldErrors = new HashMap<>();

        e.getBindingResult()
                .getFieldErrors()
                .forEach(er -> fieldErrors
                        .computeIfAbsent(er.getField(), k -> new ArrayList<>())
                        .add(er.getDefaultMessage()));

        String message = fieldErrors.values().stream()
                .flatMap(List::stream)
                .findFirst()
                .orElse("잘못된 요청입니다");

        return ResponseEntity.badRequest().body(new ApiResponse(message));
    }
}
