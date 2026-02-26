package whatsinmypack.mvp.application.pack.delete;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import whatsinmypack.mvp.domain.pack.entity.Pack;
import whatsinmypack.mvp.domain.pack.port.PackPersistencePort;

@Service
@Transactional
@RequiredArgsConstructor
public class DeletePackService implements DeletePackUseCase {

    private final PackPersistencePort packPersistencePort;

    @Override
    public void delete(Long packId, Long userId) {
        Pack pack;
        try {
            pack = packPersistencePort.findById(packId);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "팩을 찾을 수 없습니다.");
        }

        Long ownerId = pack.getUser() != null ? pack.getUser().getId() : null;

        if (!userId.equals(ownerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 팩을 삭제할 권한이 없습니다.");
        }

        packPersistencePort.clearReferencesByPackId(packId);
        packPersistencePort.delete(pack);
    }
}
