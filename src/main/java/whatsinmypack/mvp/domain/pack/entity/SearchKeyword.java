package whatsinmypack.mvp.domain.pack.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import whatsinmypack.mvp.global.entity.BaseEntity;

@Entity
@Table(name = "search_keywords")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchKeyword extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String keyword;

    @Column(nullable = false)
    @Builder.Default
    private Long searchCount = 0L;

    public void increaseCount() {
        this.searchCount += 1;
    }
}
