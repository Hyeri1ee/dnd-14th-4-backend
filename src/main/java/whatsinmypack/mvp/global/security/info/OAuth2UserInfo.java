package whatsinmypack.mvp.global.security.info;

public interface OAuth2UserInfo {
    String getProvider();
    String getEmail();
    String getProfileImage();
}
