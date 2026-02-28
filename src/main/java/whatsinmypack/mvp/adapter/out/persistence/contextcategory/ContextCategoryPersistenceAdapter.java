package whatsinmypack.mvp.adapter.out.persistence.contextcategory;

import java.util.List;
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
        for (String candidate : resolveCandidates(name)) {
            ContextCategory contextCategory = repository.findByName(candidate).orElse(null);
            if (contextCategory != null) {
                return contextCategory;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 컨텍스트 카테고리입니다.");
    }

    private List<String> resolveCandidates(String name) {
        if (name == null || name.isBlank()) {
            return List.of("");
        }
        return switch (name.trim()) {
            case "운동/선택", "운동/건강", "운동/산책" -> List.of("운동/산책", "운동/건강", "운동/선택");
            case "여행/경험", "여행/문화", "여행/캠핑" -> List.of("여행/캠핑", "여행/문화", "여행/경험");
            default -> List.of(name.trim());
        };
    }
}
