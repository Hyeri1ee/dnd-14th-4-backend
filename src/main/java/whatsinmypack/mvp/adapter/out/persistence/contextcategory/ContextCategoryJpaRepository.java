package whatsinmypack.mvp.adapter.out.persistence.contextcategory;

import org.springframework.data.jpa.repository.JpaRepository;
import whatsinmypack.mvp.domain.contextCategory.entity.ContextCategory;

public interface ContextCategoryJpaRepository extends JpaRepository<ContextCategory, Long> {
}
