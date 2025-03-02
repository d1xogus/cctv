package cctv.Controller;

import cctv.Config.JwtTokenProvider;
import cctv.Entity.Member;
import cctv.Entity.RefreshToken;
import cctv.Repository.MemberRepository;
import cctv.Service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;
    private final MemberRepository memberRepository;
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @GetMapping("/auth/logout")
    public ResponseEntity<?> kakaoLogout(HttpServletRequest request, HttpServletResponse response) {
        log.info("🔹 [카카오 로그아웃] 요청 시작");

        // ✅ 카카오 로그아웃 URL
        String kakaoLogoutUrl = "https://kauth.kakao.com/oauth/logout?client_id=" + kakaoClientId
                + "&logout_redirect_uri=\t\n" +
                "http://3.36.174.53:8080/logout";

        log.info("🔹 [카카오 로그아웃] URL: {}", kakaoLogoutUrl);

        // ✅ Spring Security 로그아웃 처리
        request.getSession().invalidate(); // 세션 무효화
        SecurityContextHolder.clearContext(); // 인증 정보 삭제

        return ResponseEntity.ok().body(Map.of("logoutUrl", kakaoLogoutUrl));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader("Refresh-Token") String refreshTokenHeader) {
        if (refreshTokenHeader == null || !refreshTokenHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("null or 형식오류");
        }

        String refreshToken = refreshTokenHeader.substring(7);

        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) { // ✅ Refresh Token만 허용
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token값 오류");
        }

        // Refresh Token 검증
        RefreshToken storedToken = tokenService.getRefreshToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "검증 오류"));

        // memberId 가져오기
        Long memberId = storedToken.getMemberId();

        // 회원 정보 조회
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 새 Access Token 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(member);

        return ResponseEntity.ok(Map.of("access_token", newAccessToken));
    }
}
