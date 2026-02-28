package whatsinmypack.mvp.domain.accesslog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import whatsinmypack.mvp.global.entity.BaseEntity;

@Entity
@Table(name = "request_access_logs")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestAccessLog extends BaseEntity {

    @Column(nullable = false, length = 500)
    private String requestUrl;

    @Column
    private Long userId;

    @Column(nullable = false)
    private Integer statusCode;

    @Column(nullable = false)
    private LocalDateTime requestedAt;
}
