package cctv.Config;

import cctv.Handler.OAuth2LoginSuccessHandler;
import cctv.Repository.MemberRepository;
import cctv.Repository.RefreshTokenRepository;
import cctv.Service.OAuth2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final OAuth2Service oAuth2Service;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http, MemberRepository memberRepository) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // csrf 보안 설정 사용 X
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 안 함
                .authorizeHttpRequests(auth -> auth // 요청에 인증 절차 필요
                        .requestMatchers("/","/main", "/oauth2/**", "/login").permitAll()// 루트 경로는 인증 절차 생략
                        .requestMatchers("/cctv/**").authenticated()
                        .anyRequest().authenticated() // 다른 모든 요청에 인증 필요a
                )
                .oauth2Login(oauth2 -> oauth2 // OAuth2를 통한 로그인 사용
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2Service) // 로그인 성공 시 사용자 서비스 로직 설정
                        )
                        .successHandler(new OAuth2LoginSuccessHandler(jwtTokenProvider, refreshTokenRepository, memberRepository)) // OAuth2 로그인 성공 후 JWT 발급
                        .failureHandler((request, response, exception) -> { // ✅ 로그인 실패 시 오류 메시지 포함하여 리다이렉트
                            log.error("OAuth2 로그인 실패: {}", exception.getMessage());
                            response.sendRedirect("http://localhost:3000/login?error=" + exception.getMessage());
                        })
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class) // JWT 필터 추가

                .logout(logout -> logout // 로그아웃 설정
                        .logoutUrl("/logout") // 로그아웃 요청 URL (기본값: /logout)
                        .logoutSuccessUrl("/") // 로그아웃 성공 후 리다이렉트 경로
                        .invalidateHttpSession(true) // 세션 무효화
                        .deleteCookies("JSESSIONID") // JSESSIONID 쿠키 삭제
                        .clearAuthentication(true) // 인증 정보 삭제
                        .permitAll() // 로그아웃 요청은 인증 없이 접근 가능
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