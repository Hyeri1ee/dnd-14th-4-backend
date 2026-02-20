package whatsinmypack.mvp.adapter.out.persistence.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import whatsinmypack.mvp.domain.relation.entity.UserContextCategory;

import java.util.List;

public interface UserContextCategoryJpaRepository extends JpaRepository<UserContextCategory, Long> {

    @Query("select ucc from UserContextCategory ucc join fetch ucc.contextCategory where ucc.user.id = :userId")
    List<UserContextCategory> findByUserId(Long userId);

    void deleteByUser_Id(Long userId);
}
