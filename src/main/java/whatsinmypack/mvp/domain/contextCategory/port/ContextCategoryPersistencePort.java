package whatsinmypack.mvp.domain.contextCategory.port;

import whatsinmypack.mvp.domain.contextCategory.entity.ContextCategory;

public interface ContextCategoryPersistencePort {
    ContextCategory findByName(String name);
}
