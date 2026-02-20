package whatsinmypack.mvp.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import whatsinmypack.mvp.adapter.in.web.item.res.ItemSummaryResponse;
import whatsinmypack.mvp.application.wishlist.GetWishlistItemsUseCase;
import whatsinmypack.mvp.domain.item.entity.Item;
import whatsinmypack.mvp.global.security.user.UserDetailsImpl;

import java.util.List;

@Tag(name = "Wish", description = "위시리스트 관련 API")
@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final GetWishlistItemsUseCase getWishlistItemsUseCase;

    @Operation(summary = "위시리스트 아이템 조회", description = "로그인 유저가 위시리스트로 설정한(is_wishlist=true) 아이템만 item_id로 item 테이블에서 조회해 목록 반환. 응답 형식은 내 아이템 조회와 동일.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ItemSummaryResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @GetMapping("/items")
    public ResponseEntity<List<ItemSummaryResponse>> getWishlistItems(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<Item> items = getWishlistItemsUseCase.getItemsByUserId(userDetails.getUserId());
        return ResponseEntity.ok(items.stream().map(ItemSummaryResponse::from).toList());
    }
}
