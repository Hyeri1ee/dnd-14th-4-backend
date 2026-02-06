package whatsinmypack.mvp.domain.contextCategory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import whatsinmypack.mvp.global.entity.BaseEntity;

@Entity
@Table(name = "context_categories")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContextCategory extends BaseEntity {

    @Column(length = 50)
    private String name;//카테고리 이름

    @Column(length = 50)
    private String detail;//카테고리 세부 사항
}
