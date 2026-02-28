package whatsinmypack.mvp.adapter.out.persistence.accesslog;

import org.springframework.data.jpa.repository.JpaRepository;
import whatsinmypack.mvp.domain.accesslog.entity.RequestAccessLog;

public interface RequestAccessLogJpaRepository extends JpaRepository<RequestAccessLog, Long> {
}
