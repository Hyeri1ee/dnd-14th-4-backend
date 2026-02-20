package whatsinmypack.mvp.application.testdata;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.application.testdata.port.SeedDataPort;

@Service
@RequiredArgsConstructor
public class SeedDataService implements SeedDataUseCase {

    private final SeedDataPort seedDataPort;

    @Override
    @Transactional
    public void seed() {
        seedDataPort.seed();
    }

    @Override
    @Transactional
    public void deleteSeed() {
        seedDataPort.deleteSeed();
    }
}
