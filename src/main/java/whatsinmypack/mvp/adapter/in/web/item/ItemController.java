package whatsinmypack.mvp.adapter.in.web.item;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import whatsinmypack.mvp.adapter.in.web.item.req.CreateItemRequest;
import whatsinmypack.mvp.adapter.in.web.item.req.UpdateItemRequest;
import whatsinmypack.mvp.adapter.in.web.item.res.CreateItemResponse;
import whatsinmypack.mvp.adapter.in.web.item.res.ItemSummaryResponse;
import whatsinmypack.mvp.adapter.in.web.item.res.UpdateItemResponse;
import whatsinmypack.mvp.application.item.create.CreateItemCommand;
import whatsinmypack.mvp.application.item.create.CreateItemUseCase;
import whatsinmypack.mvp.application.item.getlist.GetUserItemsUseCase;
import whatsinmypack.mvp.application.item.update.UpdateItemCommand;
import whatsinmypack.mvp.application.item.update.UpdateItemUseCase;
import whatsinmypack.mvp.application.wishlist.AddItemWishListUseCase;
import whatsinmypack.mvp.application.wishlist.RemoveItemWishListUseCase;
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
    private final UpdateItemUseCase updateItemUseCase;
    private final AddItemWishListUseCase addItemWishListUseCase;
    private final RemoveItemWishListUseCase removeItemWishListUseCase;

    @Operation(summary = "아이템 추가", description = "multipart/form-data: request(JSON) + reviewImages(이미지 파일, 선택, 최대 5개). request 파트는 Content-Type: application/json으로 전송")
    @PostMapping(value = "/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CreateItemResponse> createItem(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "아이템 생성 요청 (JSON)", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CreateItemRequest.class)))
            @RequestPart("request") @Valid CreateItemRequest request,
            @Parameter(description = "리뷰 이미지 파일 (선택, 최대 5개)")
            @RequestPart(value = "reviewImages", required = false) List<MultipartFile> reviewImages
    ) {
        CreateItemCommand command = new CreateItemCommand(
                userDetails.getUserId(),
                request.brandName(),
                request.productName(),
                request.satisfaction(),
                request.reviewText(),
                reviewImages,
                request.tags(),
                request.usePeriod(),
                request.purchaseLocation()
        );
        Item item = createItemUseCase.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(CreateItemResponse.from(item));
    }

    @Operation(summary = "아이템 수정", description = "multipart/form-data: request(JSON) + reviewImages(이미지 파일, 선택, 최대 5개). request 파트는 Content-Type: application/json으로 전송")
    @PatchMapping(value = "/{itemId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UpdateItemResponse> updateItem(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long itemId,
            @Parameter(description = "아이템 수정 요청 (JSON)", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UpdateItemRequest.class)))
            @RequestPart("request") @Valid UpdateItemRequest request,
            @Parameter(description = "리뷰 이미지 파일 (선택, 최대 5개)")
            @RequestPart(value = "reviewImages", required = false) List<MultipartFile> reviewImages
    ) {
        UpdateItemCommand command = new UpdateItemCommand(
                itemId,
                userDetails.getUserId(),
                request.brandName(),
                request.productName(),
                request.satisfaction(),
                request.reviewText(),
                reviewImages,
                request.tags(),
                request.usePeriod(),
                request.purchaseLocation()
        );

        Item item = updateItemUseCase.update(command);
        return ResponseEntity.ok(UpdateItemResponse.from(item));
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

    @Operation(summary = "아이템 위시리스트 추가", description = "해당 아이템을 위시리스트에 추가. item_wishlists에 (user_id, item_id) 행이 없으면 생성 후 is_wishlist=1, 있으면 is_wishlist=1로 갱신")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "아이템 없음")
    })
    @PostMapping("/{itemId}/wishlist")
    public ResponseEntity<Void> addWishlist(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long itemId
    ) {
        addItemWishListUseCase.add(userDetails.getUserId(), itemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "아이템 위시리스트 삭제", description = "해당 아이템을 위시리스트에서 제거. item_wishlists의 is_wishlist=0으로 갱신")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @DeleteMapping("/{itemId}/wishlist")
    public ResponseEntity<Void> removeWishlist(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long itemId
    ) {
        removeItemWishListUseCase.remove(userDetails.getUserId(), itemId);
        return ResponseEntity.noContent().build();
    }
}
