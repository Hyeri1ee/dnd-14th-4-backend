package whatsinmypack.mvp.adapter.out.persistence.pack;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.user.entity.User;

public interface PackJpaRepository extends JpaRepository<Pack, Long> {
    // TODO: N+1 이슈 고려
    // TODO: 검색 결과 및 조회 join 네이티브 쿼리

    List<Pack> findByUser(User user);

    /**
     * step 1 : wishlist 카운팅 기반 정렬(무한스크롤 페이징 정렬)
     * @param q
     * @return
     */
    @Query("""
        select p.id
        from Pack p
        join p.packItems pi
        join pi.item i
        left join PackWishList w on w.pack = p
        where
            (
               p.title like %:q%
               or p.introduction like %:q%
               or i.title like %:q%
               or i.brand like %:q%
               or i.purchase like %:q%
            )
            and (
               :cns is null
               or p.contextCategory.name in :cns
            )
        group by p.id
        order by count(w.id) desc
    """)
    Slice<Long> searchPackIdsOrderByWishlist(
            @Param("q") String q,
            @Param("cns") List<String> cns,
            Pageable pageable
    );

    /**
     * step 2 : pack 엔티티 + 연관관계 로딩
     * @param ids
     * @return
     */
    @Query("""
        select distinct p
        from Pack p
        left join fetch p.packItems pi
        left join fetch pi.item
        where p.id in :ids
    """)
    List<Pack> findWithItemsByIds(@Param("ids") List<Long> ids);
}
