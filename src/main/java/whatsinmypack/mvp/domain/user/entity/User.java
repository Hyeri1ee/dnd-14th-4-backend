package whatsinmypack.mvp.domain.user.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import whatsinmypack.mvp.domain.item.entity.Item;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.global.entity.BaseEntity;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Column(unique = true)
    private String nickname;

    @Column(unique = true)
    private Long kakaoId; // 향후 별개의 테이블 분리를 생각해야 될까?

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @Column
    @Enumerated(EnumType.STRING)
    private Occupation occupation;

    @Column
    @Enumerated(EnumType.STRING)
    private AgeGroup ageGroup;

    @Column(length = 500)
    private String profileImage; // 초기 생성 때는 하드코딩(디폴트 프로필 이미지) 적용

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Pack> packs = new ArrayList<>(); // User가 삭제돼도 Pack을 삭제시키지 않는다

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Item> items = new ArrayList<>(); // User가 삭제돼도 Item을 삭제시키지 않는다

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    // User 도메인 주도형 pack 관리(생성)
    public void addPack(Pack pack) {
        packs.add(pack);
        pack.setUser(this);
    }

    // User 도메인 주도형 pack 관리(삭제)
    public void removePack(Pack pack) {
        packs.remove(pack);
        pack.setUser(null);
    }

    // User 도메인 주도형 item 관리(생성)
    public void addItem(Item item) {
        items.add(item);
        item.setUser(this);
    }

    // User 도메인 주도형 item 관리(삭제)
    public void removeItem(Item item) {
        items.remove(item);
        item.setUser(null);
    }
}
