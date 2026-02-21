package whatsinmypack.mvp.application.mypage;

import java.util.List;

/**
 * 마이페이지 프로필 정보 (프로필 사진 URL + 관심 카테고리 이름 목록)
 */
public record MyPageProfile(
        String profileImageUrl,
        List<String> contextCategoryNames
) {
}
