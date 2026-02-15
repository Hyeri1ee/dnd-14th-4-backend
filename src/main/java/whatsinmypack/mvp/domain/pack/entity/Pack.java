package whatsinmypack.mvp.domain.pack.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import whatsinmypack.mvp.domain.contextCategory.entity.ContextCategory;
import whatsinmypack.mvp.domain.item.entity.Item;
import whatsinmypack.mvp.domain.relation.entity.PackItem;
import whatsinmypack.mvp.domain.user.entity.User;
import whatsinmypack.mvp.global.entity.BaseEntity;

@Entity
@Table(name = "packs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pack extends BaseEntity {

    @Column(length = 10)
    private String title; //팩 타이틀

    @Column(length = 100)
    private String introduction; // 팩 소개

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // nullable : true -> 사용자 탈퇴해도 팩은 남겨두도록
    private User user; // 작성자

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "context_category_id", nullable = false)
    private ContextCategory contextCategory; // 순간

    @OneToMany(mappedBy = "pack")
    @Builder.Default
    private List<PackItem> packItems = new ArrayList<>();
}
