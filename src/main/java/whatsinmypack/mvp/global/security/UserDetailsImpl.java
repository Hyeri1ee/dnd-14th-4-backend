package whatsinmypack.mvp.global.security;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import whatsinmypack.mvp.domain.user.User;

@Getter
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails, OAuth2User {

    private final User user;
    private final Map<String, Object> attributes;

    // UserDetails 구현 메소드
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getName() {
        return user.getNickname();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // mvp에서는 모든 사용자 USER 권한, 추후 사용자 엔티티에 Role 필드 추가시 getter 기반 수정
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public @Nullable String getPassword() {
        return null; // OAuth2.0 기반이므로 패스워드 x
    }

    // OAuth2User 구현 메소드
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // 편의 메소드
    public Long getUserId() {
        return user.getId();
    }

    public String getProfileImage() {
        return user.getProfileImage();
    }
}
