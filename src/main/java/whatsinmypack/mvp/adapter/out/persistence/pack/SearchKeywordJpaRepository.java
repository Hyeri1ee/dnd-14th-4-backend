package whatsinmypack.mvp.adapter.out.persistence.pack;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import whatsinmypack.mvp.domain.pack.entity.SearchKeyword;

public interface SearchKeywordJpaRepository extends JpaRepository<SearchKeyword, Long> {

    Optional<SearchKeyword> findByKeyword(String keyword);

    @Query("""
        select sk.keyword
        from SearchKeyword sk
        order by sk.searchCount desc, sk.updatedAt desc
    """)
    List<String> findTopKeywords(org.springframework.data.domain.Pageable pageable);
}
