package whatsinmypack.mvp.adapter.out.persistence.pack;

import org.springframework.data.jpa.repository.JpaRepository;
import whatsinmypack.mvp.domain.pack.entity.Pack;

public interface PackJpaRepository extends JpaRepository<Pack, Long> {
    // TODO: N+1 이슈 고려
    // TODO: 검색 결과 및 조회 join 네이티브 쿼리
}
