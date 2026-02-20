package whatsinmypack.mvp.domain.relation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import whatsinmypack.mvp.domain.item.entity.Item;
import whatsinmypack.mvp.domain.user.entity.User;
import whatsinmypack.mvp.global.entity.BaseEntity;


@Entity
@Table(name = "item_wishlists",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"item_id", "user_id"})
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemWishList extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_wishlist", nullable = false)
    private Boolean isWishlist = true;

    public void setWishlist(boolean wishlist) {
        this.isWishlist = wishlist;
    }
}
