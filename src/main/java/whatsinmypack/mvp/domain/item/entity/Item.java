package whatsinmypack.mvp.domain.item.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import whatsinmypack.mvp.domain.item.entity.value.ItemImage;
import whatsinmypack.mvp.domain.item.entity.value.ItemTag;
import whatsinmypack.mvp.domain.user.entity.User;
import whatsinmypack.mvp.global.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "items")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item extends BaseEntity {

    @Version
    private Long version;

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


    //ItemImage DB에서 자동 삭제
    @OneToMany(mappedBy = "item",
            cascade = CascadeType.ALL, //부모 영속성 작업 자식에 전파
            orphanRemoval = true) //부모 제거시 DB에서도 자식 삭제
    @Builder.Default
    private List<ItemImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemTag> tags = new ArrayList<>();

    //ItemImage 편의 메서드 : item에 item_image 추가
    public void addImage(ItemImage image) {
        images.add(image);
        image.setItem(this);
    }

    //ItemImage 편의 메서드 : item에 item_image 삭제
    public void removeImage(ItemImage image) {
        images.remove(image);
        image.setItem(null);
    }

    public void addTag(ItemTag tag) {
        tags.add(tag);
        tag.setItem(this);
    }

    public void update(String title, String brand, String review, Satisfaction satisfaction, UsePeriod usePeriod, String purchase) {
        this.title = title;
        this.brand = brand;
        this.review = review;
        this.satisfaction = satisfaction;
        this.usePeriod = usePeriod;
        this.purchase = purchase;
    }

    public void clearImages() {
        this.images.clear();
    }

    public void clearTags() {
        this.tags.clear();
    }

}
