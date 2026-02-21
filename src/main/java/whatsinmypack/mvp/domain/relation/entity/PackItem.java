package whatsinmypack.mvp.domain.relation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import whatsinmypack.mvp.domain.item.entity.Item;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.global.entity.BaseEntity;

/**
 * 하나의 아이템이 또다른 팩에도 할당(재사용)될 수 있다...!
 */
@Entity
@Table(name = "pack_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
public class PackItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pack_id", nullable = false)
    private Pack pack;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    public PackItem(Pack pack, Item item) {
        this.pack = pack;
        this.item = item;
    }
}
