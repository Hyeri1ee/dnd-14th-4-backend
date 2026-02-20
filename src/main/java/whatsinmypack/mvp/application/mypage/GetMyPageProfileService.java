package whatsinmypack.mvp.application.mypage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whatsinmypack.mvp.application.mypage.port.LoadMyPageProfilePort;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetMyPageProfileService implements GetMyPageProfileUseCase {

    private final LoadMyPageProfilePort loadMyPageProfilePort;

    @Override
    public MyPageProfile getMyPageProfile(Long userId) {
        return loadMyPageProfilePort.loadByUserId(userId);
    }
}
