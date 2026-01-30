package whatsinmypack.mvp.presentation.exception;

import jakarta.annotation.Priority;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import whatsinmypack.mvp.presentation.response.ApiResponse;

@Slf4j
@Priority(Integer.MAX_VALUE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse handleValidationExceptions(MethodArgumentNotValidException e) {
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

        return new ApiResponse(message);
    }
}
