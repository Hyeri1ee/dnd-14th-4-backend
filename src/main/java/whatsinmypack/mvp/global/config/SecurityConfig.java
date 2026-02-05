package whatsinmypack.mvp.global.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import whatsinmypack.mvp.global.security.filter.JwtAuthenticationFilter;
import whatsinmypack.mvp.global.security.handler.CustomLogoutHandler;
import whatsinmypack.mvp.global.security.handler.JwtAccessDenyHandler;
import whatsinmypack.mvp.global.security.handler.JwtAuthenticationEntryPoint;
import whatsinmypack.mvp.global.security.handler.OAuth2FailureHandler;
import whatsinmypack.mvp.global.security.handler.OAuth2SuccessHandler;
import whatsinmypack.mvp.global.security.jwt.JwtTokenProvider;
import whatsinmypack.mvp.global.security.repository.HttpCookieOAuth2AuthorizationRequestRepository;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity(securedEnabled = true) // 메소드 보안 레벨(권한별 인가) 어노테이션
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${client.url}")
    private String clientUrl;

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final DefaultOAuth2UserService defaultOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final JwtAccessDenyHandler jwtAccessDenyHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomLogoutHandler customLogoutHandler;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    // Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

//    // OAuth2.0 Login Cookie Data Bean
//    @Bean
//    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
//        return new HttpCookieOAuth2AuthorizationRequestRepository();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable); // jwt 기반에서는 csrf 불필요
        http.cors(cors -> cors.configurationSource(corsConfigurationSource())); // 클라이언트 도메인 개방
        http.sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // 서버 세션 생성 방지

        http.formLogin(AbstractHttpConfigurer::disable); // 폼 로그인 방식 불필요
        http.logout(l -> l
                .logoutUrl("/api/v1/users/logout")
                .addLogoutHandler(customLogoutHandler)); // 로그아웃 핸들러 등록

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/hello").permitAll()
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/oauth2/**").permitAll()
                .anyRequest().authenticated());

        http.oauth2Login(o -> o
                .authorizationEndpoint(a -> a
                        .baseUri("/oauth2/authorization")
                        .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)) //TODO: 이 레포를 구현해서 쿠키 저장 방식으로 추가 구축해야 한다...!
                .redirectionEndpoint(e -> e.baseUri("/oauth2/callback/*"))
                .userInfoEndpoint(e -> e.userService(defaultOAuth2UserService))
                .successHandler(oAuth2SuccessHandler)
                .failureHandler(oAuth2FailureHandler)); // OAuth2.0 리다이렉팅 URL 및 핸들러 등록

        http.addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService, jwtAuthenticationEntryPoint),
                UsernamePasswordAuthenticationFilter.class); // jwtAuthenticationFilter 추가

        // 등록만 해서는 자동 캐치가 안되고, 커스텀 필터가 앞서기 때문에 직접 의존성 주입이 필요하다
        // 로그인 필터는 자동으로 가장 뒤로 가기 때문에 엔트리포인트가 인증 예외 캐치가 가능했던 것
        http.exceptionHandling(e -> e
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDenyHandler)); // 인증 예외 및 인가 예외 핸들러 등록

        return http.build();
    }

    // CORS Configuration
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList(clientUrl));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
