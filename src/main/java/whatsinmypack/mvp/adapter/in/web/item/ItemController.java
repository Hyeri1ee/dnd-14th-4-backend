package whatsinmypack.mvp.adapter.in.web.item;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import whatsinmypack.mvp.adapter.in.web.item.req.CreateItemRequest;
import whatsinmypack.mvp.adapter.in.web.item.res.CreateItemResponse;
import whatsinmypack.mvp.application.item.create.CreateItemCommand;
import whatsinmypack.mvp.application.item.create.CreateItemUseCase;
import whatsinmypack.mvp.domain.item.entity.Item;
import whatsinmypack.mvp.global.security.user.UserDetailsImpl;

@Tag(name = "Item", description = "아이템 관련 컨트롤러")
@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final CreateItemUseCase createItemUseCase;

    @Operation(summary = "아이템 추가", description = "브랜드/제품명/만족도를 입력하고, 리뷰/태그/사용기간/구매처는 선택 입력")
    @PostMapping("/new")
    public ResponseEntity<CreateItemResponse> createItem(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody CreateItemRequest request
    ) {
        CreateItemCommand command = new CreateItemCommand(
                userDetails.getUserId(),
                request.brandName(),
                request.productName(),
                request.satisfaction(),
                request.reviewText(),
                request.reviewImagePaths(),
                request.tags(),
                request.usePeriod(),
                request.purchaseLocation()
        );
        Item item = createItemUseCase.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(CreateItemResponse.from(item));
    }
}
