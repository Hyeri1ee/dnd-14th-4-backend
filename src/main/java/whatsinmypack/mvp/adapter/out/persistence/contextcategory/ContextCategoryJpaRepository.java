package whatsinmypack.mvp.adapter.out.persistence.contextcategory;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import whatsinmypack.mvp.domain.contextCategory.entity.ContextCategory;

public interface ContextCategoryJpaRepository extends JpaRepository<ContextCategory, Long> {
    Optional<ContextCategory> findByName(String name);
}
