package whatsinmypack.mvp.application.mypage.port;

import whatsinmypack.mvp.application.mypage.MyPageProfile;

public interface LoadMyPageProfilePort {

    MyPageProfile loadByUserId(Long userId);
}
