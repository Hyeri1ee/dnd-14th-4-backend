package whatsinmypack.mvp.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import whatsinmypack.mvp.global.entity.BaseEntity;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User extends BaseEntity {
    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Column(nullable = false)
    private String nickname;

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
    private String profileImage;
}
