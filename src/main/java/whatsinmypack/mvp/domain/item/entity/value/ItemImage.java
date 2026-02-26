package whatsinmypack.mvp.domain.item.entity.value;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import whatsinmypack.mvp.domain.item.entity.Item;
import whatsinmypack.mvp.global.entity.BaseEntity;

@Entity
@Table(name = "item_images")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemImage extends BaseEntity {

//    @Lob
    @Column(columnDefinition = "TEXT")
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    //Image 편의 메서드
    public void setItem(Item item) {
        this.item = item;
    }

    public static ItemImage fromPath(String path) {
        return ItemImage.builder()
                .path(path.trim())
                .build();
    }
}
