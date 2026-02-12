package whatsinmypack.mvp.adapter.in.web.item;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import whatsinmypack.mvp.adapter.in.web.item.req.CreateItemRequest;
import whatsinmypack.mvp.adapter.in.web.item.res.CreateItemResponse;
import whatsinmypack.mvp.adapter.in.web.item.res.ItemSummaryResponse;
import whatsinmypack.mvp.application.item.create.CreateItemCommand;
import whatsinmypack.mvp.application.item.create.CreateItemUseCase;
import whatsinmypack.mvp.application.item.getlist.GetUserItemsUseCase;
import whatsinmypack.mvp.domain.item.entity.Item;
import whatsinmypack.mvp.global.security.user.UserDetailsImpl;
import java.util.List;


@Tag(name = "Item", description = "아이템 관련 컨트롤러")
@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final CreateItemUseCase createItemUseCase;
    private final GetUserItemsUseCase getUserItemsUseCase;

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

    //ItemController - (web) -> ItemSumaryResponse
    //ItemController - (db) -> 유스케이스(GetUserItemsUseCase) + 유스케이스 구현체(GetUserItemsService)-> 도메인과 db 연결 포트(ItemPersistencePort) + 포트 구현체(ItemPersistenceAdapter) -> db에 저장
    @Operation(summary = "내 아이템 전체 조회", description = "로그인한 유저의 인생 아이템 목록을 최신순으로 조회")
    @GetMapping
    public ResponseEntity<List<ItemSummaryResponse>> getMyItems(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<Item> items = getUserItemsUseCase.getItemsByUserId(userDetails.getUserId());
        List<ItemSummaryResponse> response = items.stream()
                .map(ItemSummaryResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }
}
