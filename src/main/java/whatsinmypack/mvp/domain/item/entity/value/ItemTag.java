package whatsinmypack.mvp.domain.item.entity.value;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import whatsinmypack.mvp.domain.item.entity.Item;
import whatsinmypack.mvp.global.entity.BaseEntity;

@Entity
@Table(name = "item_tags")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemTag extends BaseEntity {

    private String tag;//아이템 태그

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public void setItem(Item item) {
        this.item = item;
    }
}
