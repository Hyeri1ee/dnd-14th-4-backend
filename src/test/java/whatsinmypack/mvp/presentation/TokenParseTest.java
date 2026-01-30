package whatsinmypack.mvp.presentation;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import whatsinmypack.mvp.domain.user.entity.AuthProvider;
import whatsinmypack.mvp.domain.user.entity.User;
import whatsinmypack.mvp.global.security.jwt.JwtTokenProvider;
import whatsinmypack.mvp.global.security.user.UserDetailsImpl;

@SpringBootTest
@AutoConfigureMockMvc
public class TokenParseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String accessToken;
    private final String email = "test@test.com";

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary // 테스트 환경에서 더 우선하려고
        public UserDetailsService mockUserDetailsService() {
            return new UserDetailsService() {
                @Override
                public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                    User mockUser = User.builder()
                            .email(username)
                            .nickname("테스트")
                            .authProvider(AuthProvider.KAKAO)
                            .profileImage("https://example.com/profile.jpg")
                            .build();

                    return new UserDetailsImpl(mockUser, null);
                }
            };
        }
    }

    @BeforeEach
    void setUp() {
        accessToken = jwtTokenProvider.createToken(email);
    }

    @Test
    @DisplayName("유효한 엑세스토큰 기반 인증 API에서는 정상 응답이 반환된다")
    void validAccessTokenTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/auth")
                .header("Authorization", accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("인증을 뚫어낸 DND 14기 4조 화이팅"));
    }

    @Test
    @DisplayName("유호하지 않은 엑세스토큰 기반 인증 API에서는 예외 응답이 반환된다")
    void invalidAccessTokenTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/auth")
                .header("Authorization", "INVALID_ACCESS_TOKEN"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("message").value("인증에 실패하였습니다."));
    }
}
