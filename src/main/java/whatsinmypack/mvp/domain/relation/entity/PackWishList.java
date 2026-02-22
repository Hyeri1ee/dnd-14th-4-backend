package whatsinmypack.mvp.domain.relation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.user.entity.User;
import whatsinmypack.mvp.global.entity.BaseEntity;

@Entity
@Table(name = "pack_wishlists",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"pack_id", "user_id"})
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackWishList extends BaseEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pack_id")
    private Pack pack;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_wishlist", nullable = false)
    @Builder.Default
    private Boolean isWishlist = true;

    public void setWishlist(boolean wishlist) {
        this.isWishlist = wishlist;
    }
}