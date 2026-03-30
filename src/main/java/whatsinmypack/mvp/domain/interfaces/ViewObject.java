package whatsinmypack.mvp.domain.interfaces;

import java.time.LocalDateTime;

//'조회하다'의 인터페이스
//팩, 아이템 등을 모두 포괄 가능
public interface ViewObject {//이후 Pack및 아이템이 상속받는 형태로 적용예정
    Long id();
    Long ownerId();
    String title();
    LocalDateTime createdAt();

}
