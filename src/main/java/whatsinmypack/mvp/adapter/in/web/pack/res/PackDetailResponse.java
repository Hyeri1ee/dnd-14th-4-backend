package whatsinmypack.mvp.adapter.in.web.pack.res;

import java.time.LocalDate;
import java.util.List;
import whatsinmypack.mvp.adapter.in.web.item.res.ItemCapsuleResponse;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.relation.entity.PackItem;

public record PackDetailResponse(
        Long id, // 팩 id
        String user, // 작성자 이름
        String title, // 팩 제목
        LocalDate date, // 팩 작성 날짜
        String profileImage, // 작성자 프로필 이미지 경로
        String introduction, // 팩 소개
        String contextCategory, // 팩의 순간
        List<ItemCapsuleResponse> itemList // 팩을 구성하는 아이템들 개요(from Item to ItemCapsuleResponse)
) {
    public static PackDetailResponse from(Pack pack) {
        return new PackDetailResponse(
                pack.getId(),
                pack.getUser().getNickname(),
                pack.getTitle(),
                pack.getCreatedAt().toLocalDate(),
                pack.getUser().getProfileImage(),
                pack.getIntroduction(),
                pack.getContextCategory().getName(),
                pack.getPackItems().stream()
                        .map(PackItem::getItem)
                        .map(ItemCapsuleResponse::from)
                        .toList()
        );
    }
}
