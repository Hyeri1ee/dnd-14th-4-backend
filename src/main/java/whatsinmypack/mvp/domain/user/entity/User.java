package whatsinmypack.mvp.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import whatsinmypack.mvp.global.entity.BaseEntity;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Column(unique = true)
    private String nickname;

    @Column(unique = true)
    private Long kakaoId; // 향후 별개의 테이블 분리를 생각해야 될까?

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @Column
    @Enumerated(EnumType.STRING)
    private Occupation occupation;

    @Column
    @Enumerated(EnumType.STRING)
    private AgeGroup ageGroup;

    @Column(length = 500)
    private String profileImage; // 초기 생성 때는 하드코딩(디폴트 프로필 이미지) 적용

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfile(String nickname, Gender gender, AgeGroup ageGroup, String profileImage) {
        this.nickname = nickname;
        this.gender = gender;
        this.ageGroup = ageGroup;
        this.profileImage = profileImage;
    }
}
