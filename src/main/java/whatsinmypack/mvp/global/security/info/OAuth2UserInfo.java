package whatsinmypack.mvp.global.security.info;

import whatsinmypack.mvp.domain.user.entity.AuthProvider;

public interface OAuth2UserInfo {
    AuthProvider getProvider();
    String getEmail();
    Long getId();
}
