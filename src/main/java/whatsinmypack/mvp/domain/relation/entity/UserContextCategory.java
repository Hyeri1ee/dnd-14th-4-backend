package whatsinmypack.mvp.domain.relation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import whatsinmypack.mvp.domain.contextCategory.entity.ContextCategory;
import whatsinmypack.mvp.domain.user.entity.User;
import whatsinmypack.mvp.global.entity.BaseEntity;

@Entity
@Table(name = "user_context_categories")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserContextCategory extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "context_category_id")
    private ContextCategory contextCategory;
}