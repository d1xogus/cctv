package cctv.Config;

import cctv.Service.OAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2Service oAuth2Service;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // csrf 보안 설정 사용 X
                .formLogin(form -> form.disable()) // 폼 로그인 사용 Xs

                .authorizeHttpRequests(auth -> auth // 요청에 인증 절차 필요
                        .requestMatchers("/","/oauth/**").permitAll()// 루트 경로는 인증 절차 생략
                        .requestMatchers("/oauth2", "/cleanguard/**").hasAuthority("ROLE_USER")
                        .anyRequest().authenticated() // 다른 모든 요청에 인증 필요a
                )
                .logout(logout -> logout // 로그아웃 설정
                        .logoutUrl("/logout") // 로그아웃 요청 URL (기본값: /logout)
                        .logoutSuccessUrl("/") // 로그아웃 성공 후 리다이렉트 경로
                        .invalidateHttpSession(true) // 세션 무효화
                        .deleteCookies("JSESSIONID") // JSESSIONID 쿠키 삭제
                        .clearAuthentication(true) // 인증 정보 삭제
                        .permitAll() // 로그아웃 요청은 인증 없이 접근 가능
                )
                .oauth2Login(oauth2 -> oauth2 // OAuth2를 통한 로그인 사용
                        .defaultSuccessUrl("/oauth/loginInfo", true) // 로그인 성공 시 이동할 URL 설정
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2Service) // 로그인 성공 시 사용자 서비스 로직 설정
                        )
                );

        return http.build();

    }
}