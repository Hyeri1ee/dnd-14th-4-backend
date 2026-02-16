package whatsinmypack.mvp.adapter.in.web.pack.res;

import java.time.LocalDate;
import java.util.List;
import whatsinmypack.mvp.adapter.in.web.item.res.ItemCapsuleResponse;

public record PackDetailResponse(
        String user, // 작성자 이름
        String title, // 팩 제목
        LocalDate date, // 팩 작성 날짜
        String profileImage, // 작성자 프로필 이미지 경로
        String introduction, // 팩 소개
        String contextCategory, // 팩의 순간
        List<ItemCapsuleResponse> itemList // 팩을 구성하는 아이템들 개요(from Item to ItemCapsuleResponse)
) {
}
