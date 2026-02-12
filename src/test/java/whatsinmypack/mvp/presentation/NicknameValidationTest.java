package whatsinmypack.mvp.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import whatsinmypack.mvp.application.UserProfileService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // 스프링 시큐리티 필터체인 무효화
public class NicknameValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    UserProfileService userProfileService;

//    @Test
//    @DisplayName("닉네임이 null이면 400과 검증 메시지를 반환한다")
//    void testIsNull() throws Exception {
//        String body = """
//        { }
//        """;
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/api/v1/users/nickname")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(body))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message")
//                        .value("닉네임은 필수 입력값입니다"));
//
//        verify(userProfileService, never())
//                .verifyAndUpdateNickname(any(), any());
//    }
//
//    @Test
//    @DisplayName("닉네임이 빈 문자열이면 400을 반환한다")
//    void testIsBlank() throws Exception {
//        String body = """
//        { "nickname": "" }
//        """;
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/api/v1/users/nickname")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(body)
//                        .with(csrf()))
//                .andDo(print())
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("message")
//                        .value("닉네임은 필수 입력값입니다"));
//
//        verify(userProfileService, never())
//                .verifyAndUpdateNickname(any(), any());
//    }
//
//    @Test
//    @DisplayName("닉네임이 공백만 있으면 400을 반환한다")
//    void testWhitespace() throws Exception {
//        String body = """
//        { "nickname": "   " }
//        """;
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/api/v1/users/nickname")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(body))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message")
//                        .value("닉네임은 필수 입력값입니다"));
//
//        verify(userProfileService, never())
//                .verifyAndUpdateNickname(any(), any());
//    }

    @Test
    @DisplayName("닉네임이 10자를 초과하면 400을 반환한다")
    void testOverLength() throws Exception {
        String body = """
        { "nickname": "12345678901" }
        """;

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/users/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("닉네임은 10자 이하로 입력해주세요"));

        verify(userProfileService, never())
                .verifyAndUpdateNickname(any(), any());
    }

    @Test
    @DisplayName("닉네임에 특수문자가 포함되면 400을 반환한다")
    void testSpecialCharacter() throws Exception {
        String body = """
        { "nickname": "닉네임!" }
        """;

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/users/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("한글, 영문자, 숫자만 사용할 수 있습니다"));

        verify(userProfileService, never())
                .verifyAndUpdateNickname(any(), any());
    }

    @Test
    @DisplayName("닉네임에 공백이 포함되면 400을 반환한다")
    void testContainsSpace() throws Exception {
        String body = """
        { "nickname": "닉 네임" }
        """;

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/users/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("한글, 영문자, 숫자만 사용할 수 있습니다"));

        verify(userProfileService, never())
                .verifyAndUpdateNickname(any(), any());
    }

    @Test
    @DisplayName("닉네임에 이모지가 포함되면 400을 반환한다")
    void testContainsEmoji() throws Exception {
        String body = """
        { "nickname": "닉네임😀" }
        """;

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/users/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("한글, 영문자, 숫자만 사용할 수 있습니다"));

        verify(userProfileService, never())
                .verifyAndUpdateNickname(any(), any());
    }
}
