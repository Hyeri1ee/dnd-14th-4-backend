package whatsinmypack.mvp.domain.item.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import whatsinmypack.mvp.domain.user.entity.User;
import whatsinmypack.mvp.global.entity.BaseEntity;

@Entity
@Table(name = "items")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item extends BaseEntity {


    @Column(length = 25)
    private String title;//아이템 타이틀

    @Column(length = 25)
    private String brand;//아이템 브랜드

    @Column(length = 100)
    private String review;//아이템 리뷰

    @Enumerated(EnumType.STRING)
    private Satisfaction satisfaction;//아이템 만족도

    @Enumerated(EnumType.STRING)
    private UsePeriod usePeriod;//아이템 사용기간

    @Column(length = 25)
    private String purchase;//아이템 구매처

    @ManyToOne(fetch = FetchType.LAZY)//N+1 쿼리 막는 용도
    @JoinColumn(name = "user_id")
    private User user;//user_id FK


}
