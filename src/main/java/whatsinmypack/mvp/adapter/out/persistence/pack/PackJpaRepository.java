package whatsinmypack.mvp.adapter.out.persistence.pack;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.user.entity.User;

public interface PackJpaRepository extends JpaRepository<Pack, Long> {
    // TODO: N+1 이슈 고려
    // TODO: 검색 결과 및 조회 join 네이티브 쿼리

    List<Pack> findByUser(User user);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Pack p set p.user = null where p.user.id = :userId")
    void clearUserReference(@Param("userId") Long userId);

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
        left join PackWishList w on w.pack = p and w.isWishlist = true
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

    @Query("""
        select p 
        from Pack p
        left join fetch p.packItems pi
        left join fetch pi.item
        where p.id = :packId
    """)
    Optional<Pack> findByIdWithItems(@Param("packId") Long packId);

    /**
     * 메인 팩 추천 화면 -> 아이템 이미지가 필요할 것 같아서 페치 조인
     * @param contextId
     * @param pageable
     * @return
     */
    @Query("""
        select distinct p
        from Pack p
        left join fetch p.packItems pi
        left join PackWishList w on w.pack = p and w.isWishlist = true
        where p.contextCategory.id = :contextId
        group by p
        order by count(w.id) desc
    """)
    List<Pack> findTop10WithItemsByContextCategoryId(@Param("contextId") Long contextId, Pageable pageable);

    // step 1 : 팩아이디 정렬 및 리미트 카운팅 조회 -> step 2는 위의 정의된 메소드 활용
    @Query("""
        select p.id
        from Pack p
        left join PackWishList w on w.pack = p and w.isWishlist = true
        where p.contextCategory.id = :contextId
        group by p.id
        order by count(w.id) desc
    """)
    List<Long> findTopPackIdsByContextCategory(
            @Param("contextId") Long contextId,
            Pageable pageable
    );
}
