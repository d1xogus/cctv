package cctv.Config;

import cctv.Handler.CustomAuthenticationEntryPoint;
import cctv.Handler.OAuth2LoginSuccessHandler;
import cctv.Repository.MemberRepository;
import cctv.Repository.RefreshTokenRepository;
import cctv.Service.OAuth2Service;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final OAuth2Service oAuth2Service;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http, MemberRepository memberRepository) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // csrf 보안 설정 사용 X
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 안 함
                .authorizeHttpRequests(auth -> auth // 요청에 인증 절차 필요
                        .requestMatchers("/","/main", "/login", "/oauth2/**", "/login/**", "/favicon.ico").permitAll()// 루트 경로는 인증 절차 생략
                        .requestMatchers("/login/oauth2/code/**").permitAll()
                        .requestMatchers("/cctv/**").authenticated()
                        .anyRequest().authenticated() // 다른 모든 요청에 인증 필요a
                )
                .formLogin(form -> form.disable())
                .oauth2Login(oauth2 -> oauth2 // OAuth2를 통한 로그인 사용
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2Service) // 로그인 성공 시 사용자 서비스 로직 설정
                        )
                        .successHandler(new OAuth2LoginSuccessHandler(jwtTokenProvider, refreshTokenRepository, memberRepository)) // OAuth2 로그인 성공 후 JWT 발급
                        .failureHandler((request, response, exception) -> { //  로그인 실패 시 오류 메시지 포함하여 리다이렉트
                            log.error("OAuth2 로그인 실패: {}", exception.getMessage());
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"error\": \"OAuth2 authentication failed\", \"message\": \"" + exception.getMessage() + "\"}");
                        })
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint) //  401 반환 설정
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class) // JWT 필터 추가

                .logout(logout -> logout // 로그아웃 설정
                        .logoutUrl("/logout") // 로그아웃 요청 URL (기본값: /logout)
                        .logoutSuccessHandler((request, response, authentication) -> {
                            log.info("🔹 [Spring Security 로그아웃 성공] 카카오 로그아웃으로 리다이렉트");

                            String kakaoLogoutUrl = "https://kauth.kakao.com/oauth/logout?client_id=" + kakaoClientId
                                    + "&logout_redirect_uri=http://localhost:3000/login";

                            response.sendRedirect(kakaoLogoutUrl);
                        })
                        .invalidateHttpSession(true) // 세션 무효화
                        .deleteCookies("JSESSIONID") // 쿠키 삭제
                        .clearAuthentication(true) // 인증 정보 삭제
                        .permitAll()
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();

    }
    // CORS 설정을 관리하는 Bean 등록
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://3.36.174.53:8080")); // 허용할 출처
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")); // 허용할 메서드
        configuration.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 쿠키 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 적용
        return source;
    }
}