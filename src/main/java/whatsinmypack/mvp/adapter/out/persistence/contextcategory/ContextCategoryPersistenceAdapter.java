package whatsinmypack.mvp.adapter.out.persistence.contextcategory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import whatsinmypack.mvp.domain.contextCategory.entity.ContextCategory;
import whatsinmypack.mvp.domain.contextCategory.port.ContextCategoryPersistencePort;

@Component
@RequiredArgsConstructor
public class ContextCategoryPersistenceAdapter implements ContextCategoryPersistencePort {

    private final ContextCategoryJpaRepository repository;

    @Override
    public ContextCategory findByName(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 컨텍스트 카테고리입니다."));
    }
}
