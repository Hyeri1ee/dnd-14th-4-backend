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
@Table(name = "packs")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pack extends BaseEntity {

    @Column(length = 10)
    private String title;//팩 타이틀
}
